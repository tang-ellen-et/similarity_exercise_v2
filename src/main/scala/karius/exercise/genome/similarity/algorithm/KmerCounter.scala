package karius.exercise.genome.similarity.algorithm

import karius.exercise.genome.similarity.results.KmerCountResult

import scala.collection.immutable

/*
   this is the class to perform Kmer counting algorithm
   assuming that all invalid characters are thrown out ahead of time
 */
case class KmerCounter(genoIndex: Int, kmerLength: Int, sequence: String) {

  def result: KmerCountResult = {
    val r = collection.mutable.Map.empty[String, Int]

    for (ss <- segments) {
      val element: Option[Int] = r.get( ss )
      element match {
        case None => r += ss -> 1
        case Some( v ) => r.update( ss, v + 1 )
      }
    }

    KmerCountResult( genoIndex, r.toMap )
  }

  //  private lazy val segments = this.sequence.grouped(kmerLength).toSeq
  private lazy val segments  = (kmerLength> sequence.size) match {
    case true=> Seq(sequence)
    case false=> {
      for {i <- 0 to (sequence.size - kmerLength)}
        yield sequence.substring( i, i + kmerLength )
    }

  }

}

case object KmerCounter {

  def countAll(genoIndex: Int, kmerLength: Int, sequenceList: Seq[String]): KmerCountResult = {
    val resultList = sequenceList.map( s => (KmerCounter( genoIndex, kmerLength, s ).result) )
    resultList.foldLeft( KmerCountResult( genoIndex, Map.empty[String, Int] ) )( (s, r) => s.combine( r ).get )
  }

}
