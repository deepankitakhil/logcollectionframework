package logreader;

import threadutils.StoppableRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReversedLogFileReader extends FileWatcher {

    private File file;
    private ReversedLinesFileReader reversedLinesFileReader;
    private BufferedReader bufferedReader;

    public ReversedLogFileReader(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
        this.reversedLinesFileReader = new ReversedLinesFileReader(file);
        bufferedReader = new BufferedReader(new InputStreamReader(this.reversedLinesFileReader));
        startWatching(5);
    }

    private void startWatching(int seconds) {
        Timer timer = new Timer();
        StoppableRunnable watcherThread = new StoppableRunnable() {
            @Override
            protected void doUnitOfWork() {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() { // Function runs every MINUTES minutes.
                        try {
                            watchFile();
                        } catch (IOException | InterruptedException e) {
                            System.out.println("File watcher failed to register.");
                        }
                    }
                }, 0, 1000 * seconds);
            }
        };

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(watcherThread);
    }

    public List<String> readLastNFile(int lastNLines) throws IOException {

        List<String> output = new ArrayList<>();
        while (lastNLines > 0) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            output.add(line);
            lastNLines--;
        }

        return output;
    }

    @Override
    public void onModified() {
        try {
            this.reversedLinesFileReader = new ReversedLinesFileReader(file);
        } catch (FileNotFoundException ignored) {
        }
    }
}
