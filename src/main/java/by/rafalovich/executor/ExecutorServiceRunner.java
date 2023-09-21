package by.rafalovich.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceRunner {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 0. General info about Executors
        // diagram - https://www.baeldung.com/wp-content/uploads/2016/08/2016-08-10_10-16-52-1024x572.png
        // The Java ExecutorService interface represents an asynchronous execution mechanism
        //  which is capable of executing tasks concurrently in the background

        // Sync vs Async discussion

        // Executor  API - https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html
        // Executor is normally used instead of explicitly creating threads.
        // For example, rather than invoking new Thread(new(RunnableTask())).start()
        Executor executor = Executors.newSingleThreadExecutor(); // пока не задумываемся о том, как создается. Рассмотрим далее
        executor.execute(() -> System.out.println("1 - Hello World from " + Thread.currentThread().getName()));
        executor.execute(() -> System.out.println("2 - Hello World from " + Thread.currentThread().getName()));
        executor.execute(() -> System.out.println("3 - Hello World from " + Thread.currentThread().getName()));

        // Просто выполнять в параллель - это недостаточно. Хотелось бы получать информацию о состоянии и результатах
        // выполнения потока + как-то управлять корректным закрытием по окончанию работы всех потоков в экзекуторе
        // ExecutorService API  - controlling the progress of the tasks and managing the termination of the service
        // ExecutorService API - https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html

        // 1. Callable interface vs Runnable interface (show diff)
        // represents an asynchronous task which can be executed by a separate thread.
        // For instance, it is possible to submit a Callable object to a ExecutorService which will execute it asynchronously.
        // http://tutorials.jenkov.com/java-util-concurrent/java-callable.html
        Callable<String> firstCallable = () -> "Hello world callable";
        Callable<String> secondCallable = () -> {
            System.out.println("Callable started");
            TimeUnit.MILLISECONDS.sleep(300L);
            return "Callable finished";
        };

        // 2. Future interface
        // represents the result of an asynchronous computation. When the asynchronous task is created, a Future object is returned.
        // This Future object functions as a handle to the result of the asynchronous task.
        // ExecutorService, return a Java Future object from some of their methods
        // Docs - https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Future.html
        // http://tutorials.jenkov.com/java-util-concurrent/java-future.html
        ExecutorService executorService = (ExecutorService) executor;
        // submit() -  submits a Callable or a Runnable task to an ExecutorService and returns a result of type Future
        // submit Runnable
        Future<?> runnableFuture = executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Hello lesson 29");
        });
        // get  - use to wait until the submitted task is finished and the value is returned
        System.out.println(runnableFuture.get());
        runnableFuture = executorService.submit(() -> System.out.println("Hello lesson 29"), "12345");
        System.out.println(runnableFuture.get());
        // submit Callable
        Future<String> callableFuture = executorService.submit(secondCallable);
        System.out.println(callableFuture.get());
        // future.get(200, TimeUnit.MILLISECONDS); - TimeoutException will be thrown

        // isDone // isCancel // cancel
        if (callableFuture.isDone()) {
            String result = callableFuture.get();
        } else {
            callableFuture.cancel(true);
        }

        // invokeAny() -  assigns a collection of tasks to an ExecutorService, causing each to run,
        // and returns the result of a successful execution of one task (if there was a successful execution):
        // If one of the tasks complete (or throws an exception), the rest of the Callable's are cancelled.
        Set<Callable<String>> callables = new HashSet<>();
        callables.add(() -> {
            System.out.println("1");
            return "Task 1";
        });
        callables.add(() -> {
            System.out.println("2");
            return "Task 2";
        });
        callables.add(() -> {
            System.out.println("3");
            return "Task 3";
        });

        String result = executorService.invokeAny(callables);
        System.out.println("result = " + result);

        // invokeAll() - assigns a collection of tasks to an ExecutorService, causing each to run,
        // and returns the result of all task executions in the form of a list of objects of type Future:
        List<Future<String>> futures = executorService.invokeAll(callables);
        for (Future<String> future : futures) {
            System.out.println("future.get = " + future.get());
        }


        // Factory Methods
//        executorService = Executors.newSingleThreadExecutor();
        executorService = Executors.newFixedThreadPool(10);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.schedule(secondCallable, 1, TimeUnit.SECONDS);
        // если выполнение таски займет больше времени чем период - период увеличивается
        scheduledExecutorService.scheduleWithFixedDelay(() -> System.out.println("123"), 100, 150, TimeUnit.MILLISECONDS);
//        executorService = Executors.newCachedThreadPool();
        // Directly Create an ExecutorService
        executorService = new ThreadPoolExecutor(4, 10, 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());


//        In general, the ExecutorService will not be automatically destroyed when there is no task to process.
//        It will stay alive and wait for new work to do.
//        In some cases this is very helpful
//        such as when an app needs to process tasks that appear on an irregular basis
//        or the task quantity is not known at compile time.
        // But an app could reach its end but not be stopped because a waiting ExecutorService will cause the JVM to keep running.
        // shutdown() VS shutdownNow() - both don't guarantee that all the running threads will be stopped at the same time

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        // how it works + picture in the resources
        // https://www.baeldung.com/thread-pool-java-and-guava#2-threadpoolexecutor
        // ThreadPoolExecutor Docs - https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
        // Fork join Pool или автоматическая декомпозиция задачи
    }
}
