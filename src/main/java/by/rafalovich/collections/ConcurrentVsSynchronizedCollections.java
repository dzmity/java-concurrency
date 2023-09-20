package by.rafalovich.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentVsSynchronizedCollections {

    public static void main(String[] args) {
        // every modification operation create under the hood new array
        // => any threads use iterator will continue work with their 'previous' array
        List<Integer> concurrentList = new CopyOnWriteArrayList<>();

        // the same situation will lead to ConcurrentModificationException
        List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());

//        List<Integer> experimentalList = synchronizedList;
        List<Integer> experimentalList = concurrentList;

        for (int i = 0; i < 10; i++) {
            new Thread(generateWriteRunnable(experimentalList)).start();
            new Thread(generateReadRunnable(experimentalList)).start();
        }
    }

    private static Runnable generateReadRunnable(List<Integer> list) {
        return () -> {
            for (int value: list) {
                // some action using iterator
            }
        };
    }

    private static Runnable generateWriteRunnable(List<Integer> list) {
        return () -> {
            for (int i = 0; i < 10; i++) {
                list.add(new Random().nextInt());
            }
        };
    }
}
