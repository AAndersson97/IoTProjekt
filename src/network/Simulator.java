package network;

import java.util.*;

/**
 * Klassens syfte är att simulera händelser som att packet skickas över nätet.
 */
public class Simulator {
    private static final ArrayDeque<Runnable> queue;
    private static final Timer timer;
    private volatile static boolean shutdown;

    static  {
        queue = new ArrayDeque<>();
        Thread worker = new Thread(Simulator::run);
        shutdown = false;
        worker.start();
        timer = new Timer();
    }

    public static void scheduleTask(Runnable task) {
        synchronized (queue) {
            queue.addLast(task);
            queue.notifyAll();
        }
    }

    public static void scheduleFutureTask(TimerTask task, long delay) {
        Timer timer = new Timer();
        timer.schedule(task, delay);
    }

    public static void shutdown() {
        synchronized (queue) {
            shutdown = true;
            timer.cancel();
            queue.notifyAll();
        }
        PacketLocator.shutDown();
    }

    public static void run() {
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
                else {
                    queue.removeFirst().run();
                }
            }
        }
    }
}
