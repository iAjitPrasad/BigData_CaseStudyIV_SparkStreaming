package Streaming

import java.io.File
import org.apache.spark.{SparkConf, SparkContext}
import scala.io.Source._
import org.apache.log4j.{Level,Logger}

object WordCountHDFS {

  private var localFilePath: File = new File("/home/acadgild/ScalaExs/CS5/newFile.txt")
  private var dfsDirPath: String = "hdfs://localhost:8020/user/streaming"
  private val NPARAMS = 2

  def main(args: Array[String]): Unit = {
    println("WordCountHDFS : Main Called Successfully")
    println("Performing local word count")
	
    val fileContents = readFile(localFilePath.toString())

    println("Performing local word count - File Content ->> "+fileContents)
    val localWordCount = runLocalWordCount(fileContents)
	
    println("WordCountHDFS : Main Called Successfully -> Local Word Count is ->> "+localWordCount)
    println("Performing local word count Completed !!")
    println("Creating Spark Context")

    val conf = new SparkConf().setMaster("local[2]").setAppName("WordCountHDFSApp")
    val sc = new SparkContext(conf)
	
  	val rootLogger = Logger.getRootLogger()
	  rootLogger.setLevel(Level.ERROR)
    
    println("Spark Context Created")
    println("Writing local file to DFS")
	
    val dfsFilename = dfsDirPath + "/dfs_read_write_test"
    val fileRDD = sc.parallelize(fileContents)
	
    fileRDD.saveAsTextFile(dfsFilename)
    println("Writing local file to DFS Completed")
    println("Reading file from DFS and running Word Count")
	
    val readFileRDD = sc.textFile(dfsFilename)

    val dfsWordCount = readFileRDD
      .flatMap(_.split(" "))
      .flatMap(_.split("\t"))
      .filter(_.nonEmpty)
      .map(w => (w, 1))
      .countByKey()
      .values
      .sum

      sc.stop()

    if (localWordCount == dfsWordCount) {
      println(s"Success! Local Word Count ($localWordCount) " +
        s"and DFS Word Count ($dfsWordCount) agree.")
    } 
    else {
      println(s"Failure! Local Word Count ($localWordCount) " +
        s"and DFS Word Count ($dfsWordCount) disagree.")
    }
  }

  //Function to check number of arguments passed to the program during runtime.
  private def parseArgs(args: Array[String]): Unit = {
  //Checking if length of args is not equal to 2 , including the name of application itself if run from shell
	if (args.length != NPARAMS) {
      printUsage()
      System.exit(1)
    }
  }

  //Function to instruct the proper run sequence for this application with 2 number of arguments
  private def printUsage(): Unit = {
    val usage: String = "DFS Read-Write Test\n" +
      "\n" +
      "Usage: localFile dfsDir\n" +
      "\n" +
      "localFile - (string) local file to use in test\n" +
      "dfsDir - (string) DFS directory for read/write tests\n"

    println(usage)
  }

  //Function to iterate through the entire file & return a list of each line within the file
  private def readFile(filename: String): List[String] = {
    val lineIter: Iterator[String] = fromFile(filename).getLines()
    val lineList: List[String] = lineIter.toList
    lineList
  }

  //Counting the total number of words that within the file on the local file system.
  def runLocalWordCount(fileContents: List[String]): Int = {
    fileContents.flatMap(_.split(" "))
      .flatMap(_.split("\t"))
      .filter(_.nonEmpty)
      .groupBy(w => w)
      .mapValues(_.size)
      .values
      .sum
  }
}