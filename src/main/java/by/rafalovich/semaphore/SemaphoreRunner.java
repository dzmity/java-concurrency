package by.rafalovich.semaphore;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SemaphoreRunner {

    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(2, true);

        List<Thread> threads = IntStream.range(0, 10)
                .mapToObj(i -> new WorkerThread(semaphore))
                .peek(Thread::start)
                .collect(Collectors.toList());

        Thread.sleep(5_000);

        threads.forEach(Thread::interrupt);
    }
}
