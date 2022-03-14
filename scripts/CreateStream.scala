spark.stop

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import com.datastax.spark.connector.streaming._

import scala.util.Try

object CreateStream {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
      .setAppName("KafkaSparkStreaming")
      .set("spark.cassandra.connection.host", "cassandra")
      .set("spark.cassandra.auth.username", "cassandra")
      .set("spark.cassandra.auth.password", "cassandra")

    val ssc = new StreamingContext(sparkConf, Seconds(20))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kafka:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("mytopic")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val mapped = stream.map { record => 
      val key = record.key
      val value = record.value 

      val split = value.split(",")

      val firstName = split.lift(0).getOrElse("")
      val lastName = split.lift(1).getOrElse("")
      val url = split.lift(2).getOrElse("")
      val productName = split.lift(3).getOrElse("")
      val count = Try(split.lift(4).getOrElse("")).getOrElse(0)

      Console.println(s"FirstName: $firstName")
      Console.println(s"LastName: $lastName")
      Console.println(s"URL: $url")
      Console.println(s"ProductName: $productName")
      Console.println(s"Count: $count")
        
      (firstName, lastName, url, productName, count)
    }

    mapped.print();

    mapped.saveToCassandra("sparkdata", "cust_data")

    ssc.start
    ssc.awaitTermination
  }
}
