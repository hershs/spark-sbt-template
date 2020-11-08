import sbtassembly.AssemblyKeys.assembly

name := "spark-sbt-template"
organization := "com.funkod.spark"

crossScalaVersions := Seq("2.11.12", "2.12.12")
releaseIgnoreUntrackedFiles := true
parallelExecution in Test := false
fork in Test := true
publishMavenStyle := true
releaseCrossBuild := true // publish all scala versions in release plugin
assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${version.value}-assembly.jar"
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
// when running "clean assembly 'release with-defaults'" create assembly artifact also
addArtifact(artifact in(Compile, assembly), assembly)

lazy val sparkVersion = SettingKey[String]("sparkVersion")

sparkVersion := (scalaBinaryVersion.value match {
  case "2.11" => "2.4.5"
  case "2.12" => "3.0.0"
})

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.10.1",
  "org.apache.spark" %% "spark-core" % sparkVersion.value % Provided,
  "org.apache.spark" %% "spark-sql" % sparkVersion.value % Provided,
  "org.apache.hadoop" % "hadoop-aws" % "2.8.3" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
publishTo := {
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at "http://<snapshot repository>")
  else
    Some("releases" at "http://<release repository>")
}
