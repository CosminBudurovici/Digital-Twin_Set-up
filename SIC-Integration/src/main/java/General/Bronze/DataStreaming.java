package General.Bronze;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


public interface DataStreaming extends DataSchema {

    /*
    General Structured streaming function for streaming files from pulsar
    Should stay active once run, or until spark application termination

    spark - the spark session object
    serviceUrl - link to pulsar service url ex:"pulsar://localhost:6650"
    adminUrl - link to admin url ex:"http://localhost:8080"
    topic - identifier of the message stream within pulsar ex:"persistent://tenant/namespace/topic-name"
    outputPath - path to output of parquet files converted from stream
    checkpointPath - path to checkpoint location (needed in case of errors)
    */
    default void dataStream (SparkSession spark, String serviceUrl, String adminUrl, String topic,
                             String outputPath, String checkpointPath) throws Exception{
        Dataset<Row> fileStream = spark.readStream()
                .format("pulsar")
                .option("service.url", serviceUrl)
                .option("admin.url", adminUrl)
                .option("topic", topic)
                .load();

        fileStream.writeStream()
                .format("parquet")
                .option("path", outputPath)
                .option("checkpointLocation", checkpointPath)
                .start()
                .awaitTermination();
    }

    default void testStream(SparkSession spark, String outputPath, String checkpointLocation) throws  Exception {

        Dataset<Row> fileStream = spark.readStream()
                .format("json")
                .option("multiline","true")
                .schema(DataSchema.getOGSchema())
                .load("/opt/bitnami/spark/inputTest");

        fileStream.writeStream()
                .format("parquet")
                .option("path",outputPath)
                .option("checkpointLocation", checkpointLocation)
                .start()
                .processAllAvailable();

    }
}
