package Streaming

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.log4j.{Level,Logger}

object WordCountOnTheFly {

  def main(args: Array[String]): Unit = {
    println("hey Spark Streaming")

	  val conf = new SparkConf().setMaster("local[2]").setAppName("SparkSteamingExample")
	  val sc = new SparkContext(conf)
    
	  val rootLogger = Logger.getRootLogger()
	  rootLogger.setLevel(Level.ERROR)
	
    //Creating a new spark streaming context that would be triggered every 15 seconds
    val ssc = new StreamingContext(sc, Seconds(15))
	
	  val lines = ssc.textFileStream(args(0))
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
	  
    wordCounts.print()
	
    ssc.start()
	  ssc.awaitTermination()
  }
}