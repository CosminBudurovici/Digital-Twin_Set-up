package org.example;

import General.Bronze.DataSchema;
import WorkPackage.SmartInsectCounting.Gold.DataPreparation;
import WorkPackage.SmartInsectCounting.Silver.DataNormalisation;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import WorkPackage.SmartInsectCounting.Bronze.DataInput;




/*
Commands:
docker cp TestJson/test4.json spark-master:/opt/bitnami/spark/inputTest/
docker cp target/SIC-Integration-1.0-SNAPSHOT.jar spark-master:/opt/bitnami/spark/
docker exec -it spark-master bash
docker exec -it spark-master /opt/bitnami/spark/bin/spark-submit --packages io.delta:delta-spark_2.12:3.2.0 --master spark://spark-master:7077 --class org.example.Main /opt/bitnami/spark/SIC-Integration-1.0-SNAPSHOT.jar
 */

public class Main {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("StreamingTest").master("local[*]")
                .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
                .config("spark.jars.packages", "io.delta:delta-spark_2.13:3.1.0")
                .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
                .getOrCreate();

        DataInput inputStream = new DataInput(spark);
        DataNormalisation organise = new DataNormalisation(spark);
        DataPreparation prepare = new DataPreparation(spark);
        try {
            inputStream.startStream();
            organise.startTableUpdateStream();
            prepare.startPrepareInsectStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        Dataset<Row> origin = spark.read().schema(DataSchema.getOGSchema()).format("parquet").load("/opt/bitnami/spark/WP_3/Bronze/Data");
        Dataset<Row> card = spark.read().format("delta").load("/opt/bitnami/spark/WP_3/Silver/Data/yellowCardTable");
        Dataset<Row> insect = spark.read().format("delta").load("/opt/bitnami/spark/WP_3/Silver/Data/insectFindingTable");
        Dataset<Row> pop = spark.read().format("delta").load("/opt/bitnami/spark/WP_3/Gold/Data/insectPopulation");

        //origin.printSchema();
        origin.show();
        //card.printSchema();
        card.show();
        //insect.printSchema();
        insect.show();
        //pop.printSchema();
        pop.show();

        spark.stop();
        }
}