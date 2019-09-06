# Karius Geno Similarity Analsysis Spark Application #

### Package for windows and linux ###

    sbt assembly


### BenchMark Data Files

The sample input files can be found in:
    src\test\resources\


### Required Parameters

    --path
   The diretory containing the fasta files partitioned by organism name

    --kmer
    The size of the k-mer to use for the comparison. A reasonable k-mer to test the program with would be 20.

    --threshold
    Minimal threshold under which similarities should not be reported.
    (assumption is a percentage, need to be confirmed, however this is assumptio we are taking)


    --output
    The name of the directory to which the results and reference files will be written.


### Sample Command

    # for bankbudget import
    spark-submit --class karius.exercise.genome.similarity.app.SimilarityAnalysisApp --master local file:///C:/Users/tange/github-repo/karius-analysis-assembly.jar -p ....

### Notes ###

#### Kmer counting ####
https://en.wikipedia.org/wiki/K-mer

A sequence of length {\displaystyle L} L will have {\displaystyle L-k+1}
{\displaystyle L-k+1} k-mers and {\displaystyle n^{k}} n^{{k}} total possible k-mers,
where {\displaystyle n} n is number of possible monomers (e.g. four in the case of DNA).


#### TODO ####
Unit tests to be provided for all domain classes
        - KmerCountResult
        - KmerCounter (case class and case object)
Basic spark functional tests to be added
- Processor unit tests with spark tests
- reader unit tests with spark test
