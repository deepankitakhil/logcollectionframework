package readfacade;

import exception.InvalidInputException;
import logreader.ReversedLogFileReader;
import utils.FileUtils;
import validationservice.FileValidator;
import validationservice.IValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class FileReaderFacade {

    private IValidator<File> fileValidator;
    private List<Optional<ReversedLogFileReader>> availableReaders;
    private boolean killSwitchTriggered;

    public FileReaderFacade() {
        this.killSwitchTriggered = false;
        this.fileValidator = new FileValidator();
        this.availableReaders = new ArrayList<>();
    }

    public void buildAvailableReversedLogFileReaders(String folder, String[] fileNames) throws InvalidInputException {

        for (String fileName : fileNames) {
            try {
                File file = new File(FileUtils.tryGetFullQualifiedFilePath(folder, fileName));
                this.fileValidator.validate(file);
                availableReaders.add(Optional.of(new ReversedLogFileReader(file)));
            } catch (FileNotFoundException e) {
                availableReaders.add(Optional.empty());
            }
        }
    }

    public void mimicIncomingHTTPRequestsToPerformRead() {
        while (!killSwitchTriggered) {
            Random random = new Random();
            int idx = random.nextInt(availableReaders.size());
            Optional<ReversedLogFileReader> linesFileReader = this.availableReaders.get(idx);
            if (linesFileReader.isPresent()) {
                try {
                    // Try to read last N lines randomly distributed between 0 to 100.
                    System.out.println(linesFileReader.get().readLastNFile(new Random().nextInt(100)));
                } catch (IOException e) {
                    triggerKillSwitch();
                }
            }
        }
    }

    public void triggerKillSwitch() {
        killSwitchTriggered = true;
    }
}
