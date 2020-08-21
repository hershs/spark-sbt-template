package com.intuin.payment.spark

import java.util.concurrent.TimeUnit

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructType}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.slf4j.{Logger, LoggerFactory}

object PaymentSpark {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark = getSparkSession
    import spark.implicits._
    val start = System.currentTimeMillis()
    val schema = new StructType()
      .add("amount", DoubleType)
      .add("currency", StringType)
      .add("userId", StringType)
      .add("payeeId", StringType)
      .add("paymentMethodId", StringType)

    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "topic1")
      .load()
      .selectExpr("CAST(value AS STRING)")
      .as("m")
      .select(from_json($"m.value",schema).as("d"))
      .select("d.*")
      .writeStream
      .format("console")
      .option("truncate", false)
      .option("numRows", 1000)
      //      .option("checkpointLocation", "my_checkpont")
      .outputMode(OutputMode.Append())
      .trigger(Trigger.ProcessingTime(100, TimeUnit.MILLISECONDS))
      .start()
      .awaitTermination()
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
