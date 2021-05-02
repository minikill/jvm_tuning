package inc.sad.services

import inc.sad.conf.{Config, SparkConfig}
import org.apache.spark.internal.Logging
import pureconfig.ConfigSource
import scopt.OptionParser

import scala.reflect.io.File

object ConfigService extends Logging {

  val InputUrl = "input-url"
  val OutputUrl = "output-url"
  val SparkConfigProperty = "spark-config"

  final case class ConfigArgs(inputUrl: String = null,
                              outputUrl: String = null,
                              sparkConfigFile: String = null)

  def loadConfiguration(args: Array[String])(implicit appName: String): Config = {

    val parser: OptionParser[ConfigArgs] =
      new OptionParser[ConfigArgs](appName) {

        head(appName)
        opt[String](InputUrl)
          .required
          .action((i, a) => a.copy(inputUrl = i))
          .valueName(s"<$InputUrl>")
          .text(s"%s input URL".format(appName))
        opt[String](OutputUrl)
          .required
          .action((o, a) => a.copy(outputUrl = o))
          .valueName(s"<$OutputUrl>")
          .text(s"%s output URL".format(appName))
        opt[String](SparkConfigProperty)
          .required
          .action((s, a) => a.copy(sparkConfigFile = s))
          .valueName(s"<$SparkConfigProperty>")
          .text(s"%s spark configuration file".format(appName))

      }
    parser.parse(args, ConfigArgs()) match {
      case None => throw new Exception("Failed parsing arguments")
      case Some(settings) => loadConfiguration(settings.inputUrl, settings.outputUrl, settings.sparkConfigFile)
    }
  }

  private def loadConfiguration(inputUrl: String, outputUrl: String, sparkConfigFilePath: String): Config = {

    import pureconfig.generic.auto._

    val sparkConfigFile = File(sparkConfigFilePath)

    if (!sparkConfigFile.exists) {
      logError(s"Spark config file doesn't exists `${sparkConfigFile.name}`")
      throw new Exception(s"Spark config file doesn't exists `${sparkConfigFile.name}`")
    }

    val sparkConfig = ConfigSource.file(sparkConfigFile.path).load[SparkConfig]  match {
      case Right(v) => v
      case Left(f) => throw new IllegalArgumentException(f.prettyPrint())
    }

    Config(sparkConfig, inputUrl, outputUrl)
  }

}
