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

  it should "merge with other result of the same genome index" in {
    val r0 = KmerCountResult( 1, Map.empty[String, Int] )
    val r1 = KmerCountResult( 1, Map( "AGC" -> 3, "TGG" -> 2 ) )
    val r2 = KmerCountResult( 1, Map( "AGC" -> 3, "TGG" -> 1 , "ATC"-> 4) )

    val r = r1.combine(r2 )
    println (r)

    assert (r.get.result.size == 3)
    assert (r.get.result.getOrElse("AGC", 0) == 6 )
    assert (r.get.result.getOrElse("TGG", 0) == 3)
    assert (r.get.result.getOrElse("ATC", 0) == 4 )


    val r01 = r0.combine(r1)
    println(r01)
    assert (r01.get.result.size == 2)
    assert (r01.get.result.getOrElse("AGC", 0) == 3 )
  }

  "Result of different genome index" should "not be combined" in {
    val r1 = KmerCountResult( 1, Map( "AGC" -> 3, "TGG" -> 2 ) )
    val r2 = KmerCountResult( 2, Map( "AGC" -> 3, "TGG" -> 1 , "ATC"-> 4) )

    val r = r1.combine(r2 )
    assert (r.isEmpty)
  }
}
