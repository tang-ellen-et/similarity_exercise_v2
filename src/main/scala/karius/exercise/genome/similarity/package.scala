package karius.exercise.genome

package object similarity {
  val STANDARD_DNA_BASE = List("A", "C", "G", "T") //Only consider these
  val DNA_BASE_K_MERS = {
    (1 to STANDARD_DNA_BASE.length flatMap (x => STANDARD_DNA_BASE.combinations(x))) map (x =>
      STANDARD_DNA_BASE.mkString(""))

  }

  val IUPAC_CODES = Set('R', 'Y', 'S', 'W', 'K', 'M', 'B', 'D', 'H', 'V', 'N', '.', '-') //should be discarded from analysis during pre-process step
}
