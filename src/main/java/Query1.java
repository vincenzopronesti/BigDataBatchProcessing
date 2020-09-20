import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import utils.ItemQuery1;
import utils.StatQuery1;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;

import static java.lang.Math.abs;

public class Query1 {

    private final static String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
//    private static String pathToFile = "https://bucket74519372592.s3.amazonaws.com/dpc-covid19-ita-andamento-nazionale.csv";

    private static String pathToFile = "hdfs://localhost:54310/flume/query1_input_data/*";
    private static String outputDir = "hdfs://localhost:54310/query1/res";

//    private static String pathToFile = "data/dpc-covid19-ita-andamento-nazionale.csv";
//    private static String outputDir = "data/query1/res";


    public static ItemQuery1 parseCSV(String csvLine) {
        List<CSVRecord> records = null;
        try {
            records = CSVFormat.DEFAULT.parse(new StringReader(csvLine)).getRecords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVRecord csvValues = records.get(0);

        LocalDate date = LocalDate.parse(csvValues.get(0), DateTimeFormatter.ofPattern(DATE_PATTERN));

        ItemQuery1 item = new ItemQuery1();
        // set index of the week
        item.setWeek(Integer.valueOf(date.get(WeekFields.ISO.weekOfWeekBasedYear())));
        item.setDay(Integer.valueOf(date.getDayOfWeek().getValue()));
        // set number of cured and swabs
        item.setStats(new StatQuery1(Double.valueOf(csvValues.get(9)), Double.valueOf(csvValues.get(12))));
        return item;

    }

    public static Tuple2<Integer, StatQuery1> createPair(ItemQuery1 item) {
        Integer week = item.getWeek();
        StatQuery1 statQuery1 = new StatQuery1(item.getStats().getCured(),
                item.getStats().getSwabs());
        return new Tuple2<Integer, StatQuery1> (week, statQuery1);
    }

    public static Tuple2<Integer, StatQuery1> slideWeek(Tuple2<Integer, StatQuery1> item) {
        return new Tuple2<>(item._1 + 1, item._2);
    }

    public static Tuple2<Integer, StatQuery1> computeStats(Tuple2<Integer, Tuple2<StatQuery1, StatQuery1>> item) {
        Double cured1 = item._2._1.getCured();
        Double swab1 = item._2._1.getSwabs();
        Double cured2 = item._2._2.getCured();
        Double swab2 = item._2._2.getSwabs();

        Double cured = abs((cured2 - cured1)) / 7.0;
        Double swab = abs((swab2 - swab1)) / 7.0;

        StatQuery1 statQuery1 = new StatQuery1(cured, swab);

        return new Tuple2<>(item._1, statQuery1);
    }

    public static void main(String[] args) {
        long startSpark = System.currentTimeMillis();

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> rawItem = sc.textFile(pathToFile).filter(line -> !line.contains("data"));

        JavaRDD<ItemQuery1> items = rawItem.map(line -> parseCSV(line));

        // keep only sundays
        JavaRDD<ItemQuery1> itemsFiltered = items.filter(item -> item.getDay() == 7);

        // create pairs <index of weeek, <cured, swabs>>
        JavaPairRDD<Integer, StatQuery1> itemsPair = itemsFiltered.mapToPair(item -> createPair(item));
        // create new RDD with pairs <index of weeek + 1, <cured, swabs>>
        JavaPairRDD<Integer, StatQuery1> itemsPairPlus = itemsPair.mapToPair(item -> slideWeek(item));

        // join the 2 RDDs based on week index
        JavaPairRDD<Integer, Tuple2<StatQuery1, StatQuery1>> joinedPairs = itemsPair.join(itemsPairPlus);

        // <week index, <avg cured, avg swabs>>
        JavaPairRDD<Integer, StatQuery1> computedStats = joinedPairs.mapToPair(item -> computeStats(item))
                .mapToPair(item -> new Tuple2<>(item._1 - 1, item._2))
                .sortByKey(true);

        JavaRDD<String> csvOutput = computedStats.map(stat -> "" + stat._1 + "," +
                stat._2.getCured() + "," + stat._2.getSwabs());


        long startTime = System.currentTimeMillis();

        csvOutput.saveAsTextFile(outputDir);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

//        try {
//            TimeUnit.MINUTES.sleep(12);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        sc.stop();

        long end = System.currentTimeMillis();
        long all = end-startSpark;

        System.out.println("duration all: " + all);
        System.out.println("duration: " + duration);
    }
}
