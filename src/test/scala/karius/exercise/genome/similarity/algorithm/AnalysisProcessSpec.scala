package karius.exercise.genome.similarity.algorithm

import com.holdenkarau.spark.testing.DatasetSuiteBase
import karius.exercise.genome.similarity.{SimilarityAnalysisProcessor, SimilarityAnalysisProcessorV2}
import karius.exercise.genome.similarity.app.SimilarityParameters
import karius.exercise.genome.similarity.io.FASTAReader
import org.scalatest.{FlatSpec, Ignore}

class SimilarityAnalysisProcessSpec extends FlatSpec with DatasetSuiteBase {

  val inputDir  = getClass.getResource("/data").toString
  val outputDir = getClass.getResource("/output").toString

  "FASTAReader" should "read fasta input files and produce correct sequence and organisom name from partition" in {
    implicit val spark = this.spark
    val ds             = FASTAReader.read(inputDir)
    ds.printSchema()
    ds.show(10)

    assert(ds.schema.size == 2)
  }

//  ignore should "read fasta sequence input data and generate similarity report matrix" in {
//
//    implicit val spark = this.spark
//    SimilarityAnalysisProcessor.exec(
//      SimilarityParameters(path = inputDir, kmer = 20, threshold = 0.9, outputPath = outputDir))
//  }

  "SimilarityAnalysisProcessorV2" should "read fasta sequence input data and generate similarity report matrix" in {

    implicit val spark = this.spark
    SimilarityAnalysisProcessorV2.exec(
      SimilarityParameters(path = inputDir, kmer = 20, threshold = 0.9, outputPath = outputDir))
  }
}
