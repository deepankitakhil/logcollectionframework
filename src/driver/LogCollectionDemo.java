package driver;

import exception.InvalidInputException;
import readfacade.FileReaderFacade;
import threadutils.StoppableRunnable;
import utils.FileUtils;
import writefacade.FileWriterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class LogCollectionDemo {

    private FileWriterService fileWriterService;
    private FileReaderFacade fileReaderFacade;

    private LogCollectionDemo() {
        fileWriterService = new FileWriterService();
        fileReaderFacade = new FileReaderFacade();

    }

    public static void main(String[] args) {
        boolean isUpdateSuccessful = FileUtils.trySetCurrentDirectory("/Users/Deepankit/Downloads");
        String folderDirectory = "/var/logs/";
        String[] fileNames = new String[]{"1.txt", "2.txt"};

        LogCollectionDemo demo = new LogCollectionDemo();

        if (isUpdateSuccessful) {
            try {
                demo.fileReaderFacade.buildAvailableReversedLogFileReaders(folderDirectory, fileNames);
                demo.executeInternal(folderDirectory, fileNames, new Random().nextInt(10));

            } catch (InvalidInputException e) {
                demo.fileWriterService.triggerKillSwitch();
                demo.fileReaderFacade.triggerKillSwitch();
            }
        }
    }

    private void executeInternal(String folder, String[] fileNames, int duration) {

        List<StoppableRunnable> workerThreads = new ArrayList<>();

        StoppableRunnable writerThread = new StoppableRunnable() {
            @Override
            protected void doUnitOfWork() {
                try {
                    fileWriterService.mimicExternalSourcesPerformingFileWrite(folder, fileNames);
                } catch (InvalidInputException e) {
                    fileWriterService.triggerKillSwitch();
                }
            }
        };

        StoppableRunnable readerThread = new StoppableRunnable() {
            @Override
            protected void doUnitOfWork() {
                fileReaderFacade.mimicIncomingHTTPRequestsToPerformRead();
            }
        };

        workerThreads.add(writerThread);
        workerThreads.add(readerThread);

        ExecutorService service = Executors.newFixedThreadPool(workerThreads.size());


        try {
            service.invokeAny(workerThreads, duration, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fileWriterService.triggerKillSwitch();
            fileReaderFacade.triggerKillSwitch();
        } finally {
            service.shutdownNow();
        }
    }
}
