package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

public class FrequencyRule implements Rule {
    private final double weight;
    private final TemporalAmount timeWindow;
    private final int transactionCountThreshold;

    public FrequencyRule(int transactionCountThreshold, TemporalAmount timeWindow, double weight) {
        if (timeWindow == null) {
            throw new IllegalArgumentException("timeWindow cannot be null");
        }
        this.weight = weight;
        this.timeWindow = timeWindow;
        this.transactionCountThreshold = transactionCountThreshold;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("transactions is null");
        }
        List<LocalDateTime> timeSorted = transactions
                .parallelStream()
                .map(Transaction::transactionDate)
                .sequential()
                .sorted()
                .toList();
        for (int i = 0; i <= transactions.size() - transactionCountThreshold; i++) {
            List<LocalDateTime> currWindow = timeSorted.subList(i, i + transactionCountThreshold);
            LocalDateTime currentMinimumDate = currWindow.get(transactionCountThreshold - 1).minus(timeWindow);
            if (!currWindow.getFirst().isBefore(currentMinimumDate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double weight() {
        return weight;
    }
}
