package by.rafalovich.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {

    private static final Integer MAX_CAPACITY = 10;

    private final Lock lock = new ReentrantLock(true);
    private final Condition notEnoughProducts = lock.newCondition();
    private final Condition tooManyProducts = lock.newCondition();

    private int currentProductQuantity;

    public Warehouse(int currentProductQuantity) {
        this.currentProductQuantity = currentProductQuantity;
    }

    public void getProduct() throws InterruptedException {
        lock.lock();
        try {
            while (currentProductQuantity == 0) {
                System.out.println("Warehouse is empty. Waiting...");
                tooManyProducts.signalAll();
                notEnoughProducts.await();
            }
            currentProductQuantity--;
            tooManyProducts.signal();
            System.out.println("Customer bought 1 good");
            System.out.println("Current products count: " + currentProductQuantity);
        } finally {
            lock.unlock();
        }
    }

    public void produceProduct() throws InterruptedException {
        lock.lock();
        try {
            while (currentProductQuantity == MAX_CAPACITY) {
                System.out.println("Warehouse is full. Waiting...");
                notEnoughProducts.signalAll();
                tooManyProducts.await();
            }
            currentProductQuantity++;
            notEnoughProducts.signal();
            System.out.println("Producer added 1 good.");
            System.out.println("Current products count: " + currentProductQuantity);
        } finally {
            lock.unlock();
        }
    }
}
