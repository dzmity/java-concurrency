package by.rafalovich.lock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsumerThread extends Thread {

    private final Warehouse warehouse;

    @Override
    public void run() {
        try {
            warehouse.getProduct();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
