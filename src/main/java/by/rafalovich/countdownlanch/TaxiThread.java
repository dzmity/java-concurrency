package by.rafalovich.countdownlanch;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TaxiThread extends Thread {

    private final CountDownLatch countDownLatch;

    @Override
    public void run() {
        try {
            System.out.println("Taxi thread started");
            countDownLatch.await(4, TimeUnit.SECONDS);
            System.out.println("Taxi thread didn't wait for all and continued its work.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
