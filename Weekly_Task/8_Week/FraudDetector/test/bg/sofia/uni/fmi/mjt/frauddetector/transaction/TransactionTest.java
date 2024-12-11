package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    private static Transaction transaction;

    @BeforeAll
    static void setUp() {
        String transactionID = "trId1";
        String accountID = "accId1";
        double transactionAmount = 149.50;
        LocalDateTime transactionDate = LocalDateTime.of(2024, 9, 5, 11, 55, 2);
        String location = "Varna";
        Channel channel = Channel.ONLINE;
        transaction = new Transaction(transactionID, accountID, transactionAmount, transactionDate, location, channel);
    }

    @Test
    void testOfTransactionValid() {
        String toRead = "trId1,accId1,149.5,2024-09-05 11:55:02,Varna,Online";
        assertDoesNotThrow(() -> Transaction.of(toRead),"Must not throw an exception if csv format");
        Transaction testTr = Transaction.of(toRead);
        assertEquals(transaction, testTr, "Must return the same transaction when used of method");
    }

    @Test
    void testOfTransactionNull() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of(null),
                "Must throw IllegalArgumentException when null");
    }

    @Test
    void testOfTransactionCommasInvalid() {
        String toRead = "trId1 accId1,149.5,2024-09-05 11:55:02,Varna,Online";
        assertThrows(IllegalArgumentException.class, () -> Transaction.of(toRead));
    }
}
