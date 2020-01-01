package old;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Klassens syfte är att simulera händelser som att packet skickas över nätet.
 */
public class Simulator {
    private static Thread worker;
    private static final ArrayDeque<Runnable> queue;
    private static final ArrayList<Timer> timers;
    private volatile static boolean shutdown;

    static  {
        queue = new ArrayDeque<>();
        worker = new Thread(network.Simulator::run);
        shutdown = false;
        worker.start();
        timers = new ArrayList<>();
    }

    public static void scheduleTask(Runnable task) {
        synchronized (queue) {
            queue.addLast(task);
            queue.notifyAll();
        }
    }

    public static void scheduleTaskPeriodically(TimerTask task, long delay, long period) {
        Timer timer = new Timer();
        timers.add(timer);
        timer.scheduleAtFixedRate(task, delay, period);
    }

    public static void shutdown() {
        synchronized (queue) {
            shutdown = true;
            for (Timer timer : timers)
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
