package karius.exercise.genome.similarity.results

import org.scalatest.FlatSpec

class KmerCountResultSpec extends FlatSpec {

  "KmerCountResult" should "be comparable if matched Kmer sequence count above threshold" in {
    val r1 = KmerCountResult(   Map( "AGC" -> 3L, "TGG" -> 2L ) )
    val r2 = KmerCountResult(   Map( "AGC" -> 3L, "TGG" -> 1L ) )

    assert ( r1.isComparable(r2, 0.4) == true)
    assert ( r1.isComparable(r2, 0.8) == false)
  }

  it should "not be comparable if kmer sequence keys are mismatched" in {
    val r1 = KmerCountResult(  Map( "AGC" -> 3L) )
    val r2 = KmerCountResult( Map( "AGC" -> 3L, "TGG" -> 1L ) )
    assert ( r1.isComparable(r2, 0.8) == false)
  }

  it should "merge with other result of the same genome index" in {
    val r0 = KmerCountResult(  Map.empty[String, Long] )
    val r1 = KmerCountResult(  Map( "AGC" -> 3L, "TGG" -> 2l ) )
    val r2 = KmerCountResult( Map( "AGC" -> 3L, "TGG" -> 1L , "ATC"-> 4L) )

    val r = r1.combine(r2 )
    println (r)

    assert (r.kemerCounts.size == 3)
    assert (r.kemerCounts.getOrElse("AGC", 0) == 6L )
    assert (r.kemerCounts.getOrElse("TGG", 0) == 3L)
    assert (r.kemerCounts.getOrElse("ATC", 0) == 4L )


    val r01 = r0.combine(r1)
    println(r01)
    assert (r01.kemerCounts.size == 2)
    assert (r01.kemerCounts.getOrElse("AGC", 0) == 3 )
  }

//  "Result of different genome index" should "not be combined" in {
//    val r1 = KmerCountResult( 1, Map( "AGC" -> 3, "TGG" -> 2 ) )
//    val r2 = KmerCountResult( 2, Map( "AGC" -> 3, "TGG" -> 1 , "ATC"-> 4) )
//
//    val r = r1.combine(r2 )
//    assert (r.isEmpty)
//  }
}
