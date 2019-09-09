package karius.exercise.genome

package object similarity {
  //sequence inputs should be discarded from analysis during pre-process step if contains following letters
  //TODO: to be confirmed if this is the correct understanding of the requirement
  val IUPAC_CODES = Set( 'R', 'Y', 'S', 'W', 'K', 'M', 'B', 'D', 'H', 'V', 'N', '.', '-' )
}
