package karius.exercise.genome.similarity.results

case class KmerCountResult(kemerCounts: Map[String, Long]) {

  //1) if key sets are different return false
  //2) compare the key count with similar sequence
  def isComparable(other: KmerCountResult, threshold: Double): Boolean = {
    this.kemerCounts.keySet.equals( other.kemerCounts.keySet ) match {
      case false => false
      //compare key sets
      case true => {
        val diff: Map[String, Long] = (this.kemerCounts.toSet diff other.kemerCounts.toSet).toMap
        val diffRatio = (diff.size.toDouble / this.kemerCounts.keySet.size)

        diffRatio < (1 - threshold)
      }
    }
  }

  /*
  should use this strategy as multiplicity won't be taken into consideration for the first pass
   */
  def calculateDiffRatioByKmerSet(other: KmerCountResult) = {
     val intersetKmers =  this.kemerCounts.keySet intersect( other.kemerCounts.keySet)
     val unionKmers =this.kemerCounts.keySet union( other.kemerCounts.keySet )
     val diffRatio =  1- intersetKmers.size.toDouble /  unionKmers.size.toDouble
    diffRatio
  }

  def calculateDiffRatioByKmerAndCounts(other: KmerCountResult) = {
    val diff: Map[String, Long] = (this.kemerCounts.toSet diff other.kemerCounts.toSet).toMap
    val unionKmers =this.kemerCounts.keySet union( other.kemerCounts.keySet )
    val diffRatio =  diff.size.toDouble /  unionKmers.size.toDouble
    diffRatio
  }


  @deprecated( "not the best performance", "2019-09-08" )
  def combine_v1(other: KmerCountResult) = {

    println( f"keysize: ${this.kemerCounts.keySet.size}  other: ${other.kemerCounts.keySet.size}" )
    val merged = (this.kemerCounts.keySet ++ other.kemerCounts.keySet)
      .map( k => (k, this.kemerCounts.getOrElse( k, 0L ) + other.kemerCounts.getOrElse( k, 0L )) )
      .toMap
    println( f"--merged--" )
    KmerCountResult( merged )
  }


  def combine(other: KmerCountResult): KmerCountResult = {

    println( f"keysize: ${this.kemerCounts.keySet.size}  other: ${other.kemerCounts.keySet.size}" )
    // val merged = (this.result.keySet ++ other.result.keySet).map( k => (k, this.result.getOrElse( k, 0 ) + other.result.getOrElse( k, 0 )) ).toMap
    val merged = CombineMaps.combine( this.kemerCounts, other.kemerCounts )
    println( f"--merged--" )
    KmerCountResult( merged )

  }
}

object CombineMaps {
  type Counts = Map[String, Long]

  def combine(x: Counts, y: Counts): Counts = {
    val x0 = x.withDefaultValue( 0L )
    val y0 = y.withDefaultValue( 0L )
    val keys = x.keySet.union( y.keySet )
    keys.map { k =>
      (k -> (x0( k ) + y0( k )))
    }.toMap
  }
}

case object KmerCountResult {

  def combineAll(start: KmerCountResult, others: Seq[KmerCountResult]): KmerCountResult = others.isEmpty match {
    case true => start
    case false => {
      val r = start.combine( others.head )
      combineAll( r, others.tail )
    }
  }

  def isDiffAcceptable(diffRatio: Double, threshold: Double): Boolean = {
    diffRatio < (1-threshold)
  }
}
