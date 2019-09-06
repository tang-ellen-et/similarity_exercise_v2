package karius.exercise.genome.similarity.algorithm
import karius.exercise.genome.similarity.results.KmerCountResult

/*
   this is the class to perform Kmer counting algorithm
   assuming that all invalid characters are thrown out ahead of time
 */
case class KmerCounter(genoIndex:Int, kmerLength: Int, sequence: String) {

  def result: KmerCountResult = {
    val r = collection.mutable.Map.empty[String, Int]

    for (ss <- segments) {
        val element: Option[Int] = r.get(ss)
        element match {
          case None => r+= ss-> 1
          case Some(v)=> r.update(ss, v+1)
        }
    }

    KmerCountResult(kmerLength, r.toMap)
  }

  private lazy val segments = this.sequence.grouped(kmerLength).toSeq

}

case object KmerCounter {

  def countAll(genoIndex: Int, kmerLength: Int, sequenceList: Seq[String]): KmerCountResult = {
    val resultList = sequenceList.map(s => (KmerCounter(genoIndex, kmerLength, s).result))
    resultList.foldLeft(KmerCountResult(kmerLength, Map.empty[String, Int]))((s, r) => s.combine(r).get)
  }

}
