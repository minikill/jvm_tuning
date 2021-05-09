package inc.sad.app

import inc.sad.services.{ConfigService, SparkService}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.functions.count
import org.apache.spark.storage.StorageLevel

object BatchProcessingApplication extends App with Logging {

  implicit val appName: String = "BatchProcessingApplication"

  val config = ConfigService.loadConfiguration(args)
  val spark = SparkService("batchJob", config.sparkConfig)

  import spark.implicits._

  logInfo(s"Job initialized")

  val df = spark
    .read
    .textFile(config.appConfig.inputUrl)
    .flatMap(line => line.split(" "))
    .persist(StorageLevel.OFF_HEAP)

  df.groupBy("value")
    .agg(count("value").alias("count"))
    .coalesce(10)
    .write
    .parquet(config.appConfig.outputUrl)

  logInfo(s"Job successfully finished")

}
