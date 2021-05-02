package inc.sad.conf

case class SparkConfig(masterUrl: String,
                       sparkConfigMap: Map[String, String])

final case class Config(sparkConfig: SparkConfig = null,
                        inputUrl: String = null,
                        outputUrl: String = null)
