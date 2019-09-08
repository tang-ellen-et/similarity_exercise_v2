package karius.exercise.genome.similarity
import karius.exercise.genome.similarity.algorithm.KmerCounter
import karius.exercise.genome.similarity.app.SimilarityParameters
import karius.exercise.genome.similarity.io.{FASTAReader, GenomeSimilarityResultWriter}
import karius.exercise.genome.similarity.results.{GenomeSimilarity, GnomeIndexReference, KmerCountResult}
import org.apache.spark.TaskContext
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{Dataset, SparkSession}

object SimilarityAnalysisProcessor extends Serializable with Logging {

  def exec(parameters: SimilarityParameters)(implicit spark: SparkSession) = {
    import spark.implicits._
    val ds = FASTAReader.read(parameters.path)
    val results: Dataset[KmerCountResult] = ds.mapPartitions((iterator) => {
      val genoIndex = TaskContext.getPartitionId()
      val seq = iterator.toSeq
      val name = seq.head._2

      logInfo(f"@@@@@@@@@@@@@@@@@@${genoIndex}: ${name}")

      //the idea is to read the input directory and change the partition path as something below
      //then we can compute analysis per partition in parallel

      Seq(KmerCounter.countAll_v1(genoIndex, parameters.kmer, seq.map(_._1))).iterator
    })

    logInfo("@@@@@@@@@@@@@@@@@@collect results from partition")
    //this will be done at master node given this is final aggregation reduce mode
    val totalResults: Array[KmerCountResult] = results.collect()

    logInfo(f"@@@@@@@@@@@@@@@@@@total KmerCountResult: ${totalResults.size}")

    val reportMatrix =
      for {
        i <- 0 to totalResults.size-1;
        j <- i+1 to totalResults.size-1
      } yield {
        logInfo(f"@@@@@@@@@@@@@@@@ i: ${i} j: ${j}")
        GenomeSimilarity( totalResults( i ).genoIndex,
          totalResults( j ).genoIndex,
          totalResults( i ).isComparable( totalResults( j ), parameters.threshold ) )
      }

    logInfo(f"@@@@@@@@@@@@@@@@@@reportMatrix: ${reportMatrix}")

    GenomeSimilarityResultWriter.writeGenomeIndexReference(parameters.outputPath, Seq.empty[GnomeIndexReference])
    GenomeSimilarityResultWriter.writeSimilaryReportMatrix(parameters.outputPath, reportMatrix)

    logInfo("@@@@@@@@@@@@@@@@@@Analysis completed!")
  }
}
