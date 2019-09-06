package karius.exercise.genome.similarity.algorithm
import karius.exercise.genome.similarity.results.KmerCountResult

/*
   this is the class to perform Kmer counting algorithm
   assuming that all invalid charactors are thrown out ahead of time
 */
case class KmerCounter(genoIndex:Int, kmerLength: Int, sequence: String) {

  def result: KmerCountResult = {
    val r = Map.empty[String, Int]

    for (ss <- segments) {
      r.keySet.contains(ss) match {
        case true  => r(ss) += 1
        case false => r(ss) = 1
      }
    }

    KmerCountResult(kmerLength, r)
  }

  private lazy val segments = this.sequence.grouped(kmerLength).toSeq

}

case object KmerCounter {

  def countAll(genoIndex: Int, kmerLength: Int, sequenceList: Seq[String]): KmerCountResult = {
    val resultList = sequenceList.map(s => (KmerCounter(genoIndex, kmerLength, s).result))
    resultList.foldLeft(KmerCountResult(kmerLength, Map.empty[String, Int]))((s, r) => s.combine(r).get)
  }

}
