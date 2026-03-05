package com.santoni.iot.aps.infrastructure.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduleSolveThreadPool {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 5, 1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<Runnable>(500));

    public static void submitSolveTask(Runnable task) {
        THREAD_POOL_EXECUTOR.execute(task);
    }
}
