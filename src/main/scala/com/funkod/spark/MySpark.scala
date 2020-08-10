package com.funkod.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.slf4j.{Logger, LoggerFactory}

object MySpark {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val DATE_FORMAT = "yyyy-MM-dd"
  val dateParser: DateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT).withZoneUTC()

  def getConfiguration(args: Array[String]): MySparkConf = {
    logger.info("Started With Arguments [" + args.mkString(",") + "]")
    if (args.length != 3) {
      throw new Exception("3 arguments expected: inputPath, outputPath, date!")
    }
    MySparkConf(args(0) + "/date=" + args(2), args(1) + "/date=" + args(2))
  }

  def main(args: Array[String]): Unit = {
    val spark = getSparkSession
    val conf = getConfiguration(args)
    import spark.implicits._
    val start = System.currentTimeMillis()
    spark
      .read
      .parquet(conf.inputPath)
      // your stuff
      .write
      .parquet(conf.outputPath)
    spark.close()
    logger.info(s"Process finished in " + (System.currentTimeMillis() - start) + "ms")
  }

  def getSparkSession: SparkSession = {
    val sparkConf = new SparkConf()
    val spark: SparkSession = SparkSession
      .builder()
      .master(sparkConf.get("spark.master", "local[*]"))
      .appName(sparkConf.get("spark.app.name", "MySpark"))
      .getOrCreate()
    spark
  }

}
