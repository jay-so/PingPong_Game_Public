package org.prography.spring.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoCloseScheduledExecutor implements AutoCloseable {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void schedule(Runnable command, long delay, TimeUnit unit) {
        executorService.schedule(command, delay, unit);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
