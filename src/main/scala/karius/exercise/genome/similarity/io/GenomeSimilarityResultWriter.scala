package karius.exercise.genome.similarity.io
import karius.exercise.genome.similarity.results.{GenomeSimilarity, GnomeIndexReference}
import org.apache.spark.sql.SparkSession

object GenomeSimilarityResultWriter {
  def REFERENCE_FILE_NAME = "gnome_index_reference.csv"
  def SIMILARITY_REPORT_FILE_NAME = "similary_report_matrix.csv"

  def writeGenomeIndexReference(path: String, references: Seq[GnomeIndexReference])(implicit spark: SparkSession) = {
    import spark.implicits._
    references.toDS().write.option("header", "true").csv(s"${path}/${REFERENCE_FILE_NAME}")
  }

  def writeSimilaryReportMatrix(path: String, references: Seq[GenomeSimilarity])(implicit spark: SparkSession) = {
    import spark.implicits._
    references.toDS().write.option("header", "true").csv(s"${path}/${SIMILARITY_REPORT_FILE_NAME}")
  }
}
