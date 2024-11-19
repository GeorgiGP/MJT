package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class EventBusImpl implements EventBus {
    private final Map<Class<? extends Event<?>>, Set<Subscriber<?>>> subscribers;
    private final Map<Class<? extends Event<?>>, PriorityQueue<Event<?>>> events;

    public static class MyComparator<T extends Event<?>> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }

    public EventBusImpl() {
        subscribers = new HashMap<>();
        events = new HashMap<>();
    }

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null || subscriber == null) {
            throw new IllegalArgumentException("Event type and subscriber cannot be null");
        }
        if (!subscribers.containsKey(eventType)) {
            subscribers.put(eventType, new HashSet<>());
        }
        if (!events.containsKey(eventType)) {
            events.put(eventType, new PriorityQueue<>(new MyComparator<>()));
        }
        subscribers.get(eventType).add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
            throws MissingSubscriptionException {
        if (eventType == null || subscriber == null) {
            throw new IllegalArgumentException("Event type and subscriber cannot be null");
        }
        if (!subscribers.containsKey(eventType) || !subscribers.get(eventType).contains(subscriber)) {
            throw new MissingSubscriptionException("User is not subscribed");
        }
        subscribers.get(eventType).remove(subscriber);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (!subscribers.containsKey(event.getClass())) {
            subscribers.put((Class<T>)event.getClass(), new HashSet<>());
        }
        if (!events.containsKey(event.getClass())) {
            events.put((Class<T>)event.getClass() , new PriorityQueue<>(new MyComparator<>()));
        }
        for (Subscriber<?> subscriber : subscribers.get(event.getClass())) {
            ((Subscriber<T>)subscriber).onEvent(event);
        }
        events.get((Class<T>)event.getClass()).add(event);
    }

    @Override
    public void clear() {
        subscribers.clear();
        events.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from, Instant to) {
        if (eventType == null || from == null || to == null) {
            throw new IllegalArgumentException("Event type and from and to cannot be null");
        }
        Queue<Event<?>> events = new PriorityQueue<>(new MyComparator<>());
        if (!this.events.containsKey(eventType)) {
            return Collections.emptyList();
        }
        for (Event<?> event : this.events.get(eventType)) {
            if ((event.getTimestamp().isAfter(from) || event.getTimestamp().equals(from))
                    && event.getTimestamp().isBefore(to)) {
                events.add(event);
            }
        }
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(events);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        if (!subscribers.containsKey(eventType)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(subscribers.get(eventType));
    }
}