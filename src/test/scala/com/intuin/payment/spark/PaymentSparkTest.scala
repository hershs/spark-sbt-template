package com.intuin.payment.spark

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}

class PaymentSparkTest extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {
  val TEST_DIR: String = "target/test_data/"

  override def beforeAll(): Unit = {
  }

  before {
    FileUtils.deleteDirectory(new File(TEST_DIR))
  }

  after {
  }

  test("test main") {
    PaymentSpark.main(Array(""))

  }

  def getTestSparkSession: SparkSession = {
    SparkSession
      .builder()
      .master("local[*]")
      .appName("getTestSparkSession")
      .config("spark.sql.shuffle.partitions", 3)
      .getOrCreate()
  }

}
