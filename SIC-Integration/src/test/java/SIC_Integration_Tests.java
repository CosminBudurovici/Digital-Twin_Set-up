import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class SIC_Integration_Tests {

    private SparkSession spark;

    @Before
    public void setUp(){
        spark = SparkSession.builder()
                .master("local[*]")
                .appName("IntegrationTest")
                .config("spark.driver.extraJavaOptions", "--add-opens java.base/sun.nio.ch=ALL-UNNAMED")
                .config("spark.executor.extraJavaOptions", "--add-opens java.base/sun.nio.ch=ALL-UNNAMED")
                .getOrCreate();
    }

    @After
    public void stop(){
        if(spark != null){
            spark.stop();
        }
    }



    /*

    public static Collection<Object[]> exampleInput(){

        SparkSession temp = SparkSession.builder()
                .master("local[*]")
                .appName("temporary")
                .config("spark.driver.extraJavaOptions", "--add-opens java.base/sun.nio.ch")
                .config("spark.executor.extraJavaOptions", "--add-opens java.base/sun.nio.ch")
                .getOrCreate();

        Dataset<Row> data1 = temp.createDataFrame(Arrays.asList(
                new ExampleRow("1",3),
                new ExampleRow("2",7),
                new ExampleRow("3",14)
        ), ExampleRow.class);

        Dataset<Row> data2 = temp.createDataFrame(Arrays.asList(
                new ExampleRow("5",0)
        ), ExampleRow.class);

        Dataset<Row> data3 = temp.createDataFrame(Arrays.asList(
                new ExampleRow("1",3),
                new ExampleRow("2",7),
                new ExampleRow("3",14),
                new ExampleRow("4",45),
                new ExampleRow("5",87),
                new ExampleRow("6",125),
                new ExampleRow("7",239),
                new ExampleRow("8",1875)
        ), ExampleRow.class);

        temp.stop();

        return Arrays.asList(new Object[][]{
                {data1, 3},
                {data2, 1},
                {data3, 8}
        });

    }

    public static class ExampleRow {
        private String id;
        private int number;

        public ExampleRow() {}

        public ExampleRow(String id, int number){
            this.id = id;
            this.number = number;
        }

        public String getId(){
            return this.id;
        }

        public int getNumber(){
            return this.number;
        }
    }

    @ParameterizedTest
    @MethodSource("exampleInput")
    public void streamingTest(Dataset<Row> input, int expectedCount) throws StreamingQueryException, TimeoutException {

        Dataset<Row> data;

        StreamingQuery query = input.writeStream()
                .format("memory")
                .queryName("streamingTestTable")
                .outputMode("append")
                .trigger(Trigger.AvailableNow())
                .start();

        query.processAllAvailable();

        Dataset<Row> result = spark.sql("SELECT * FROM streamingTestTable");
        assertEquals(expectedCount,result.count());

        query.stop();
    }

    */
}
