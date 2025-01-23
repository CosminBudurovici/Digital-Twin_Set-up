package WorkPackage.SmartInsectCounting.Silver;

import General.Bronze.DataSchema;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;


import static General.Silver.DataOrganization.fileStream;
import static org.apache.spark.sql.functions.*;

public class DataNormalisation {

    String fileSource = "/opt/bitnami/spark/WP_3/Bronze/Data",
        insectFindingTablePath = "/opt/bitnami/spark/WP_3/Silver/Data/insectFindingTable",
            insectFindingTablePathCheckpoint = "/opt/bitnami/spark/WP_3/Silver/DataCheckpoint/insectFindingTable",
        yellowCardTablePath = "/opt/bitnami/spark/WP_3/Silver/Data/yellowCardTable",
            yellowCardTablePathCheckpoint = "/opt/bitnami/spark/WP_3/Silver/DataCheckpoint/yellowCardTable",
        greenhouseTablePath = "/opt/bitnami/spark/WP_3/Silver/Data/greenhouseTable";
    SparkSession spark;

    public DataNormalisation(SparkSession spark){
        this.spark = spark;
    }

    public void startTableUpdateStream() throws Exception {
        Dataset<Row> readStream = fileStream(spark,fileSource, DataSchema.getOGSchema());
        Dataset<Row> insectFindingEntry = readStream
                .select(readStream.col("imageID").as("CardID"),
                        functions.explode(readStream.col("findings")).as("finding"))
                .select("finding.id", "CardID", "finding.type", "finding.probability")
                .withColumnRenamed("id","InsectID")
                .withColumnRenamed("type","InsectType")
                .withColumn("DateAndTime", functions.current_timestamp());

        insectFindingEntry.writeStream()
                .format("delta")
                .option("path",insectFindingTablePath)
                .option("checkpointLocation", insectFindingTablePathCheckpoint)
                .start()
                .processAllAvailable();

        Dataset<Row> yellowCard = readStream
                .select(readStream.col("imageID"))
                .withColumn("AreaID", substring(readStream.col("imageID"),0,2))
                .withColumn("Location", substring(readStream.col("imageID"),4,6))
                .withColumn("DateAndTime",functions.current_timestamp())
                .withColumnRenamed("imageID", "CardID");

        yellowCard.writeStream()
                .format("delta")
                .option("path",yellowCardTablePath)
                .option("checkpointLocation", yellowCardTablePathCheckpoint)
                .start()
                .processAllAvailable();
    }
}
