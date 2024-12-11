package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

public record Transaction(String transactionID, String accountID, double transactionAmount,
                          LocalDateTime transactionDate, String location, Channel channel) {
    private static final String SEPARATOR = ",";
    private static final int COUNT_OF_SEPARATORS = 5;

    public static Transaction of(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        String[] fields = line.split(SEPARATOR);
        if (fields.length != COUNT_OF_SEPARATORS + 1) {
            throw new IllegalArgumentException("Line contains invalid fields");
        }
        int idx = 0;
        String transactionID = fields[idx++];
        String accountID = fields[idx++];
        double transactionAmount = Double.parseDouble(fields[idx++]);
        LocalDateTime transactionDate = LocalDateTime.parse(fields[idx++],
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String location = fields[idx++];
        Channel channel = Channel.valueOf(fields[idx].toUpperCase());
        return new Transaction(transactionID, accountID, transactionAmount, transactionDate, location, channel);
    }
}
