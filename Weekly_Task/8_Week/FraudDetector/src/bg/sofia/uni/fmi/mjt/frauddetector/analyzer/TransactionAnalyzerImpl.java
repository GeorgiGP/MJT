package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.exceptions.InvalidFileFormat;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TransactionAnalyzerImpl implements TransactionAnalyzer {
    private final List<Rule> rules;
    private final List<Transaction> transactions;
    private static final double EPSILON = 1e-9;
    public TransactionAnalyzerImpl(Reader reader, List<Rule> rules) {
        if (reader == null || rules == null) {
            throw new IllegalArgumentException("reader and rules cannot be null");
        }
        if (Math.abs(rules.parallelStream().map(Rule::weight).reduce(0.0, Double::sum, Double::sum) - 1) >= EPSILON) {
            throw new IllegalArgumentException("sum of rules must have exactly 1.0 weight");
        }
        transactions = new ArrayList<>();
        String line;
        BufferedReader buffReader = new BufferedReader(reader);
        try {
            buffReader.readLine();
            while ((line = buffReader.readLine()) != null) {
                transactions.add(Transaction.of(line));
            }
        } catch (IOException e) {
            throw new InvalidFileFormat("File to read is not valid");
        }
        this.rules = rules;
    }

    @Override
    public List<Transaction> allTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public List<String> allAccountIDs() {
        return transactions.parallelStream().map(Transaction::accountID).sequential().distinct().toList();
    }

    @Override
    public Map<Channel, Integer> transactionCountByChannel() {
        Map<Channel, Integer> counts = new HashMap<>();
        for (Channel channel : Channel.values()) {
            long occurs = transactions.parallelStream()
                    .filter(transaction -> transaction.channel().equals(channel))
                    .count();
            if (occurs > 0) {
                counts.put(channel, (int)occurs);
            }
        }
        return counts;
    }

    @Override
    public double amountSpentByUser(String accountID) {
        if (accountID == null || accountID.isBlank()) {
            throw new IllegalArgumentException("accountID is invalid");
        }
        return transactions.parallelStream().filter(t -> t.accountID().equals(accountID))
                .reduce(0.0, (sum, t) -> sum + t.transactionAmount(), Double::sum);
    }

    @Override
    public List<Transaction> allTransactionsByUser(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("accountID is invalid");
        }
        return transactions.parallelStream().filter(t -> t.accountID().equals(accountId)).toList();
    }

    @Override
    public double accountRating(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("accountID is invalid");
        }
        List<Transaction> accountTransactions = allTransactionsByUser(accountId);
        return rules.parallelStream()
                .reduce(0.0,
                        (rating, rule) -> rating + (rule.applicable(accountTransactions) ? rule.weight() : 0.0),
                        Double::sum);
    }

    @Override
    public SortedMap<String, Double> accountsRisk() {
        List<String> ids = allAccountIDs();
        Map<String, Double> risks = ids.stream()
                .collect(Collectors.toMap(
                        accountId -> accountId,
                        this::accountRating
                ));

        SortedMap<String, Double> result = new TreeMap<>(
                (key1, key2) -> {
                    int comparison = risks.get(key2).compareTo(risks.get(key1));
                    return comparison == 0 ? key1.compareTo(key2) : comparison;
                }
        );
        result.putAll(risks);
        return result;
    }
}
