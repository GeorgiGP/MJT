package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import java.util.LinkedList;
import java.util.Queue;

public class MyBlockingQueue<T> {
    private final Queue<T> queue;
    private final int capacity;

    public MyBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public MyBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public boolean isEmpty() {
        synchronized (queue) {
            return queue.isEmpty();
        }
    }

    public boolean isFull() {
        synchronized (queue) {
            return queue.size() == capacity;
        }
    }

    public boolean add(T element) {
        synchronized (queue) {
            if (!isFull()) {
                queue.add(element);
                queue.notifyAll();
                return true;
            }
            throw new IllegalStateException("Queue is full");
        }
    }

    public boolean offer(T element) {
        synchronized (queue) {
            if (!isFull()) {
                queue.add(element);
                queue.notifyAll();
                return true;
            }
            return false;
        }
    }

    public void put(T t) throws InterruptedException {
        synchronized (queue) {
            while (isFull()) {
                queue.wait();
            }
            queue.add(t);
            queue.notifyAll();
        }
    }

    public T take() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
            queue.notifyAll();
            return queue.remove();
        }
    }

    public T poll() {
        synchronized (queue) {
            return queue.poll();
        }
    }

    public T peek() {
        synchronized (queue) {
            return queue.peek();
        }
    }

    public int size() {
        synchronized (queue) {
            return queue.size();
        }
    }

    public int capacity() {
        return capacity;
    }

    public int remainingCapacity() {
        synchronized (queue) {
            return capacity - size();
        }
    }

}
