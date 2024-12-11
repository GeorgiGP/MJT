package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import bg.sofia.uni.fmi.mjt.frauddetector.utils.StatisticsFunctions;

import java.util.List;

public class ZScoreRule implements Rule {
    private final double weight;
    private final double zScoreThreshold;

    public ZScoreRule(double zScoreThreshold, double weight) {
        this.zScoreThreshold = zScoreThreshold;
        this.weight = weight;
    }

    public double getZScoreOf(double element, List<Double> list) {
        double avg = StatisticsFunctions.getAvgOfList(list);
        double standartDeviation = StatisticsFunctions.getStandardDeviation(list);
        return (element - avg) / standartDeviation;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("transactions is null");
        }
        List<Double> list = transactions.stream().map(Transaction::transactionAmount).toList();
        double max = list.parallelStream()
                .reduce(0.0,
                        Math::max,
                        Math::max);
        return getZScoreOf(max, list) >= zScoreThreshold;
    }

    @Override
    public double weight() {
        return weight;
    }
}
