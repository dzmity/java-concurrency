package by.rafalovich.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompletableFutureRunner {
    // https://www.baeldung.com/java-completablefuture
    // https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // complete method
        System.out.println(calculateAsync().get());

        // -- completedFuture(result)
        Future<String> predefinedFuture = CompletableFuture.completedFuture("Future is predefined.");
        System.out.println("Result: " + predefinedFuture.get());


        // ----------------------------Executing some code asynchronously
        //
        // -- runAsync(runnable)
        CompletableFuture.runAsync(() -> System.out.println("Runnable instance from " + getName()));
        // -- supplyAsync(supplier)
        CompletableFuture<String> completableFuture =
                CompletableFuture.supplyAsync(() -> "Supplier instance from " + getName());


        // ----------------------Processing Results of Asynchronous Computations
        //
        // -- thenApply(function) - map analog  - (run in the current thread)
        // -- thenApplyAsync(function) - map analog  - (run in the thread from async threadPool)
        CompletableFuture<String> future = completableFuture.thenApply(previousResult ->
                previousResult + ". Then apply function from " + getName());
        System.out.println("Result: " + future.get());
        // -- thenAccept(consumer) - (run in the current thread)
        // -- thenAcceptAsync - (run in the thread from async threadPool)
        CompletableFuture<Void> future2 = completableFuture.thenAccept(previousResult ->
                System.out.printf("%s. Then consume by consumer from %s%n", previousResult, getName()));
        System.out.println("Result: " + future2.get());
        // -- thenRun(runnable) - (run in the current thread)
        // -- thenRunAsync(runnable) - (run in the thread from async threadPool)
        CompletableFuture<Void> future3 = completableFuture
                .thenRun(() -> System.out.println("Then run runnable from " + getName()));
        System.out.println("Result: " + future3.get());


        // ------------------- Chaining and combining futures
        //
        // -- thenCompose(completableFuture) == flatMap analog - sequential computation
        // -- thenComposeAsync(completableFuture) == flatMap analog - sequential computation
        CompletableFuture<String> composedFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(3_000);
                        System.out.println("1.1");
                        return "Hello from 1.1 + " + getName();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return "error";
                    }
                })
                .thenComposeAsync(previousResult -> CompletableFuture.supplyAsync(() -> {
                    System.out.println("1.2");
                    return previousResult + ". Hello from 1.2 " + getName();
                }));
        System.out.println("Result: " + composedFuture.get());

        // -- thenCombine(completableFuture) == parallel computation + combining
        completableFuture
                = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(3_000);
                        System.out.println("2.1");
                        return "Hello from 2.1 " + getName();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return "error";
                    }
                })
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                Thread.sleep(1_000);
                                System.out.println("2.2");
                                return "Hello from 2.2 " + getName();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return "error";
                            }
                        }),
                        (firstResult, secondResult) -> {
                            try {
                                Thread.sleep(1_000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("BiFunction is ran from " + getName());
                            return firstResult + " " + secondResult;
                        });
        System.out.println("Result: " + completableFuture.get());


        //--------------------- Running Multiple Futures in Parallel

        // without combining results of computation
        CompletableFuture<String> future4
                = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future5
                = CompletableFuture.supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future6
                = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future4, future5, future6);
        combinedFuture.get();

        // with combining results of computation
        String combined = Stream.of(future4, future5, future6)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));
        System.out.println("Result: " + combined);


        // ------------------------ Handling Errors

        String name = null;
        CompletableFuture<String> completableFutureWithErrorHandling = CompletableFuture.supplyAsync(() -> {
            if (name == null) {
                throw new RuntimeException("Computation error!");
            }
            return "Hello, " + name;
        }).handle((result, throwable) -> result != null ? result : "Hello, Stranger!");

        System.out.println("Result: " + completableFutureWithErrorHandling.get());
    }

    private static Future<String> calculateAsync() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // could be completed as promise
            completableFuture.complete("Future is completed by another thread from " + getName());
        });

        executorService.shutdown();
        return completableFuture;
    }

    private static String getName() {
        return Thread.currentThread().getName();
    }
}
