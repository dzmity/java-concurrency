package by.rafalovich.blockingqueue;

import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class ProducerThread extends Thread {

    private final BlockingQueue<Integer> blockingQueue;

    @Override
    public void run() {
        try {
            int good = new Random().nextInt(100);
            blockingQueue.put(good);
            System.out.printf("%s add %s to warehouse %n", getName(), good);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
