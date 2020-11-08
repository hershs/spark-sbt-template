package com.funkod.spark

case class MySparkConf(inputPath: String,
                       outputPath: String,
                       var runDateStr: String = "" // filled by additional argument
                      ) {
  require(inputPath.last == '/', "neustarMaidPath must end with /")
  require(outputPath.last == '/', "neustarMaidPath must end with /")
}
