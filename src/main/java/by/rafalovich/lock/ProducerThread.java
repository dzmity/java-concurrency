package by.rafalovich.lock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProducerThread extends Thread {

    private final Warehouse warehouse;

    @Override
    public void run() {
        try {
            warehouse.produceProduct();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
