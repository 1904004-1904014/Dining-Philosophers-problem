import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Table {
    private static final Random RANDOM = new Random();
    private final List<Philosopher> philosophers = new ArrayList<>();
    private final List<Fork> forks = new ArrayList<>();
    private final int capacity;
    private final int tableNumber;

    public Table(int capacity, int tableNumber) {
        this.capacity = capacity;
        this.tableNumber = tableNumber;
        for (int i = 0; i < capacity; i++) {
            forks.add(new Fork());
        }
    }

    public synchronized void addPhilosopher(Philosopher philosopher) {
        if (philosophers.size() < capacity) {
            philosophers.add(philosopher);
        }
    }

    public synchronized void removePhilosopher(Philosopher philosopher) {
        philosophers.remove(philosopher);
    }

    public synchronized boolean isFull() {
        return philosophers.size() == capacity;
    }

    public synchronized boolean isEmpty() {
        return philosophers.isEmpty();
    }

    public synchronized Philosopher getRandomPhilosopher() {
        if (!philosophers.isEmpty()) {
            return philosophers.get(RANDOM.nextInt(philosophers.size()));
        }
        return null;
    }

    public synchronized Fork getLeftFork(int index) {
        return forks.get(index);
    }

    public synchronized Fork getRightFork(int index) {
        return forks.get((index + 1) % capacity);
    }

    public synchronized boolean isDeadlocked() {
        try {
            Thread.sleep(50); // Add a small delay before checking for deadlock
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return philosophers.stream().allMatch(p -> p.hasOneFork());
    }

    public int getSize() {
        return philosophers.size(); 
    }

    public int getTableNumber() {
        return tableNumber;
    }
}