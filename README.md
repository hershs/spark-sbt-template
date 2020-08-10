# spark-sbt-template
This is my version of simple sbt spark project

## Features
* Include assembly plugin
* Include release plugin
* Include test of main function, 100% test coverage 
* Support multiple scala versions, see crossScalaVersions
* Generates assembly jar of few kBytes
* Build assembly jar locally `sbt assembly`
* Build and publish snapshot version `sbt assembly publish`
* Build and publish release version `sbt assembly "release with-defaults"`
* Build all scala versions - add plus to assembly command: `+assembly`