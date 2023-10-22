package by.rafalovich.forkjoinpool;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolRunner {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[] array = MathUtil.prepareArray();

        long startTime = System.currentTimeMillis();

        double sum = MathUtil.calculate(array);

        long endTime = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(endTime - startTime + " mS for the computing");

        //////////////// Divide task by user  /////////////////////////////////

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
        double result = 0;
        startTime = System.currentTimeMillis();

        List<Callable<Double>> tasks = Arrays.asList(
                () -> MathUtil.calculate(array, 0, array.length / 4),
                () -> MathUtil.calculate(array, array.length / 4, array.length / 2),
                () -> MathUtil.calculate(array, array.length / 2, array.length * 3 / 4),
                () -> MathUtil.calculate(array, array.length * 3 / 4, array.length)
        );
        List<Future<Double>> futures = fixedThreadPool.invokeAll(tasks);

        for (Future<Double> future: futures) {
            result += future.get();
        }

        endTime = System.currentTimeMillis();
        System.out.println(result);
        System.out.println(endTime - startTime + " mS for the computing");

        fixedThreadPool.shutdown();


        ////////////////   Using ForkJoinPool Directly  ///////////////////////////////

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        startTime = System.currentTimeMillis();

        sum = forkJoinPool.invoke(new RecursiveCalc(array, 0, array.length));

        endTime = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(endTime - startTime + " mS for the computing");

        forkJoinPool.shutdown();

        ////////////////   Using Stream API  ///////////////////////////////

        startTime = System.currentTimeMillis();
        sum = Arrays.stream(array)
                .parallel()
                .mapToDouble(MathUtil::function)
                .sum();
        endTime = System.currentTimeMillis();

        System.out.println(sum);
        System.out.println(endTime - startTime + " mS for the computing");
    }

    @RequiredArgsConstructor
    private static class RecursiveCalc extends RecursiveTask<Double> {

        private static final int THRESHOLD = 25_000;

        private final int[] array;
        private final int start;
        private final int end;

        @Override
        protected Double compute() {
            if (end - start <= THRESHOLD) {
                return MathUtil.calculate(array, start, end);
            } else {
                int middle = start + ((end - start) / 2);
                RecursiveCalc left = new RecursiveCalc(array, start, middle);
                RecursiveCalc right = new RecursiveCalc(array, middle, end);
                invokeAll(left, right);
                return left.join() + right.join();
            }
        }
    }
}
