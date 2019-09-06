package karius.exercise.genome.similarity.io
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{ SparkSession}
import karius.exercise.genome.similarity._
/*
Handle FASTA input file reading
1. skip the first line
2. each file could have multiple line of sequence segments sample
3. We will skip lines which include IUPAC codes from consideration

 */
object FASTAReader extends Serializable with Logging {
  def read(path: String)(implicit spark: SparkSession)= {
    import spark.implicits._
    spark.sqlContext.read.option("header", "false").text(path).as[String].filter(s=>s.contains(IUPAC_CODES))
  }
}
