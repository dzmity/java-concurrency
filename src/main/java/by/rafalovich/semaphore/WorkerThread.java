package by.rafalovich.semaphore;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
public class WorkerThread extends Thread {

    private final Semaphore semaphore;

    @Override
    public void run() {
        while (!isInterrupted()) {
            doSomeWork();
        }
        System.out.println(getName() + " finished work using interrupted flag");
    }

    private void doSomeWork() {

        try {
            System.out.println(getName() + " is trying to get an access");
            semaphore.acquire();
            System.out.println(getName() + " got an access");
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            System.out.println(getName() + " was interrupted");
        } finally {
            semaphore.release();
            System.out.println(getName() + " returned access");
            Thread.currentThread().interrupt();
        }
    }
}
