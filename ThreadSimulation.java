import java.util.Random;
import java.util.concurrent.Semaphore;

public class ThreadSimulation {
    private static final Semaphore dbSemaphore = new Semaphore(1);

    public static void main(String[] args) {
        for (int i = 1; i <= 21; i++) {
            new Thread(new Task(i)).start();
        }
    }

    static class Task implements Runnable {
        private final int id;
        private final Random random = new Random();

        public Task(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Thread ID: " + id + " starting...");

                if (id % 3 == 1) {
                    executeTypeA();
                } else if (id % 3 == 2) {
                    executeTypeB();
                } else if (id % 3 == 0) {
                    executeTypeC();
                }

                System.out.println("Thread ID: " + id + " finished.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread ID: " + id + " interrupted.");
            }
        }

        private void executeTypeA() throws InterruptedException {
            performCalculations(0.2, 1.0);
            dbSemaphore.acquire();
            try {
                performDatabaseTransaction(1);
                performCalculations(0.2, 1.0);
                performDatabaseTransaction(1);
            } finally {
                dbSemaphore.release();
            }
        }

        private void executeTypeB() throws InterruptedException {
            performCalculations(0.5, 1.5);
            dbSemaphore.acquire();
            try {
                performDatabaseTransaction(1.5);
                performCalculations(0.5, 1.5);
                performDatabaseTransaction(1.5);
                performCalculations(0.5, 1.5);
                performDatabaseTransaction(1.5);
            } finally {
                dbSemaphore.release();
            }
        }

        private void executeTypeC() throws InterruptedException {
            performCalculations(1, 2);
            dbSemaphore.acquire();
            try {
                performDatabaseTransaction(1.5);
                performCalculations(1, 2);
                performDatabaseTransaction(1.5);
                performCalculations(1, 2);
            } finally {
                dbSemaphore.release();
            }
        }

        private void performCalculations(double minSec, double maxSec) throws InterruptedException {
            double duration = minSec + (maxSec - minSec) * random.nextDouble();
            System.out.println("Thread ID: " + id + " performing calculations for " + duration + " seconds.");
            Thread.sleep((long) (duration * 1000));
        }

        private void performDatabaseTransaction(double sec) throws InterruptedException {
            System.out.println("Thread ID: " + id + " performing database transaction for " + sec + " seconds.");
            Thread.sleep((long) (sec * 1000));
        }
    }
}
