package karius.exercise.genome.similarity

import karius.exercise.genome.similarity.algorithm.KmerCounter
import karius.exercise.genome.similarity.app.SimilarityParameters
import karius.exercise.genome.similarity.io.{FASTAReader, GenomeSimilarityResultWriter}
import karius.exercise.genome.similarity.results._
import org.apache.spark.internal.Logging
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

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
      val map: Map[String, Long]    = it.map(fr => (fr.kmerKey -> fr.count)).toMap
      val combined: KmerCountResult = KmerCountResult(map)
      n -> combined
    })

    //    rs.show(10)
    rs.printSchema()

    val rsCollected = rs.collect()

    val reportMatrix: Seq[FlatGenomeSimilarity] =
      for {
        i <- 0 to rsCollected.size - 1;
        j <- i + 1 to rsCollected.size - 1
      } yield {
        logInfo(f"@@@@@@@@@@@@@@@@ i: ${i} j: ${j}")
        val diffRatio = rsCollected(i)._2.calculateDiffRatioByKmerAndCounts(rsCollected(j)._2)
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
