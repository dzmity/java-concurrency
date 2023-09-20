package by.rafalovich.lock;

public class ReentrantLockRunner {

    public static void main(String[] args) throws InterruptedException {
        Warehouse repository = new Warehouse(5);
        for (int i = 0; i < 10; i++) {
            new ConsumerThread(repository).start();
        }

        Thread.sleep(1_000);

        for (int i = 0; i < 20; i++) {
            new ProducerThread(repository).start();
        }

        Thread.sleep(3_000);

        for (int i = 0; i < 5; i++) {
            new ConsumerThread(repository).start();
        }
    }
}
