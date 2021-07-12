package writefacade;

import exception.InvalidInputException;
import threadutils.StoppableRunnable;
import utils.FileUtils;
import validationservice.FileValidator;
import validationservice.IValidator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FileWriterService {

    private IValidator<File> fileValidator;
    private boolean killSwitchTriggered;
    private List<StoppableRunnable> workerThreads;

    public FileWriterService() {
        this.fileValidator = new FileValidator();
        this.workerThreads = new ArrayList<>();
        killSwitchTriggered = false;
    }

    public void mimicExternalSourcesPerformingFileWrite(String folder, String[] fileNames) throws InvalidInputException {

        ExecutorService service = Executors.newFixedThreadPool(fileNames.length);

        // Each thread acts like an external source appending to the log files.
        File[] files = new File[fileNames.length];
        for (int index = 0; index < files.length; index++) {
            files[index] = new File(FileUtils.tryGetFullQualifiedFilePath(folder, fileNames[index]));
            this.fileValidator.validate(files[index]);
            int finalIndex = index;
            StoppableRunnable thread = new StoppableRunnable() {
                @Override
                protected void doUnitOfWork() {
                    try {
                        appendFileContentsPeriodically(files[finalIndex]);
                    } catch (Exception e) {
                        triggerKillSwitch();
                    }
                }
            };

            workerThreads.add(thread);
        }

        try {
            service.invokeAny(workerThreads, 2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            this.triggerKillSwitch();
        } finally {
            service.shutdown();
        }
    }

    private void appendFileContentsPeriodically(File file) {
        StringBuilder buffer = new StringBuilder();
        int timestamp = 0;

        while (!killSwitchTriggered) {
            buffer.append(timestamp);

            if (timestamp % 5 == 0) {
                try (FileWriter fileWriter = new FileWriter(file, true);
                     BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                     PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
                    printWriter.println(buffer.toString());
                } catch (IOException ignored) {
                }
                buffer.setLength(0);
            }
            timestamp++;
        }
    }

    public void triggerKillSwitch() {
        killSwitchTriggered = true;
        for (StoppableRunnable runnable : workerThreads) {
            runnable.stop();
        }
    }
}
