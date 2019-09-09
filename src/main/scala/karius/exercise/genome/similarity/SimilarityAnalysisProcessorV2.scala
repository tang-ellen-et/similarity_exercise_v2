package karius.exercise.genome.similarity

import karius.exercise.genome.similarity.algorithm.KmerCounter
import karius.exercise.genome.similarity.app.SimilarityParameters
import karius.exercise.genome.similarity.io.{FASTAReader, GenomeSimilarityResultWriter}
import karius.exercise.genome.similarity.results._
import org.apache.spark.internal.Logging
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

/*
The basic idea and calculation flow is outlined below:
- each fasta file is a sequence inputs partitioned by organism name
- read each file line as a below flat data structure
                case class FlatKmerCountResult(name: String, kmerKey: String, count: Long)
- Use spark dataset operation groupBy (name + kmer) and sum count by (name+key) to reduce to an aggregated FlatKmerCountResult
- Use spark dataset groupBy oto group all kmerCount result for a given organism
- map resulted group dataet to KmerCountResult object
- collect KmerCountResults for all organism List[(name, KmerCounts)]
- calculate the diff ratio and then compare with threshold and produce FlatGenomeSimilarity
            case class FlatGenomeSimilarity(nameOne: String, nameTwo: String, diffRatio: Double, isComparable: Boolean)

Push down aggregation to spark layer and gain much improved performance
*/
object SimilarityAnalysisProcessorV2 extends Serializable with Logging {

  def exec(parameters: SimilarityParameters)(implicit spark: SparkSession) = {
    import spark.implicits._
    val ds = FASTAReader.read(parameters.path)

    val kmerCountResults: Dataset[FlatKmerCountResult] = ds.flatMap(s => {
      val gnomName = s._2
      val sequence = s._1
      (KmerCounter(0, parameters.kmer, sequence).result.kemerCounts.map(r =>
        FlatKmerCountResult(name = gnomName, kmerKey = r._1, count = r._2)))
    })

    val flatKmerCountDs: Dataset[FlatKmerCountResult] = kmerCountResults
      .groupBy("name", "kmerKey")
      .agg(count("count") as "count")
      .select("name", "kmerKey", "count")
      .as[FlatKmerCountResult]

    //    flatKmerCountDs.show(10)
    flatKmerCountDs.printSchema()

    val groupedDs: KeyValueGroupedDataset[String, FlatKmerCountResult] = flatKmerCountDs.groupByKey(row => row.name)

    val rs = groupedDs.mapGroups((n, it) => {
      val map = it.map(fr => (fr.kmerKey -> fr.count)).toMap
      n ->  KmerCountResult(map)
    })

    //    rs.show(10)
    rs.printSchema()

    val rsCollected: Array[(String, KmerCountResult)] = rs.collect()

    val reportMatrix: Seq[FlatGenomeSimilarity] =
      for {
        i <- 0 to rsCollected.size - 1;
        j <- i + 1 to rsCollected.size - 1
      } yield {
        logInfo(f"@@@@@@@@@@@@@@@@ i: ${i} j: ${j}")
        val diffRatio = rsCollected(i)._2.calculateDiffRatioByKmerSet(rsCollected(j)._2)
        FlatGenomeSimilarity(rsCollected(i)._1,
                             rsCollected(j)._1,
                             diffRatio,
                             KmerCountResult.isDiffAcceptable(diffRatio, parameters.threshold))

      }

    reportMatrix.toDS().show(10)
    GenomeSimilarityResultWriter.writeFlatSimilarityyReportMatrix(parameters.outputPath, reportMatrix)

    logInfo("@@@@@@@@@@@@@@@@@@Analysis completed!")
  }

}
