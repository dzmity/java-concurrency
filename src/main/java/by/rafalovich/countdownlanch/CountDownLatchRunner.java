package by.rafalovich.countdownlanch;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchRunner {

    public static void main(String[] args) throws InterruptedException {
        int counter = 5;
        CountDownLatch countDownLatch = new CountDownLatch(counter);

        new TaxiThread(countDownLatch).start();

        Thread.sleep(2_000);

        for (int i = 0; i < counter; i++) {
            new FamilyMemberThread(countDownLatch).start();
        }
    }
}
