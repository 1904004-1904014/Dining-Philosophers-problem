import java.util.Random;

public class Philosopher implements Runnable {
    private static final Random RANDOM = new Random();
    private final String name;
    private Fork leftFork;
    private Fork rightFork;
    private Table table;
    private boolean hasLeftFork = false;
    private boolean hasRightFork = false;

    public Philosopher(String name, Fork leftFork, Fork rightFork, Table table) {
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.table = table;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                think();
                if (pickUpForks()) {
                    eat();
                    putDownForks();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void think() throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(10001));  // Think for 0 to 10 seconds
    }

    private boolean pickUpForks() throws InterruptedException {
        if (leftFork.pickUp()) {
            hasLeftFork = true;
            Thread.sleep(4000);  // wait 4s before trying to pick up the right fork
            if (rightFork.pickUp()) {
                hasRightFork = true;
                return true;  // Successfully picked up both forks
            } else {
                leftFork.putDown();
                hasLeftFork = false;
            }
        }
        return false;
    }

    private void eat() throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(5000));  // Eat for 0 to 5 seconds
    }

    private void putDownForks() {
        if (hasRightFork) {
            rightFork.putDown();
            hasRightFork = false;
        }
        if (hasLeftFork) {
            leftFork.putDown();
            hasLeftFork = false;
        }
    }

    public String getName() {
        return name;
    }

    public void moveToNewTable(Table newTable, Fork newLeftFork, Fork newRightFork) {
        table.removePhilosopher(this);
        newTable.addPhilosopher(this);
        this.table = newTable;
        this.leftFork = newLeftFork;
        this.rightFork = newRightFork;
    }

    public boolean hasOneFork() {
        return hasLeftFork ^ hasRightFork; // XOR operation, true if exactly one fork is held
    }
}