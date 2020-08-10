import sbtassembly.AssemblyKeys.assembly

name := "spark-sbt-template"
organization := "com.funkod.spark"

crossScalaVersions := Seq("2.11.12", "2.12.12")
releaseIgnoreUntrackedFiles := true
parallelExecution in Test := false
fork in Test := true
publishMavenStyle := true
assemblyJarName in assembly := s"${name.value}-assembly_${scalaBinaryVersion.value}-${version.value}.jar"
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
// when running "clean assembly 'release with-defaults'" create assembly artifact also
addArtifact(artifact in(Compile, assembly), assembly)

val sparkVersion = "2.4.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.apache.hadoop" % "hadoop-aws" % "2.8.3" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
publishTo := {
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at "http://<snapshot repository>")
  else
    Some("releases" at "http://<release repository>")
}
