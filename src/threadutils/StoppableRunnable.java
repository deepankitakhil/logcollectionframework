package threadutils;

import java.util.concurrent.Callable;

public abstract class StoppableRunnable implements Callable<Boolean> {

    private volatile boolean stopWork;

    public final Boolean call() {
        if (!stopWork) {
            doUnitOfWork();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                stop();
            }
        }
        return true;
    }

    public void stop() {
        stopWork = true;
    }

    protected abstract void doUnitOfWork();
}
