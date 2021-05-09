package inc.sad.conf

case class SparkConfig(masterUrl: String,
                       sparkConfigMap: Map[String, String])

case class AppConfig(inputUrl: String = null,
                     outputUrl: String = null)

final case class Config(appConfig: AppConfig = null,
                        sparkConfig: SparkConfig = null)
