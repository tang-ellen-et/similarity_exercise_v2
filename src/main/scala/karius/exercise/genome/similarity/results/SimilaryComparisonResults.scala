package karius.exercise.genome.similarity.results

case class GnomeIndexReference(name: String, index: Int)

case class GenomeSimilarity(genomeIndexOne: Int, genomeIndexTwo: Int, isComparable: Boolean)

case class FlatGenomeSimilarity(nameOne: String, nameTwo: String, diffRatio: Double, isComparable: Boolean)



