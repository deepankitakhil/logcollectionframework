package threadutils;

import java.util.Timer;
import java.util.TimerTask;

public class TimeoutTask extends TimerTask {
    private Thread task;
    private Timer timer;

    public TimeoutTask(Thread t, Timer timer) {
        this.task = t;
        this.timer = timer;
    }

    public void run() {
        if (this.task != null && this.task.isAlive()) {
            this.task.interrupt();
            this.timer.cancel();
        }
    }
}
