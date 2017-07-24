
import org.apache.spark.mllib.linalg.Vectors
import com.datastax.spark.connector._
import org.apache.spark.mllib.clustering.KMeans

import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}

object kmeans {

  def main (args: Array[String]) {

    val conf = new SparkConf(true)
      .setAppName("read_csv_from_cassandra_into_case_class")
      .setMaster("mesos://10.0.1.37:5050")
      .set("spark.cassandra.connection.host", "10.0.249.29")

    val sc = new SparkContext(conf)

    val session = SparkSession
      .builder()
      .appName("Calculate Average Spending Per County")
      .getOrCreate()

    val df = session
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map( "table" -> "wordcount", "keyspace" -> "sentencesks"))
      .load() // This Dataset will use a spark.cassandra.input.size of 128
    //df.coalesce(1).write.mode("append").csv("/home/sskrishn/lalaoutput11/la.csv")
   /* df.coalesce(1).
      write.
      format("com.databricks.spark.csv").
      option("header", "false").
      save("myfile.csv")*/
    val allValRDDbo = df.rdd.map(r => (r.getString(0), r.getInt(1) ))
    allValRDDbo.cache()
    val paramRDDbo = df.rdd.map(r => Vectors.dense( r.getInt(1)))
    paramRDDbo.cache()
    /*val clusters2 = KMeans.train(paramRDDbo,2,100)
    val WSSSE2 = clusters2.computeCost(paramRDDbo)
    println("Within Set Sum of Squared Errors = " + WSSSE2)*/
    val clusters4 = KMeans.train(paramRDDbo,4,100)
    val WSSSE4 = clusters4.computeCost(paramRDDbo)
    println("Within Set Sum of Squared Errors = " + WSSSE4)
    println("Displaying the centroids of each of the clusters")
    clusters4.clusterCenters.foreach(println)
    val predictions = allValRDDbo.map{r => (r._1, clusters4.predict(Vectors.dense(r._2) ))}

    //Convert the rdd to a dataframe
    val finalDF = session.createDataFrame(predictions).toDF("word","cluster")

    //finalDF.show()

    //finalDF.write.format("com.databricks.spark.csv").option("header","true").save("tatakai1.csv")
    finalDF.show()
    val finalRDD = finalDF.rdd.map(r=> (r.getString(0),r.getInt(1)))
    finalRDD.saveToCassandra("sentencesks", "clusters", SomeColumns("word", "cluster"))
    paramRDDbo.unpersist()
    allValRDDbo.unpersist()
  }



}