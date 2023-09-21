package by.rafalovich.forkjoinpool;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolRunner {

    public static void main(String[] args) {
        int[] array = MathUtil.prepareArray();

        long startTime = System.currentTimeMillis();

        double sum = MathUtil.calculate(array);

        long endTime = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println(endTime - startTime + " mS for the computing");

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
