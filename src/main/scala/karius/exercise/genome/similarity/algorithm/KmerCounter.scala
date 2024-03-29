package karius.exercise.genome.similarity.algorithm

import karius.exercise.genome.similarity.results.KmerCountResult

/*
   Kmer counting algorithm implementation
   assuming that all invalid characters are thrown out ahead of time
 */
case class KmerCounter(genoIndex: Int, kmerLength: Int, sequence: String) {

  def result: KmerCountResult = {
    val r = collection.mutable.Map.empty[String, Long]

    for (ss <- segments) {
      val element = r.get( ss )
      element match {
        case None => r += ss -> 1
        case Some( v ) => r.update( ss, v + 1 )
      }
    }

    KmerCountResult( r.toMap )
  }

  private lazy val segments = (kmerLength > sequence.size) match {
    case true => Seq( sequence )
    case false => {
      for {i <- 0 to (sequence.size - kmerLength)} yield sequence.substring( i, i + kmerLength )
    }
  }

}

case object KmerCounter {

  def countAll_v1(genoIndex: Int, kmerLength: Int, sequenceList: Seq[String]): KmerCountResult = {
    val resultList = sequenceList.map( s => (KmerCounter( genoIndex, kmerLength, s ).result) )
    println( f"@@@@@@@@@@@@  resultList #${genoIndex} ${resultList.size} " )

    val r1 = resultList.reduce( (x, y) => x.combine( y ) )
    println( f"@@@@@@@@@@@@  combine all #${genoIndex} completed" )
    r1
  }

  def countAll(genoIndex: Int, kmerLength: Int, sequenceList: Seq[String]): KmerCountResult = {
    val resultList = sequenceList.map( s => (KmerCounter( genoIndex, kmerLength, s ).result) )
    println( f"@@@@@@@@@@@@  resultList #${genoIndex} ${resultList.size} " )
    val r1 = KmerCountResult.combineAll( resultList.head, resultList.tail )
    println( f"@@@@@@@@@@@@  combine all #${genoIndex} completed" )
    r1
  }


}
