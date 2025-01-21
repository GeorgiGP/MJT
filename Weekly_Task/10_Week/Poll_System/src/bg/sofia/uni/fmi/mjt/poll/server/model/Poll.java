package bg.sofia.uni.fmi.mjt.poll.server.model;

import java.util.HashMap;
import java.util.Map;

public record Poll(String question, Map<String, Integer> options) {
    private static final int MIN_OPTIONS = 2;
    public Poll {
        if (question == null || question == null) {
            throw new IllegalArgumentException("question or options are null");
        }
        if (options.size() < MIN_OPTIONS) {
            throw new IllegalArgumentException("options must have at least " + MIN_OPTIONS + " options");
        }
    }

    public void clearOptionsCount() {
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            entry.setValue(0);
        }
    }

    public boolean vote(String option) {
        if (!options.containsKey(option)) {
            return false;
        }
        options.replace(option, options.get(option) + 1);
        return true;
    }

    public static int getMinOptionsCount() {
        return MIN_OPTIONS;
    }

    public static Poll of(String[] args) {
        if (args == null || args.length < MIN_OPTIONS + 1) {
            throw new IllegalArgumentException("options must have at least " + MIN_OPTIONS + " options and not null");
        }
        String question = args[0];
        Map<String, Integer> options = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            options.put(args[i], 0);
        }
        return new Poll(question, options);
    }

    @Override
    public String toString() {
        String toReturn = "{\"question\":\"" + question + "\",\"options\":{";
        StringBuilder builder = new StringBuilder(toReturn);
        if (!options.isEmpty()) {
            for (var entry : options.entrySet()) {
                builder.append("\"").append(entry.getKey()).append("\":").append(entry.getValue()).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString() + "}}";
    }
}