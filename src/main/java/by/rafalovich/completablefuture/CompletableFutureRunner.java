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

        // completedFuture
        Future<String> predefinedFuture = CompletableFuture.completedFuture("Future is predefined.");
        System.out.println(predefinedFuture.get());

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Async execution from " + getName());

        // thenApply - map analog
        CompletableFuture<String> future = completableFuture.thenApply(s -> s + " World from " + getName());
        System.out.println(future.get());

        // thenAccept
        CompletableFuture<Void> future2 = completableFuture.thenAccept(s -> System.out.println("Computation returned: " + s));
        future2.get();

        // thenRun
        CompletableFuture<Void> future3 = completableFuture
                .thenRun(() -> System.out.println("Computation finished."));

        future3.get();

        // ------------------- Chaining and combining futures

        // thenCompose -- flatMap analog - sequential computation
        CompletableFuture<String> composedFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(3_000);
                        System.out.println("first after sleeping");
                        return "Hello from " + getName();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return "error";
                    }
                })
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> {
                    System.out.println("----------------");
                    return s + " World from " + getName();
                }));

        System.out.println(composedFuture.get());

        // thenCombine - parallel computation + combining
        completableFuture
                = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(3_000);
                        System.out.println("first after sleeping");
                        return "Hello from " + getName();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return "error";
                    }
                })
                .thenCombine(CompletableFuture.supplyAsync(() -> {
                                    try {
                                        Thread.sleep(1_000);
                                        System.out.println("second after sleeping");
                                        return " World from " + getName();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        return "error";
                                    }
                                }
                        ),
                        (s1, s2) -> {
                            try {
                                Thread.sleep(1_000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("BiFunction is ran from " + getName());
                            return s1 + s2;
                        });

        System.out.println(completableFuture.get());


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
        System.out.println(combined);


        // ------------------------ Handling Errors

        String name = null;
        CompletableFuture<String> completableFutureWithErrorHandling = CompletableFuture.supplyAsync(() -> {
            if (name == null) {
                throw new RuntimeException("Computation error!");
            }
            return "Hello, " + name;
        }).handle((s, throwable) -> s != null ? s : "Hello, Stranger!");

        System.out.println(completableFutureWithErrorHandling.get());
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
            completableFuture.complete("Future is completed by another thread.");
        });

        executorService.shutdown();
        return completableFuture;
    }

    private static String getName() {
        return Thread.currentThread().getName();
    }
}
