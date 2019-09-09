# Karius Geno Similarity Analsysis Spark Application #

# [Business requirement](https://github.com/tangellen/similarity_exercise/tree/master/requirements)

# Technical design and considerations
It is a large dataset processing problem and we select Apache Spark™ as our underline distributed analytic engine.


## Basic flow

Input data
- fasta gnome sequence input dataset are paritioned by organism name
- fasta input data should be pre-processed as per requirement

Analysis
- calculate and aggregate kmer count per organism
- compare, calculate similarity ratio

Output
- generate and write out similarity comparison matrix reports for all organisms.


## Sample input data files

The sample input files can be found in:
    src\test\resources\

### Required Parameters

| Parameter    | Description                              |
| ------------ | ---------------------------------------- |
| --path        | The diretory containing the fasta files partitioned by organism name. |
| --kmer        | The size of the k-mer to use for the comparison. A reasonable k-mer to test the program with would be 20. |
| --threshold   | Minimal threshold under which <br />similarities should not be reported. |
| --output      |  The name of the directory to which the comparison results will be written. |


### Build and Tests
````shellscript
sbt clean compile test
sbt assembly
````


### Sample Spark job submit commandline
````shellscript
spark-submit --class karius.exercise.genome.similarity.app.SimilarityAnalysisApp --master local file:///C:/Users/tange/github-repo/karius-analysis-assembly.jar -p ....
````


### Notes ###

#### Kmer counting
https://en.wikipedia.org/wiki/K-mer

A sequence of length {\displaystyle L} L will have {\displaystyle L-k+1}
{\displaystyle L-k+1} k-mers and {\displaystyle n^{k}} n^{{k}} total possible k-mers,
where {\displaystyle n} n is number of possible monomers (e.g. four in the case of DNA).


### Summary
- The project should be in a relative complete state, you should be able to compile and run with the sample input datasets under test resource folder
- Try SimilarityAnalysisProcessSpec for a spark end to end run
- Two implementations with different data processing strategy, SimilarityAnalysisProcessorV2 is the clear winner with 44 secs run time to generate results.

- Sample spark job execution logs
````shellscript
2019-09-08 21:35:37,614 INFO  [Spark Context Cleaner] spark.ContextCleaner (Logging.scala:logInfo(54)) - Cleaned accumulator 94
2019-09-08 21:35:37,614 INFO  [Spark Context Cleaner] spark.ContextCleaner (Logging.scala:logInfo(54)) - Cleaned accumulator 75
2019-09-08 21:35:37,614 INFO  [Spark Context Cleaner] spark.ContextCleaner (Logging.scala:logInfo(54)) - Cleaned accumulator 29
2019-09-08 21:35:37,614 INFO  [Spark Context Cleaner] spark.ContextCleaner (Logging.scala:logInfo(54)) - Cleaned accumulator 48
2019-09-08 21:35:38,933 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] similarity.SimilarityAnalysisProcessorV2 (Logging.scala:logInfo(54)) - @@@@@@@@@@@@@@@@ i: 0 j: 1
2019-09-08 21:35:45,095 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] codegen.CodeGenerator (Logging.scala:logInfo(54)) - Code generated in 23.195024 ms
2019-09-08 21:35:45,177 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] codegen.CodeGenerator (Logging.scala:logInfo(54)) - Code generated in 11.832179 ms
2019-09-08 21:35:45,187 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] codegen.CodeGenerator (Logging.scala:logInfo(54)) - Code generated in 7.553471 ms
+--------------------+--------------------+------------------+------------+
|             nameOne|             nameTwo|         diffRatio|isComparable|
+--------------------+--------------------+------------------+------------+
|GCF_000019265.1_A...|GCF_000010705.1_A...|0.5156607056654089|       false|
+--------------------+--------------------+------------------+------------+

2019-09-08 21:35:45,423 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] output.FileOutputCommitter (FileOutputCommitter.java:<init>(123)) - File Output Committer Algorithm version is 1
2019-09-08 21:35:45,423 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] output.FileOutputCommitter (FileOutputCommitter.java:<init>(138)) - FileOutputCommitter skip cleanup _temporary folders under output directory:false, ignore cleanup failures: false
2019-09-08 21:35:45,426 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] datasources.SQLHadoopMapReduceCommitProtocol (Logging.scala:logInfo(54)) - Using output committer class org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
2019-09-08 21:35:45,448 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] codegen.CodeGenerator (Logging.scala:logInfo(54)) - Code generated in 11.295745 ms
2019-09-08 21:35:45,546 INFO  [ScalaTest-run-running-SimilarityAnalysisProcessSpec] spark.SparkContext (Logging.scala:logInfo(54)) - Starting job: csv at GenomeSimilarityResultWriter.scala:23alysisProcessSpec] spark.SparkContext (Logging.scala:logInfo(54)) - Starting job: csv at GenomeSimilarityResultWriter.scala:23
````