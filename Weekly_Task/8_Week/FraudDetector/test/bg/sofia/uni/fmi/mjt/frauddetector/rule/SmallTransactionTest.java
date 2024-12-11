package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SmallTransactionTest {
    private static List<Transaction> transactions;
    private static final String[] toRead = {
            "TX000001,AC00128,0.09,2023-09-11 16:30:01,San Diego,ATM",
            "TX000002,AC00128,0.24,2023-09-11 16:31:19,San Diego,ATM",
            "TX000003,AC00128,0.99,2023-09-11 16:31:08,San Diego,Online",
            "TX000004,AC00128,1.29,2023-07-10 18:39:08,Houston,Online"
    };

    @BeforeAll
    static void setUp() {
        transactions = new ArrayList<>();
        transactions.add(Transaction.of(toRead[0]));
        transactions.add(Transaction.of(toRead[1]));
        transactions.add(Transaction.of(toRead[2]));
        transactions.add(Transaction.of(toRead[3]));
    }

    @Test
    void testSmallTransactionRuleViolation() {
        Rule r = new SmallTransactionsRule(3,  0.99 ,0.5);
        assertTrue(r.applicable(transactions));
    }

    @Test
    void testSmallTransactionRuleValid() {
        Rule r = new SmallTransactionsRule(3, 0.98 ,0.5);
        assertFalse(r.applicable(transactions));
    }

    @Test
    void testSmallTransactionRuleNull() {
        Rule r = new SmallTransactionsRule(3, 1 ,0.5);
        assertThrows(IllegalArgumentException.class,() -> r.applicable(null),
                "Should throw IllegalArgumentException if transactions are null");
    }
}
