package karius.exercise.genome.similarity

import karius.exercise.genome.similarity.algorithm.KmerCounter
import karius.exercise.genome.similarity.app.SimilarityParameters
import karius.exercise.genome.similarity.io.{FASTAReader, GenomeSimilarityResultWriter}
import karius.exercise.genome.similarity.results.{GenomeSimilarity, GnomeIndexReference, KmerCountResult}
import org.apache.spark.TaskContext
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{Dataset, SparkSession}

/*
This is the first attempt of the implementation.

The basic idea and flow can be described below:
- each fasta file is a sequence inputs partitioned by organism name
- read each fasta file as a partition
- each includes multiple input sequence segments
- each segments produce a set of KmerCountResult
- KmerCountResults from all segments for the same partition should be further aggregated and reduced to a single KmerCountResult
- collect KmerCountResults for all organism List[index, KmerCounts]
- compare organisms' kmer count results based on comparability strategy and generate comparison matrix report.

After initial test run, the fasta file is actually quite large, even input is a relatively small size ~2MB
Handling aggregation with the partition performance is not acceptable, it is essentially O(length of (segments)-k+1  * n(segements) )
(25000 * 81)
 */

@deprecated("first attempt of the implementation, abandoned due to poor performance.", "2019-09-08")
object SimilarityAnalysisProcessor extends Serializable with Logging {

  def exec(parameters: SimilarityParameters)(implicit spark: SparkSession) = {
    import spark.implicits._
    val ds: Dataset[(String, String)] = FASTAReader.read( parameters.path )
    val results: Dataset[KmerCountResult] = ds.mapPartitions( (iterator) => {
      val genoIndex = TaskContext.getPartitionId()
      val seq = iterator.toSeq
      val name = seq.head._2

      logInfo( f"@@@@@@@@@@@@@@@@@@${genoIndex}: ${name}" )

      //the idea is to read the input directory and change the partition path as something below
      //then we can compute analysis per partition in parallel

      Seq( KmerCounter.countAll_v1( genoIndex, parameters.kmer, seq.map( _._1 ) ) ).iterator
    } )

    logInfo( "@@@@@@@@@@@@@@@@@@collect results from partition" )
    //this will be done at master node given this is final aggregation reduce mode
    val totalResults: Array[KmerCountResult] = results.collect()

    logInfo( f"@@@@@@@@@@@@@@@@@@total KmerCountResult: ${totalResults.size}" )

    val reportMatrix =
      for {
        i <- 0 to totalResults.size - 1;
        j <- i + 1 to totalResults.size - 1
      } yield {
        logInfo( f"@@@@@@@@@@@@@@@@ i: ${i} j: ${j}" )
        GenomeSimilarity( i,
          j,
          totalResults( i ).isComparable( totalResults( j ), parameters.threshold ) )
      }

    logInfo( f"@@@@@@@@@@@@@@@@@@reportMatrix: ${reportMatrix}" )

    GenomeSimilarityResultWriter.writeGenomeIndexReference( parameters.outputPath, Seq.empty[GnomeIndexReference] )
    GenomeSimilarityResultWriter.writeSimilarityyReportMatrix( parameters.outputPath, reportMatrix )

    logInfo( "@@@@@@@@@@@@@@@@@@Analysis completed!" )
  }
}
