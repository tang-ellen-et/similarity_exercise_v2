import sbt._

object Dependencies {

  val SparkVersion   = "2.4.4"



  val CatsCore = "org.typelevel"     %% "cats-core" % "1.5"
  val PlayJson = "com.typesafe.play" %% "play-json" % "2.6.10"
  val Slf4J    = "org.slf4j"         % "slf4j-api"  % "1.7.25"

  val Spark = Seq(
    "org.apache.spark" %% "spark-core" % SparkVersion,
    "org.apache.spark" %% "spark-sql"  % SparkVersion
  )

  val ProcessTest = Seq(
    "com.holdenkarau"  %% "spark-testing-base" % "2.3.0_0.10.0" % "test",
    "org.apache.spark" %% "spark-hive"         % "2.2.1"        % "test"
  )

  def spark(provided: Boolean) =
    if (provided) Dependencies.Spark.map(_ % "provided")
    else Dependencies.Spark.map(_          % "compile")
}
