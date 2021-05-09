package inc.sad.services

import inc.sad.conf.{AppConfig, Config, SparkConfig}
import org.apache.spark.internal.Logging
import pureconfig.ConfigSource
import scopt.OptionParser

import scala.reflect.io.File

object ConfigService extends Logging {

  val ConfigFileProperty = "config-file"
  val SparkConfigProperty = "spark-config"

  final case class ConfigArgs(configFile: String = null,
                              sparkConfigFile: String = null)

  def loadConfiguration(args: Array[String])(implicit appName: String): Config = {

    val parser: OptionParser[ConfigArgs] =
      new OptionParser[ConfigArgs](appName) {

        head(appName)
        opt[String](ConfigFileProperty)
          .required
          .action((i, a) => a.copy(configFile = i))
          .valueName(s"<$ConfigFileProperty>")
          .text(s"%s application configuration file".format(appName))
        opt[String](SparkConfigProperty)
          .required
          .action((s, a) => a.copy(sparkConfigFile = s))
          .valueName(s"<$SparkConfigProperty>")
          .text(s"%s spark configuration file".format(appName))

      }
    parser.parse(args, ConfigArgs()) match {
      case None => throw new Exception("Failed parsing arguments")
      case Some(settings) => loadConfiguration(settings.configFile, settings.sparkConfigFile)
    }
  }

  private def loadConfiguration(configFilePath: String, sparkConfigFilePath: String): Config = {

    import pureconfig.generic.auto._

    val configFile = File(configFilePath)
    val sparkConfigFile = File(sparkConfigFilePath)

    if (!configFile.exists) {
      logError(s"Application config file doesn't exists `${sparkConfigFile.name}`")
      throw new Exception(s"Application config file doesn't exists `${sparkConfigFile.name}`")
    }

    if (!sparkConfigFile.exists) {
      logError(s"Spark config file doesn't exists `${sparkConfigFile.name}`")
      throw new Exception(s"Spark config file doesn't exists `${sparkConfigFile.name}`")
    }

    val appConfig = ConfigSource.file(configFile.path).load[AppConfig]  match {
      case Right(v) => v
      case Left(f) => throw new IllegalArgumentException(f.prettyPrint())
    }

    val sparkConfig = ConfigSource.file(sparkConfigFile.path).load[SparkConfig]  match {
      case Right(v) => v
      case Left(f) => throw new IllegalArgumentException(f.prettyPrint())
    }

    Config(appConfig, sparkConfig)
  }

}
