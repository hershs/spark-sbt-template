package com.funkod.spark

import java.io.File
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import pureconfig.generic.ProductHint
import pureconfig.generic.auto.exportWriter
import pureconfig.{CamelCase, ConfigFieldMapping}

class MySparkTest extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {
  val TEST_DIR: String = "target/test_data/"

  override def beforeAll(): Unit = {
  }

  before {
    FileUtils.deleteDirectory(new File(TEST_DIR))
  }

  after {
  }

  test("test main") {
    val spark = getTestSparkSession
    import spark.implicits._
    // create test data
    Seq((1, "a"), (2, "b")).toDF("id", "name").write.parquet(TEST_DIR + "input/date=2020-08-11")

    // execute main
    MySpark.main(Array(createTempConf(defaultConf()), "2020-08-11"))

    // test output
    val res = getTestSparkSession
      .read
      .parquet(TEST_DIR + "output/date=2020-08-11")
      .collect()
    // test schema
    val schemaStr = res.head.schema.toString()
    assert(schemaStr === "StructType(StructField(id,IntegerType,true), StructField(name,StringType,true))")
    // test data
    val data = res
      .map(_.toString)
      .toList
      .sorted
    assert(data(0) === "[1,a]")
    assert(data(1) === "[2,b]")
  }

  def getTestSparkSession: SparkSession = {
    SparkSession
      .builder()
      .master("local[*]")
      .appName("getTestSparkSession")
      .config("spark.sql.shuffle.partitions", 3)
      .getOrCreate()
  }

  def defaultConf(inputPath: String = TEST_DIR + "input/",
                  outputPath: String = TEST_DIR + "output/"): MySparkConf = {
    MySparkConf(inputPath, outputPath)
  }

  def createTempConf(conf: MySparkConf): String = {
    implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

    val confFile = File.createTempFile("snap_", ".conf", new File("target"))
    confFile.deleteOnExit()
    val confFilePath = confFile.getPath.replace('\\', '/')
    pureconfig.saveConfigAsPropertyFile(conf, Paths.get(confFilePath), overrideOutputPath = true)
    confFilePath
  }
}
