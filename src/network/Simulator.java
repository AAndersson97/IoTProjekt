package network;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Klassens syfte är att simulera händelser som att packet skickas över nätet.
 */
public class Simulator {
    private static Thread[] workers;
    private static final ArrayDeque<Runnable> queue;
    private static final ArrayList<Timer> timers;
    private volatile static boolean shutdown;

    static  {
        queue = new ArrayDeque<>();
        workers = new Thread[2];
        workers[0] = new Thread(newRunnable());
        workers[1] = new Thread(newRunnable());
        shutdown = false;
        workers[0].start();
        workers[1].start();
        timers = new ArrayList<>();
    }

    public static void scheduleTask(Runnable task) {
        synchronized (queue) {
            queue.addLast(task);
            queue.notifyAll();
        }
    }

    public static void scheduleFutureTask(TimerTask task, long delay) {
        Timer timer = new Timer();
        timers.add(timer);
        timer.schedule(task, delay);
    }

    public static void shutdown() {
        synchronized (queue) {
            shutdown = true;
            for (Timer timer : timers)
                timer.cancel();
            queue.notifyAll();
        }
        PacketLocator.shutDown();
    }

    public static Runnable newRunnable() {
        return () -> {
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty() && !shutdown) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    if (shutdown)
                        break;
                    else
                        queue.removeFirst().run();
                }
            }
        };
    }
}
