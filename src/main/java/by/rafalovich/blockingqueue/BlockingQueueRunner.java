package by.rafalovich.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueRunner {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> warehouse = new ArrayBlockingQueue<>(5);
        for (int i = 0; i < 10; i++) {
            new ConsumerThread(warehouse).start();
        }

        System.out.printf("Waiting for producers...%n%n");
        Thread.sleep(1_000);

        for (int i = 0; i < 20; i++) {
            new ProducerThread(warehouse).start();
        }

        Thread.sleep(3_000);
        System.out.printf("%n Waiting for consumers...%n%n");

        for (int i = 0; i < 5; i++) {
            new ConsumerThread(warehouse).start();
        }
    }
}
