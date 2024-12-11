package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class SmallTransactionsRule implements Rule {
    private final double weight;
    private final int countThreshold;
    private final double amountThreshold;

    public SmallTransactionsRule(int countThreshold, double amountThreshold, double weight) {
        this.countThreshold = countThreshold;
        this.amountThreshold = amountThreshold;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("transactions is null");
        }
        return transactions
                .parallelStream()
                .map(Transaction::transactionAmount)
                .filter(amount -> amount <= amountThreshold)
                .sequential()
                .count() >= countThreshold;
    }

    @Override
    public double weight() {
        return weight;
    }
}
