package karius.exercise.genome.similarity.algorithm

import org.scalatest.{FlatSpec, FunSuite}

class KmerCounterSpec extends FlatSpec {

  "KmerCounter" should "generate Kmer count when kmerLength less than sequence length" in {
    val r = KmerCounter( 1, 3, "ATGG" ).result
    println( r )

    assert( r.kemerCounts.size == 2 )
    assert( r.kemerCounts.keySet.equals( Set( "ATG", "TGG" ) ) )
  }

  it should "generate Kmer count when kmerLength is longer than sequence length" in {
    val r = KmerCounter( 1, 20, "ATGG" ).result
    println( r )

    assert( r.kemerCounts.size == 1 )
    assert( r.kemerCounts.keySet.equals( Set( "ATGG" ) ) )
  }

  it should "count sequence correctly" in {
    val r = KmerCounter( 1, 3, "ATGCGATG" ).result
    println( r )

    assert( r.kemerCounts.size == 5 )
    assert( r.kemerCounts.keySet.equals( Set( "ATG", "TGC", "GCG", "CGA", "GAT" ) ) )
    assert( r.kemerCounts.getOrElse( "ATG", "" ) == 2 )
  }

  it should "count and merge all kmer count for all sequences" in {
    val r = KmerCounter.countAll( 1, 3, Seq( "ATGCGATG", "GCGATGC" ) )
    println( r.kemerCounts )
    assert( r.kemerCounts.keySet.equals( Set( "ATG", "TGC", "GCG", "CGA", "GAT" ) ) )
    assert( r.kemerCounts.getOrElse( "ATG", "" ) == 3 )
    assert( r.kemerCounts.getOrElse( "TGC", "" ) == 2 )
    assert( r.kemerCounts.size == 5 )
  }

}
