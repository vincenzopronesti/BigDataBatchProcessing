import exception.ContinentNotFound;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import utils.Continent;
import utils.ContinentFinder;
import utils.StatQuery2;
import utils.StateQuery2;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Query2 {

//    private static String pathToFile = "data/time_series_covid19_confirmed_global.csv";
//    private static String outputDir = "data/query2/";

    private static String pathToFile = "hdfs://localhost:54310/flume/query2_input_data/*";
    private static String outputDir = "hdfs://localhost:54310/query2/res";

    public static StateQuery2 parseCSV(String csvLine) {
        List<CSVRecord> records = null;
        try {
            records = CSVFormat.DEFAULT.parse(new StringReader(csvLine)).getRecords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVRecord csvValues = records.get(0);

        StateQuery2 state = new StateQuery2();
        if (csvValues.get(0) == null || csvValues.get(0).equals("")) {
            state.setName(csvValues.get(1));
        } else {
            state.setName(csvValues.get(0));
        }

        Double lat = Double.valueOf(csvValues.get(2));
        state.setLatitude(lat);
        Double lon = Double.valueOf(csvValues.get(3));
        state.setLongitude(lon);

        int days = csvValues.size() - 4 - ((csvValues.size()-4)%7);
        Integer[] cases = new Integer[days];
        for (int i=0; i<days; i++) {
            cases[i] = Integer.valueOf(csvValues.get(i + 4));
        }

        Integer[] increments = new Integer[days];
        for (int i=0; i<days; i++) {
            if (i == 0) {
                increments[i] = cases[i];
            } else {
                int inc = cases[i] - cases[i - 1];
                if (inc < 0)
                    increments[i] = 0;
                else
                    increments[i] = inc;
            }
        }
        state.setCases(increments);

        return state;
    }

    private static Tuple2<Double, StateQuery2> computeTrendlineCoeff(StateQuery2 itm) {
        SimpleRegression simpleRegression = new SimpleRegression();
        for (int i=0; i<itm.getCases().length; i++) {
            simpleRegression.addData(i+1, itm.getCases()[i]);
        }
        double slope = simpleRegression.getSlope();
        return new Tuple2<>(slope, itm);
    }

    private static Tuple2<Continent, StateQuery2> detectContinent(StateQuery2 state) {
        Continent continent = null;
        try {
            ContinentFinder finder = new ContinentFinder();
            continent = finder.getContinent(state.getLatitude(), state.getLongitude());
        } catch (ContinentNotFound e) {
            e.printStackTrace();
        }
        return new Tuple2<>(continent, state);
    }

    private static Integer[] reduceStates(Integer[] casesState1, Integer[] casesState2) {
        Integer[] summedCases = new Integer[casesState1.length];
        for (int i = 0; i < casesState1.length; i++) {
            summedCases[i] = casesState1[i] + casesState2[i];
        }
        return summedCases;
    }

    public static void main(String[] args) {
//        long startSpark = System.currentTimeMillis();

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query2");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> rawItem = sc.textFile(pathToFile).filter(line -> !line.contains("Province"));

        JavaRDD<StateQuery2> items = rawItem.map(line -> parseCSV(line));

        // create <trendline coeff, state info> and sort from worse to better country
        JavaPairRDD<Double, StateQuery2> trendlineCoeffAndState = items.mapToPair(itm -> computeTrendlineCoeff(itm))
                .sortByKey(false);

        // assign index, keep only the first 100 countries, discard trendline coefficient
        JavaRDD<StateQuery2> mostAffectedState = trendlineCoeffAndState.zipWithIndex()
                .filter(itm -> itm._2 < 100)
                .map(itm -> itm._1._2);

        // map countries to continents
        JavaPairRDD<Continent, StateQuery2> continentState = mostAffectedState.mapToPair(itm -> detectContinent(itm));

        // map each continent to the sequence of cases in a country
        JavaPairRDD<Continent, Integer[]> continentCases = continentState
                .mapToPair(itm -> new Tuple2<>(itm._1,itm._2.getCases() ));

//        // sum sequences of all countries inside a continent
//        <continent, num. of total cases for each day>
        JavaPairRDD<Continent, Integer[]> continentStates = continentCases
                .reduceByKey((casesState1, casesState2) -> reduceStates(casesState1, casesState2));

        // <continent, <week index, cases[]>>
        JavaPairRDD<Continent, Tuple2<Integer, Integer[]>> continentCasesPerWeek = continentStates
                .flatMapToPair(itm -> splitWeeks(itm));

        // <continent, <week index, week stats>>
        JavaPairRDD<Continent, Tuple2<Integer, StatQuery2>> finalStats = continentCasesPerWeek
                .mapToPair(itm -> computeStats(itm)).cache();

        long exectime = 0;

        // build an RDD for each continent
        Map<Continent, JavaPairRDD<Integer, StatQuery2>> continentStats = new HashMap<>();
        for (Continent continent : Continent.values()) {
            JavaPairRDD<Integer, StatQuery2> singleContinent = finalStats.filter((itm) -> {
                return itm._1 == continent;
            }).mapToPair(itm -> new Tuple2<>(itm._2._1, itm._2._2)).sortByKey(true);
            continentStats.put(continent, singleContinent);

            JavaRDD<String> csvOutput = singleContinent.map(stat -> "" + stat._1 + "," +
                    stat._2.getAvg() + "," + stat._2.getMin() + "," + stat._2.getMax() +
                    "," + stat._2.getStandardDev());

            long startexec = System.currentTimeMillis();

            csvOutput.saveAsTextFile(outputDir + continent.name());

            long endexec = System.currentTimeMillis();
            exectime += endexec - startexec;
        }

//        try {
//            TimeUnit.SECONDS.sleep(120);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        sc.stop();

//        long end = System.currentTimeMillis();
//        long all = end-startSpark;

//        System.out.println("duration all: " + all);
//        System.out.println("duration: " + exectime);
    }

    private static Iterator<Tuple2<Continent, Tuple2<Integer, Integer[]>>> splitWeeks(Tuple2<Continent, Integer[]> itm) {
        List<Tuple2<Continent, Tuple2<Integer, Integer[]>>> res = new ArrayList<>();
        int days = itm._2.length;
        for (int i=0; i < days/7; i++) {
            Integer[] currentWeek = new Integer[7];
            for (int j = 0; j < 7; j++) {
                currentWeek[j] = itm._2[7 * i + j];
            }
            Tuple2<Integer, Integer[]> weekItm = new Tuple2<>(i + 1, currentWeek);
            Tuple2<Continent, Tuple2<Integer, Integer[]>> curr = new Tuple2<>(itm._1, weekItm);
            res.add(curr);
        }
        return res.iterator();
    }

    private static Tuple2<Continent, Tuple2<Integer, StatQuery2>> computeStats(Tuple2<Continent, Tuple2<Integer, Integer[]>> itm) {
        StatQuery2 weekStats = new StatQuery2();

        // Get a DescriptiveStatistics instance
        DescriptiveStatistics stats = new DescriptiveStatistics();

        // Add the data from the array
        for(int i = 0; i < itm._2._2.length; i++) {
            stats.addValue(itm._2._2[i]);
        }

        // Compute some statistics
        weekStats.setMin(stats.getMin());
        weekStats.setMax(stats.getMax());
        weekStats.setAvg(stats.getMean());
        weekStats.setStandardDev(stats.getStandardDeviation());

        return new Tuple2<>(itm._1, new Tuple2<>(itm._2._1, weekStats));
    }
}
