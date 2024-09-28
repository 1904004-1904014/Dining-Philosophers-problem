import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophersSimulation {
    private static final int NUM_TABLES = 6;
    private static final int PHILOSOPHERS_PER_TABLE = 5;
    private static final long DEADLOCK_CHECK_INTERVAL_MS = 100;

    public static void main(String[] args) {
        List<Table> tables = new ArrayList<>();
        for (int i = 0; i < NUM_TABLES; i++) {
            tables.add(new Table(PHILOSOPHERS_PER_TABLE, i));
        }

        List<Thread> philosopherThreads = new ArrayList<>();
        char philosopherName = 'A';
        for (int i = 0; i < NUM_TABLES - 1; i++) {
            Table table = tables.get(i);
            for (int j = 0; j < PHILOSOPHERS_PER_TABLE; j++) {
                Philosopher philosopher = new Philosopher(
                    String.valueOf(philosopherName++),
                    table.getLeftFork(j),
                    table.getRightFork(j),
                    table
                );
                table.addPhilosopher(philosopher);
                Thread thread = new Thread(philosopher);
                philosopherThreads.add(thread);
            }
        }

        System.out.println("Starting simulation...");
        long startTime = System.currentTimeMillis();
        for (Thread thread : philosopherThreads) {
            thread.start();
        }

        List<String> movedPhilosophers = new ArrayList<>();
        Table lastTable = tables.get(NUM_TABLES - 1);

        while (true) {
            try {
                Thread.sleep(DEADLOCK_CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < NUM_TABLES - 1; i++) {
                Table currentTable = tables.get(i);
                if (currentTable.isDeadlocked() && currentTable.getSize() > 0 && lastTable.getSize() < PHILOSOPHERS_PER_TABLE) {
                    Philosopher movingPhilosopher = currentTable.getRandomPhilosopher();
                    int emptyIndex = lastTable.getSize();
                    movingPhilosopher.moveToNewTable(
                        lastTable,
                        lastTable.getLeftFork(emptyIndex),
                        lastTable.getRightFork(emptyIndex)
                    );
                    movedPhilosophers.add(movingPhilosopher.getName());
                    System.out.println("Philosopher " + movingPhilosopher.getName() + " moved to the 6th table.");
                    break;
                }
            }

            if (lastTable.getSize() == PHILOSOPHERS_PER_TABLE && lastTable.isDeadlocked()) {
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime) / 1000;
                System.out.println("Simulation completed!");
                System.out.println("Time taken for 6th table to deadlock: " + duration + " seconds");
                System.out.println("Last philosopher moved to the 6th table: " + movedPhilosophers.get(movedPhilosophers.size() - 1));
                
                for (Thread thread : philosopherThreads) {
                    thread.interrupt();
                }
                break;
            }
        }
    }
}