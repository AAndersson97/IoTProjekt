package network;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Klassens syfte är att simulera händelser som att packet skickas över nätet.
 */
public class Simulator {
    private static Thread worker;
    private static final ArrayDeque<Runnable> queue;
    private volatile static boolean shutdown;

    static  {
        queue = new ArrayDeque<>();
        worker = new Thread(Simulator::run);
        shutdown = false;
        worker.start();
    }
    public static void scheduleTask(Runnable task) {
        synchronized (queue) {
            queue.addLast(task);
            queue.notifyAll();
        }
    }

    public static void shutdown() {
        synchronized (queue) {
            queue.notifyAll();
            shutdown = true;
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
