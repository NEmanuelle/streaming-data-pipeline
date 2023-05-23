package com.labs1904.spark

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get}
import org.apache.hadoop.hbase.util.Bytes



/**
 * Spark Structured Streaming app
 *
 */
case class Review(marketplace: String, customerID: String, reviewID: String, productID: String, product_parent: String, product_title: String, product_category: String, star_rating: String, helpful_votes: String, total_votes: String,vine: String, verified_purchase: String, review_headline: String, review_body: String, review_date:String )
case class JoinReview(marketplace: String, customerID: String, reviewID: String, productID: String, product_parent: String, product_title: String, product_category: String, star_rating: String, helpful_votes: String, total_votes: String,vine: String, verified_purchase: String, review_headline: String, review_body: String, review_date:String, birthdate:String, mail:String, name:String, sex:String, username:String )

object StreamingPipeline {
  lazy val logger: Logger = Logger.getLogger(this.getClass)
  val jobName = "StreamingPipeline"

  val hdfsUrl = "CHANGE ME"
  val bootstrapServers =  "CHANGE ME"
  val username = "CHANGE ME"
  val password = CHANGE ME"
  val hdfsUsername = "CHANGE ME"

  //Use this for Windows
//  val trustStore: String = "src\\main\\resources\\kafka.client.truststore.jks"
  //Use this for Mac
  val trustStore: String = "src/main/resources/kafka.client.truststore.jks"

  def main(args: Array[String]): Unit = {
    try {
      val spark = SparkSession.builder()
        .config("spark.sql.shuffle.partitions", "3")
        .config("spark.hadoop.dfs.client.use.datanode.hostname", "true")
        .config("spark.hadoop.fs.defaultFS", hdfsUrl)
        .appName(jobName)
        .master("local[*]")
        .getOrCreate()

      import spark.implicits._

      val ds = spark
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", bootstrapServers)
        .option("subscribe", "reviews")
        .option("startingOffsets", "earliest")
        .option("maxOffsetsPerTrigger", "20")
        .option("startingOffsets","earliest")
        .option("kafka.security.protocol", "SASL_SSL")
        .option("kafka.sasl.mechanism", "SCRAM-SHA-512")
        .option("kafka.ssl.truststore.location", trustStore)
        .option("kafka.sasl.jaas.config", getScramAuthString(username, password))
        .load()
        .selectExpr("CAST(value AS STRING)").as[String]

      //ds.printSchema()
      //Ingest data from kafka topic: read from the "reviews" topic. You will need to use something to iterate through.
      //Split each message from the "reviews" topic into a scala case class. message.split("\t")
      val reviews = ds.map(s => {
        val splitLine = s.split("\t")
        //Parse the values into a Review scala case class
      Review(splitLine(0), splitLine(1), splitLine(2), splitLine(3), splitLine(4), splitLine(5), splitLine(6), splitLine(7), splitLine(8), splitLine(9), splitLine(10), splitLine(11), splitLine(12), splitLine(13), splitLine(14))
      })

      val customers = reviews.mapPartitions(partition => {
        val conf = HBaseConfiguration.create()
        conf.set("hbase.zookeeper.quorum", "hbase01.labs1904.com:2181")
        val connection = ConnectionFactory.createConnection(conf)
        val table = connection.getTable(TableName.valueOf("nkessler:users"))

        val getUserInfo = partition.map(id =>{
          val get = new Get(Bytes.toBytes(id.customerID)).addFamily(Bytes.toBytes("f1"))
          val res = table.get(get)
          val birthdate = Bytes.toString(res.getValue(Bytes.toBytes("f1"),Bytes.toBytes ("birthdate")))
          val mail = Bytes.toString(res.getValue(Bytes.toBytes("f1"), Bytes.toBytes("mail")))
          val name = Bytes.toString(res.getValue(Bytes.toBytes("f1"),Bytes.toBytes( "name")))
          val sex = Bytes.toString(res.getValue(Bytes.toBytes("f1"), Bytes.toBytes ("sex")))
          val username = Bytes.toString(res.getValue(Bytes.toBytes("f1"), Bytes.toBytes("username")))

          JoinReview(id.marketplace,id.customerID, id.reviewID, id.productID, id.product_parent, id.product_title, id.product_category, id.star_rating, id.helpful_votes, id.total_votes, id.vine, id.verified_purchase, id.review_headline, id.review_body, id.review_date, birthdate, mail, name, sex, username)

        }).toList

        connection close()

        getUserInfo.iterator
      })

      // TODO: implement logic here
      val result = customers

      // Write output to console
//      val query = result.writeStream
//        .outputMode(OutputMode.Append())
//        .format("console")
//        .option("truncate", false)
//        .trigger(Trigger.ProcessingTime("5 seconds"))
//        .start()

      //Write output to hdfs
      val query = result.writeStream
        .outputMode(OutputMode.Append())
        .format("csv")
        .option("delimiter", "\t")
        .option("path", s"/user/${hdfsUsername}/reviews_csv")
        .option("checkpointLocation", s"/user/${hdfsUsername}/reviews_checkpoint")
        .partitionBy("star_rating")
        .trigger(Trigger.ProcessingTime("5 seconds"))
        .start()
      query.awaitTermination()
    } catch {
      case e: Exception => logger.error(s"$jobName error in main", e)
    }
  }


  def getScramAuthString(username: String, password: String) = {
    s"""org.apache.kafka.common.security.scram.ScramLoginModule required
   username=\"$username\"
   password=\"$password\";"""
  }
}
