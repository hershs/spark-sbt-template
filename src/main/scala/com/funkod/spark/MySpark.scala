package com.funkod.spark

import java.net.URI

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.IOUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.slf4j.{Logger, LoggerFactory}
import pureconfig.generic.ProductHint
import pureconfig.generic.auto.exportReader
import pureconfig.{CamelCase, ConfigFieldMapping}


object MySpark {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val DATE_FORMAT = "yyyy-MM-dd"
  val dateParser: DateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT).withZoneUTC()

  def getConfiguration(args: Array[String]): MySparkConf = {
    if (args.length != 2) {
      throw new Exception("2 arguments expected!")
    }
    val confPath = args(0)
    logger.info("Loading config file " + confPath)
    // load configuration from s3\local as string
    val fs = FileSystem.get(URI.create(confPath), new Configuration())
    val inputStream = fs.open(new Path(confPath))
    val fileContent = IOUtils.toString(inputStream)
    inputStream.close()
    logger.info(s"Configuration ->\n$fileContent\n<-")

    implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

    val typesafeConfig: Config = ConfigFactory.parseString(fileContent)
    val config = pureconfig.loadConfigOrThrow[MySparkConf](typesafeConfig)
    config.runDateStr = args(1)
    config
  }

  def main(args: Array[String]): Unit = {
    val spark = getSparkSession
    val conf = getConfiguration(args)
    val start = System.currentTimeMillis()
    spark
      .read
      .parquet(conf.inputPath + "date=" + conf.runDateStr)
      // your stuff
      .write
      .parquet(conf.outputPath + "date=" + conf.runDateStr)
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
