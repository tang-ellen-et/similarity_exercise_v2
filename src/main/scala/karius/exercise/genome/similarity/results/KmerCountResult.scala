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

  def combine(other: KmerCountResult): Option[KmerCountResult] = this.genoIndex.equals( other.genoIndex ) match {
    case false => None
    case true => {
      val list = this.result.toList ++ other.result.toList
      val merged = list.groupBy( _._1 ).map { case (k, v) => k -> v.map( _._2 ).sum }
      Some( KmerCountResult( this.genoIndex, merged ) )
    }
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