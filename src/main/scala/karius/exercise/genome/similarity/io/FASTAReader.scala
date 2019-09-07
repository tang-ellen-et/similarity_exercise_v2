package karius.exercise.genome.similarity.io

import org.apache.spark.internal.Logging
import org.apache.spark.sql.{Dataset, SparkSession}
import karius.exercise.genome.similarity._

/*
Handle FASTA input file reading
1. skip the first line
2. each file could have multiple line of sequence segments sample
3. We will skip lines which include IUPAC codes from consideration

 */
object FASTAReader extends Serializable with Logging {
  def read(path: String)(implicit spark: SparkSession) = {
    import spark.implicits._
    val ds: Dataset[(String, String)] = spark.sqlContext.read.option( "header", "False" ).text( path ).as[(String, String)]
    val filtered = ds.filter( (s: (String, String)) => (!s._1.startsWith(">") ) &&  s._1.forall( !IUPAC_CODES.contains( _ ) ) )
    filtered
  }
}
