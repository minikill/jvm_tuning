package inc.sad.app

import inc.sad.services.{ConfigService, SparkService}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.functions.count

object BatchProcessingApplication extends App with Logging {

  implicit val appName: String = "BatchProcessingApplication"

  val config = ConfigService.loadConfiguration(args)
  val spark = SparkService("batchJob", config.sparkConfig)

  import spark.implicits._

  logInfo(s"Job initialized")

  val df = spark
    .read
    .textFile(config.inputUrl)
    .flatMap(line => line.split(" "))
    .groupBy("value")
    .agg(count("value").alias("count"))
    .write
    .parquet(config.outputUrl)

  logInfo(s"Job successfully finished")

}
