package by.rafalovich.countdownlanch;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public class FamilyMemberThread extends Thread {

    private final CountDownLatch countDownLatch;

    @Override
    public void run() {
        System.out.printf("%s started for preparing %n", getName());
        try {
            Thread.sleep( (int) (Math.random() * 10_000));
            System.out.printf("%s is ready %n", getName());
            countDownLatch.countDown();
            System.out.printf("%s is waiting for %s others %n", getName(), countDownLatch.getCount());
            countDownLatch.await();
            System.out.printf("%s continues its work. %n", getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
