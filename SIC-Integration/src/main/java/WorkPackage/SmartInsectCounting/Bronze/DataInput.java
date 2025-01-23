package WorkPackage.SmartInsectCounting.Bronze;

import General.Bronze.DataStreaming;
import org.apache.spark.sql.SparkSession;

public class DataInput implements DataStreaming {

    String serviceURL = "",
            adminURL = "",
            topic = "",
            outputPath = "/opt/bitnami/spark/WP_3/Bronze/Data",
            checkpointPath = "/opt/bitnami/spark/WP_3/Bronze/DataCheckpoint";
    SparkSession spark;

    public DataInput(SparkSession spark){
        this.spark = spark;
    }

    public void startStream() throws Exception{
        //Test streaming with mock data
        testStream(spark, outputPath, checkpointPath);

        //Apache Pulsar Streaming
        //dataStream(spark,serviceURL,adminURL,topic,outputPath,checkpointPath);
    }
}
