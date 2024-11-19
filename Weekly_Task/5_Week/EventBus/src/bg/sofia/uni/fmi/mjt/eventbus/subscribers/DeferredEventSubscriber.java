package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

public class DeferredEventSubscriber<T extends Event<?>> implements Subscriber<T>, Iterable<T> {

    private final PriorityQueue<T> events;

    public DeferredEventSubscriber() {
        Comparator<T> myComparator = new Comparator<>() {
            @Override
            public int compare(T e1, T e2) {
                if (e1.getPriority() == e2.getPriority()) {
                    return e1.getTimestamp().compareTo(e2.getTimestamp());
                }
                return e1.getPriority() - e2.getPriority();
            }
        };

        events = new PriorityQueue<>(myComparator);
    }

    /**
     * Store an event for processing at a later time.
     *
     * @param event the event to be processed
     * @throws IllegalArgumentException if the event is null
     */
    @Override
    public void onEvent(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        events.add(event);
    }

    /**
     * Get an iterator for the unprocessed events. The iterator should provide the events sorted by
     * their priority in descending order. Events with equal priority are ordered in ascending order
     * of their timestamps.
     *
     * @return an iterator for the unprocessed events
     */
    @Override
    public Iterator<T> iterator() {
        return events.iterator();
    }

    /**
     * Check if there are unprocessed events.
     *
     * @return true if there are unprocessed events, false otherwise
     */
    public boolean isEmpty() {
        return events.isEmpty();
    }
}
