package bg.sofia.uni.fmi.mjt.poll.server.repository;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class InMemoryPollRepository implements PollRepository {
    private final Map<Integer, Poll> polls;
    private int id;

    public InMemoryPollRepository() {
        this.polls = new TreeMap<>();
        this.id = 0;
    }

    @Override
    public int addPoll(Poll poll) {
        if (poll == null) {
            throw new IllegalArgumentException("Poll cannot be null");
        }
        polls.put(++id, poll);
        return id;
    }

    @Override
    public Poll getPoll(int pollId) {
        if (!polls.containsKey(pollId)) {
            throw new IllegalArgumentException("Poll with id " + pollId + " not found");
        }
        return polls.get(pollId);
    }

    @Override
    public Map<Integer, Poll> getAllPolls() {
        return Collections.unmodifiableMap(polls);
    }

    @Override
    public void clearAllPolls() {
        polls.clear();
    }
}
