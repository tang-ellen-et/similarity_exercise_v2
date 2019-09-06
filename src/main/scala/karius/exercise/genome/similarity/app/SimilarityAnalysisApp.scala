package karius.exercise.genome.similarity.app

import karius.exercise.genome.similarity.SimilarityAnalysisProcessor
import moodys.analytics.cra.util.Logging
import org.apache.spark.sql.SparkSession

object SimilarityAnalysisApp extends Logging {
  def main(args: Array[String]): Unit = {

    implicit val sparkSession: SparkSession =
      SparkSession.builder().appName(SimilarityParametersParser.KARIUS_ANALYSIS_APP).getOrCreate()

    // Read CLI Arguments to get JSON configuration file path
    val parameters = SimilarityParametersParser.parse(args)

    if (parameters.isDefined) {
      // Start budget calculation
      SimilarityAnalysisProcessor.exec(parameters.get)(sparkSession)
      logInfo("Analysis completed.")
    } else
      // Cancel the execution
      logError("Analysis failed.")

    sparkSession.close()
  }

}
