package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.command.Command;
import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

public class CommandExecutor {
    private static final String DISCONNECT = "disconnect";
    private static final String CREATE = "create-poll";
    private static final String VOTE = "submit-vote";
    private static final String LIST = "list-polls";

    private PollRepository pollRepository;

    public CommandExecutor(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    public String execute(Command cmd) {
        return switch (cmd.command()) {
            case CREATE -> createPoll(cmd.arguments());
            case VOTE -> vote(cmd.arguments());
            case LIST -> list(cmd.arguments());
            case DISCONNECT -> disconnect(cmd.arguments());
            default -> "{\"status\":\"ERROR\",\"message\":\"Unknown command\"}";
        };
    }

    private String createPoll(String[] args) {
        if (args.length < Poll.getMinOptionsCount() + 1) {
            return "{\"status\":\"ERROR\",\"message\":\"Usage: create-poll <question> <option-1> <option-2> " +
                    "[... <option-N>]\"}";
        }
        int todoID = pollRepository.addPoll(Poll.of(args));
        return "{\"status\":\"OK\",\"message\":\"Poll " + todoID + " created successfully.\"}";
    }

    private String list(String[] args) {
        if (args.length > 0) {
            return "{\"status\":\"ERROR\",\"message\":\"No arguments should be when listing\"}";
        }
        var polls = pollRepository.getAllPolls();
        if (polls.isEmpty()) {
            return "{\"status\":\"ERROR\",\"message\":\"No active polls available.\"}";
        }
        StringBuilder response = new StringBuilder("{\"status\":\"OK\",\"polls\":{");
        for (var entry : polls.entrySet()) {
            response.append("\"").append(entry.getKey()).append("\":").append(entry.getValue()).append(",");
        }
        response.deleteCharAt(response.length() - 1);
        return response.toString();
    }

    private String vote(String[] args) {
        if (args.length != 2 || !args[0].matches("-?\\d+")) {
            return "{\"status\":\"ERROR\",\"message\":\"Arguments must be exactly 2\"}";
        }
        String idStr = args[0];
        String option = args[1];
        int id = Integer.parseInt(idStr);
        if (!pollRepository.getAllPolls().containsKey(id)) {
            return "{\"status\":\"ERROR\",\"message\":\"Poll with ID " + id + " does not exist.\"}";
        }
        var currPoll = pollRepository.getPoll(id);
        if (!currPoll.vote(option)) {
            return "{\"status\":\"ERROR\",\"message\":\"Invalid option. Option " + option + " does not exist.\"}";
        } else {
            return "{\"status\":\"OK\",\"message\":\"Vote submitted successfully for option: " + option + "\"}";
        }
    }

    private String disconnect(String[] args) {
        if (args.length > 0) {
            return "{\"status\":\"ERROR\",\"message\":\"No arguments should be when disconnecting\"}";
        }
        return "{\"status\":\"OK\",\"message\":\"Disconnected successfully.\"}";
    }
}