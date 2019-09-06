package karius.exercise.genome.similarity.app

import scopt.OptionParser

case class SimilarityParameters(path: String = "", kmer: Int = 20, threshold: Double = 0.8, outputPath: String = "")

object SimilarityParametersParser {
  private val KARIUS_ANALYSIS_JAR = "karius-genome-similarity-analysis-assembly.jar"
  val KARIUS_ANALYSIS_APP = "Karius Genome Similarity Analysis App"

  private val similarityArgParser: OptionParser[SimilarityParameters] =
    new scopt.OptionParser[SimilarityParameters](KARIUS_ANALYSIS_APP) {

      head(KARIUS_ANALYSIS_APP, "1.0")

      opt[String]('p', "path")
        .required()
        .action { (x, c) => c.copy(path = x)
        }
        .text("The diretory containing the fasta files partitioned by organism name")

      opt[Int]('k', "kmer")
        .required()
        .action { (x, c) => c.copy(kmer = x)
        }
        .text("The size of the k-mer to use for the comparison. A reasonable k-mer to test the program with would be 20.")

      opt[Double]('t', "threshold")
        .required()
        .action { (x, c) => c.copy(threshold = x)
        }
        .text("Minimal threshold under which \nsimilarities should not be reported.")

      opt[String]('o', "output")
        .required()
        .action { (x, c) => c.copy(path = x)
        }
        .text("The name of the directory to which the results and reference files will be written.")

      help("help").text("Please see the ReadMe file for help.")

    }

  def parse(args: Array[String]): Option[SimilarityParameters] = {
    similarityArgParser.parse(args, SimilarityParameters())
  }

}
