package inc.sad.services

import inc.sad.conf.SparkConfig
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

object SparkService extends Logging {

  def apply(appName: String, sparkConfig: SparkConfig): SparkSession = {

    logInfo(s"Spark initialization")

    val conf = new SparkConf()
    conf.setAll(sparkConfig.sparkConfigMap)

    SparkSession.builder()
      .config(conf)
      .appName(appName)
      .master(sparkConfig.masterUrl)
      .getOrCreate()
  }

}