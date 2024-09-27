import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophersSimulation {
    private static final int NUM_TABLES = 6;
    private static final int PHILOSOPHERS_PER_TABLE = 5;
    private static final long MINIMUM_RUNTIME_MS = 30000; // 30 seconds minimum runtime

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
        for (Thread thread : philosopherThreads) {
            thread.start();
        }

        long startTime = System.currentTimeMillis();
        List<String> movedPhilosophers = new ArrayList<>();

        while (true) {
            for (int i = 0; i < NUM_TABLES - 1; i++) {
                Table currentTable = tables.get(i);
                Table nextTable = tables.get(i + 1);
                
                if (currentTable.getSize() == PHILOSOPHERS_PER_TABLE && nextTable.getSize() < PHILOSOPHERS_PER_TABLE) {
                    Philosopher movingPhilosopher = currentTable.getRandomPhilosopher();
                    int emptyIndex = nextTable.getSize();
                    movingPhilosopher.moveToNewTable(
                        nextTable,
                        nextTable.getLeftFork(emptyIndex),
                        nextTable.getRightFork(emptyIndex)
                    );
                    movedPhilosophers.add(movingPhilosopher.getName());
                    break;
                }
            }
            
            if (tables.get(NUM_TABLES - 1).getSize() == PHILOSOPHERS_PER_TABLE && 
                System.currentTimeMillis() - startTime >= MINIMUM_RUNTIME_MS) {
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime) / 1000;
                System.out.println("Time taken: " + duration + " seconds");
                System.out.println("Sequence of philosophers moved to the sixth table: " + String.join(", ", movedPhilosophers));
                
                for (Thread thread : philosopherThreads) {
                    thread.interrupt();
                }
                return;
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}