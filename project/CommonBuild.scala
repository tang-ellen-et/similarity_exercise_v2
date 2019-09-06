import sbt._
import sbt.Keys.{resolvers, _}
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.{MergeStrategy, assemblyJarName, assemblyMergeStrategy, assemblyOption}
import sbtassembly.PathList

object CommonBuild {
  lazy val commonScalacOptions = Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yinline-warnings",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture",
    // Enable Java SAM for compatibility with Java8 collection stream operations (https://herringtondarkholme.github.io/2015/01/24/scala-sam/)
    "-Xexperimental"
  )

//  javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

  lazy val commonJavacOptions = Seq(
    "-source",
    "1.8",
    "-target",
    "1.8",
    "-Xlint",
    "-Xms512M",
    "-Xmx2048M",
    "-XX:MaxPermSize=2048M",
    "-XX:+CMSClassUnloadingEnabled"
  )

  lazy val publishSettings = Seq(
    sources in (Compile, doc) := Seq.empty, //Skip doc generating
    publishArtifact in (Compile, packageDoc) := false,
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishTo := {
      val nexus = System.getProperty("nexusUrl")
      if (isSnapshot.value)
        Some("snapshots" at nexus + "repository/ers-rafa-snapshots/")
      else
        Some("releases" at nexus + "repository/ers-rafa-releases/")
    }
  )

  def assemblySettings = Seq(
    mainClass in assembly := Some("moodys.analytics.cra.data.MainApp"),
    assemblyMergeStrategy in assembly := {
      case PathList(ps @ _*) if ps.last endsWith ".parquet" =>  MergeStrategy.discard
      case PathList("META-INF", xs @ _*)                 => MergeStrategy.discard
      case PathList(ps @ _*) if ps.last endsWith ".conf" => MergeStrategy.concat
      case x                                             => MergeStrategy.first
    },
    assemblyJarName in assembly := s"cra-data-process-assembly.jar",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheUnzip = false)
  )

  def settings(appName: String) = Seq(
    name := appName,
    organization := "moodys.analytics.cra",
    version := "1.1-SNAPSHOT",
    scalaVersion := "2.11.11",
    scalacOptions ++= commonScalacOptions,
    javacOptions ++= commonJavacOptions,
    test in assembly := {},
    dependencyOverrides ++= Set(
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.11",
      "com.fasterxml.jackson.core"   % "jackson-databind"      % "2.8.11",
      "com.fasterxml.jackson.core"   % "jackson-annotations"   % "2.8.11"
    ),
    resolvers in ThisBuild += "SF Maven Repository" at "http://nexus-sf:8081/nexus/repository/maven-public"
//    resolvers in ThisBuild += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
  )

  def sparkSettings(appName: String) = settings(appName) ++ Seq(
    parallelExecution in Test := false,
    fork in Test := true
  )
}
