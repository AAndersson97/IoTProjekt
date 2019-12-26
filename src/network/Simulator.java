package network;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Klassens syfte är att simulera händelser som att packet skickas över nätet.
 */
public class Simulator {
    private static Thread worker;
    private static final ArrayDeque<Runnable> queue;
    private static Timer timer;
    private volatile static boolean shutdown;

    static  {
        queue = new ArrayDeque<>();
        worker = new Thread(Simulator::run);
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

    public static void scheduleTaskPeriodically(TimerTask task, long delay, long period) {
        timer.scheduleAtFixedRate(task, delay, period);
    }

    public static void shutdown() {
        synchronized (queue) {
            shutdown = true;
            timer.cancel();
            queue.notifyAll();
        }
    }

    public static void run() {
        while (true) {
            synchronized (queue) {
                while(queue.isEmpty() && !shutdown) {
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
    }
}
