package karius.exercise.genome.similarity.results

case class KmerCountResult(genoIndex: Int, result: Map[String, Int]) {

  //1) if key sets are different return false
  //2) compare the key count with similar sequence
  def isComparable(other: KmerCountResult, threshold: Double): Boolean = {
    this.result.keySet.equals( other.result.keySet ) match {
      case false => false
      //compare key sets
      case true => {
        val diff: Map[String, Int] = (this.result.toSet diff other.result.toSet).toMap
        val diffRatio = (diff.size.toDouble / this.result.keySet.size)

        diffRatio < (1 - threshold)
      }
    }
  }

  @deprecated("not the best performance", "2019-09-08")
  def combine_v1(other: KmerCountResult): Option[KmerCountResult] = this.genoIndex.equals( other.genoIndex ) match {
    case false => None
    case true => {
      println (f"keysize: ${this.result.keySet.size}  other: ${other.result.keySet.size}" )
      val merged = (this.result.keySet ++ other.result.keySet).map( k => (k, this.result.getOrElse( k, 0 ) + other.result.getOrElse( k, 0 )) ).toMap
      println (f"--merged--" )
      Some( KmerCountResult( this.genoIndex, merged ) )
    }
  }


  def combine(other: KmerCountResult): Option[KmerCountResult] = this.genoIndex.equals( other.genoIndex ) match {
    case false => None
    case true => {
      println (f"keysize: ${this.result.keySet.size}  other: ${other.result.keySet.size}" )
     // val merged = (this.result.keySet ++ other.result.keySet).map( k => (k, this.result.getOrElse( k, 0 ) + other.result.getOrElse( k, 0 )) ).toMap
      val merged = CombineMaps.combine(this.result, other.result)
      println (f"--merged--" )
      Some( KmerCountResult( this.genoIndex, merged ) )

    }
  }
}


object CombineMaps {
  type Counts = Map[String,Int]
  def combine(x: Counts, y: Counts): Counts = {
    val x0 = x.withDefaultValue(0)
    val y0 = y.withDefaultValue(0)
    val keys = x.keySet.union(y.keySet)
    keys.map{ k => (k -> (x0(k) + y0(k))) }.toMap
  }
}

case object KmerCountResult {

  def combineAll(start: KmerCountResult, others: Seq[KmerCountResult]): KmerCountResult = others.isEmpty match {
    case true => start
    case false => {
      val r = start.combine( others.head ).get
      combineAll( r, others.tail )
    }
  }
}