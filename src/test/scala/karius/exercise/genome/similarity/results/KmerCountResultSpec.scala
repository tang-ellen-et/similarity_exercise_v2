package karius.exercise.genome.similarity.results

import org.scalatest.FlatSpec

class KmerCountResultSpec extends FlatSpec {

  "KmerCountResult" should "be comparable if matched Kmer sequence count above threshold" in {
    val r1 = KmerCountResult( 1, Map( "AGC" -> 3, "TGG" -> 2 ) )
    val r2 = KmerCountResult( 2, Map( "AGC" -> 3, "TGG" -> 1 ) )

    assert ( r1.isComparable(r2, 0.4) == true)
    assert ( r1.isComparable(r2, 0.8) == false)
  }

  it should "not be comparable if kmer sequence keys are mismatched" in {
    val r1 = KmerCountResult( 1, Map( "AGC" -> 3) )
    val r2 = KmerCountResult( 2, Map( "AGC" -> 3, "TGG" -> 1 ) )
    assert ( r1.isComparable(r2, 0.8) == false)
  }
}
