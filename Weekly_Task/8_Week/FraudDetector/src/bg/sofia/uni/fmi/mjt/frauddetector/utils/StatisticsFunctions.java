package bg.sofia.uni.fmi.mjt.frauddetector.utils;

import java.util.List;

public class StatisticsFunctions {
    public static double getAvgOfList(List<Double> list) {
        return list.parallelStream().reduce(0.0, Double::sum, Double::sum) / list.size();
    }

    public static double getStandardDeviation(List<Double> list) {
        double avg = getAvgOfList(list);
        double sumOfSquares = list
                .parallelStream()
                .reduce(0.0, (res, step) -> res + (step - avg) * (step - avg), Double::sum);
        return Math.sqrt(sumOfSquares / list.size());
    }
}
