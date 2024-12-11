package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.FrequencyRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.LocationsRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.SmallTransactionsRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.ZScoreRule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionAnalyzerImplTest {
    private static List<Transaction> transactions;
    private static File fileHold;

    private static final String[] toRead = {
            "TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego,ATM",
            "TX000002,AC00455,376.24,2023-06-27 16:44:19,Houston,ATM",
            "TX000003,AC00019,126.29,2023-07-10 18:16:08,Mesa,Online"
    };

    @BeforeAll
    static void setUp() {
        transactions = new ArrayList<>();
        transactions.add(Transaction.of(toRead[0]));
        transactions.add(Transaction.of(toRead[1]));
        transactions.add(Transaction.of(toRead[2]));
        setUpFileToRead();
    }

    static void setUpFileToRead() {
        File dir = new File("test/examples");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        fileHold = new File(dir, "transactions.txt");
        try (Writer writer = new FileWriter(fileHold)) {
            writer.write("TransactionID,AccountID,TransactionAmount,TransactionDate,Location,Channel" + System.lineSeparator());
            writer.write("TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego,ATM" + System.lineSeparator());
            writer.write("TX000002,AC00455,376.24,2023-06-27 16:44:19,Houston,ATM" + System.lineSeparator());
            writer.write("TX000003,AC00019,126.29,2023-07-10 18:16:08,Mesa,Online");
        } catch (IOException e) {
            throw new IllegalArgumentException("Error with file");
        }
    }

    @Test
    void testTransactionAnalyzerNullReader() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionAnalyzerImpl(
                        null,
                        List.of(new LocationsRule(3, 0.5))));
    }

    @Test
    void testTransactionAnalyzerNullList() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionAnalyzerImpl(
                        new StringReader("fake"),
                        null));
    }

    @Test
    void testTransactionAnalyzerRulesWeightSumIsNotOne() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionAnalyzerImpl(
                        new StringReader("fake"),
                        List.of(
                                new LocationsRule(3, 0.5),
                                new SmallTransactionsRule(1, 2, 0.4)
                        )
                ), "Two Rules with weight 0.5 and weight 0.4 are invalid, their sum is not 1.0");
    }

    @Test
    void testConstructorValidReading() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertEquals(analyzer.allTransactions(), transactions);
    }

    @Test
    void testTransactionAnalyzerAccountIDValid() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertEquals(analyzer.allAccountIDs(), transactions.stream().map(Transaction::accountID).toList());
    }

    @Test
    void testTransactionCountByChannelValid() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        Map<Channel, Integer> map = new HashMap<>();
        map.put(Channel.ONLINE, 1);
        map.put(Channel.ATM, 2);
        assertEquals(map, analyzer.transactionCountByChannel());
    }

    @Test
    void testAmountSpentByUserValid() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertEquals(analyzer.amountSpentByUser("AC00128"), 14.09);
    }

    @Test
    void testAmountSpentByNullUser() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertThrows(IllegalArgumentException.class, () -> analyzer.amountSpentByUser(null));
    }

    @Test
    void testAmountSpentByBlankUser() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertThrows(IllegalArgumentException.class, () -> analyzer.amountSpentByUser("   "));
    }

    @Test
    void testTransactionsByUser() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertEquals(List.of(transactions.get(1)),analyzer.allTransactionsByUser("AC00455"));
    }

    @Test
    void testTransactionsByUserNull() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertThrows(IllegalArgumentException.class, () ->analyzer.allTransactionsByUser(null));
    }

    @Test
    void testTransactionsByUserBlank() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertThrows(IllegalArgumentException.class, () ->analyzer.allTransactionsByUser("  "));
    }

    @Test
    void testAccountRatingNull() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertThrows(IllegalArgumentException.class, () ->analyzer.accountRating(null));
    }

    @Test
    void testAccountRatingBlank() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(3, 0.5),
                        new SmallTransactionsRule(1, 2, 0.5)));

        assertThrows(IllegalArgumentException.class, () ->analyzer.accountRating("  "));
    }

    @Test
    void testAccountRatingValid() throws FileNotFoundException {
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold),
                List.of(new LocationsRule(0, 0.5),
                        new SmallTransactionsRule(1, 0.01, 0.5)));

        assertEquals(0.5, analyzer.accountRating("AC00128"));
    }


    @Test
    void testSortedRatings() throws FileNotFoundException {
        List<Rule> rules = List.of(
                new ZScoreRule(1.0, 0.3),
                new FrequencyRule(4, Period.ofWeeks(4), 0.25),
                new SmallTransactionsRule(1, 15.00, 0.45)
        );
        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(new FileReader(fileHold), rules);

        SortedMap<String, Double> toTest = analyzer.accountsRisk();

        int counter = 0;
        for (Map.Entry<String, Double> entry : toTest.entrySet()) {
            switch (++counter) {
                case 1:
                    assertEquals(entry.getKey(), "AC00128");
                    assertEquals(entry.getValue(), 0.45);
                    break;
                case 2:
                    assertEquals(entry.getKey(), "AC00019");
                    assertEquals(entry.getValue(), 0.0);
                    break;
                case 3:
                    assertEquals(entry.getKey(), "AC00455");
                    assertEquals(entry.getValue(), 0.0);
            }
        }
    }

}
