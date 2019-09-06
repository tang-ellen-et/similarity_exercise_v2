import sbt._
import sbt.Keys.{resolvers, _}
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.{MergeStrategy, assemblyJarName, assemblyMergeStrategy, assemblyOption}
import sbtassembly.PathList

val externalDependencies = Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.github.scopt" %% "scopt" % "3.5.0"
)

lazy val analysisDependencies = externalDependencies ++   Dependencies.ProcessTest

 
lazy val root = project.in(file("."))
  .settings(CommonBuild.sparkSettings("karius-genom-analysis"))
  .settings(libraryDependencies ++= Dependencies.spark(true) ++ analysisDependencies)
  .settings(CommonBuild.publishSettings)
  .settings(CommonBuild.assemblySettings)
 

 
