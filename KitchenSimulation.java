import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class KitchenSimulation {

    private static final Semaphore deliverySemaphore = new Semaphore(1);

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            new Thread(new CookingTask(i)).start();
        }
    }

    static class CookingTask implements Runnable {
        private final int id;
        private final Random random = new Random();

        public CookingTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                double cookingTime = calculateCookingTime();
                System.out.println("Dish ID: " + id + " (" + getDishName() + ") started cooking for " + String.format("%.2f", cookingTime) + " seconds.");

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < cookingTime * 1000) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    double percent = (elapsed / 100.0) / (cookingTime * 10) * 100;
                    System.out.printf("Dish ID: %d (%.2f%% cooked)\n", id, percent);
                    TimeUnit.MILLISECONDS.sleep(100);
                }

                System.out.println("Dish ID: " + id + " (" + getDishName() + ") is ready.");

                deliverySemaphore.acquire();
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    System.out.println("Dish ID: " + id + " (" + getDishName() + ") delivered.");
                } finally {
                    deliverySemaphore.release();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Dish ID: " + id + " was interrupted.");
            }
        }

        private double calculateCookingTime() {
            if (id % 2 == 1) {
                return 0.5 + (0.8 - 0.5) * random.nextDouble();
            } else {
                return 0.6 + (1.2 - 0.6) * random.nextDouble();
            }
        }

        private String getDishName() {
            return id % 2 == 1 ? "Sopa de Cebola" : "Lasanha a Bolonhesa";
        }
    }
}
