package network;

import java.util.Objects;
import java.util.concurrent.*;

public class UpdateObserver {

    private long latestUpdate;
    private UpdateListener listener;
    private long timeout; // antalet millisekunder som får fortgår innan det kan konstateras att variabeln updated ej har uppdaterats.
    private ScheduledFuture<?> updateTask;
    private ScheduledExecutorService executorService;

    public UpdateObserver(long timeout) {
        this.timeout = timeout;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public Runnable checkUpdateTask() {
        return () -> {
            if (latestUpdate + timeout < System.currentTimeMillis()) {
                listener.missingUpdates();
                updateTask.cancel(true);
            }
        };
    }

    public void update() {
        latestUpdate = System.currentTimeMillis();
    }

    public void addListener(UpdateListener updateListener) {
        if (this.listener != null)
            throw new IllegalArgumentException("Listener is already set");
        Objects.requireNonNull(updateListener);
        this.listener = updateListener;
    }

    public void removeListener() {
        if (listener == null)
            throw new IllegalArgumentException("No listener is set");
        this.listener = null;
        if (!updateTask.isCancelled())
            updateTask.cancel(false);
    }

    public void start() {
        updateTask = executorService.scheduleAtFixedRate(checkUpdateTask(), timeout, timeout, TimeUnit.MILLISECONDS);
    }

    public boolean isActive() {
        return updateTask != null && !updateTask.isCancelled();
    }

    public void reset() {
        if (!updateTask.isCancelled())
            throw new IllegalStateException("There is no need to reset the observer");
        updateTask = executorService.scheduleAtFixedRate(checkUpdateTask(), timeout, timeout, TimeUnit.MILLISECONDS);
    }

    @FunctionalInterface
    public interface UpdateListener {
        void missingUpdates();
    }
}
