package karius.exercise.genome.similarity.io

import karius.exercise.genome.similarity.results.{GenomeSimilarity, GnomeIndexReference}
import org.apache.spark.sql.{SaveMode, SparkSession}

object GenomeSimilarityResultWriter {
  val REFERENCE_FILE_NAME = "gnome_index_reference.csv"
  val SIMILARITY_REPORT_FILE_NAME = "similarity_report_matrix.csv"

  def writeGenomeIndexReference(path: String, references: Seq[GnomeIndexReference])(implicit spark: SparkSession) = {
    import spark.implicits._
    references.toDS().write.option( "header", "true" ).mode( SaveMode.Overwrite ).csv( s"${path}/${REFERENCE_FILE_NAME}" )
  }

  def writeSimilaryReportMatrix(path: String, references: Seq[GenomeSimilarity])(implicit spark: SparkSession) = {
    import spark.implicits._
    references.toDS().write.option( "header", "true" ).mode( SaveMode.Overwrite ).csv( s"${path}/${SIMILARITY_REPORT_FILE_NAME}" )
  }
}
