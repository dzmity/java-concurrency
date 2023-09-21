package by.rafalovich.blockingqueue;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class ConsumerThread extends Thread {

    private final BlockingQueue<Integer> blockingQueue;

    @Override
    public void run() {
        try {
            Integer good = blockingQueue.take();
            System.out.printf("%s consume %s from warehouse %n", getName(), good);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
