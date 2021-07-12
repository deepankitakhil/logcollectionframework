package logreader;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class FileWatcher {
    private Path folderPath;
    private String watchFile;

    FileWatcher(File file) {
        Path filePath = Paths.get(file.getAbsolutePath());

        boolean isRegularFile = Files.isRegularFile(filePath);

        if (!isRegularFile) {
            throw new IllegalArgumentException("Illegal option provided to watch. " + file.getName() + " is not a regular file");
        }

        folderPath = filePath.getParent();

        this.watchFile = file.getName();
    }

    void watchFile() throws IOException, InterruptedException {
        FileSystem fileSystem = folderPath.getFileSystem();

        try (WatchService service = fileSystem.newWatchService()) {
            folderPath.register(service, ENTRY_MODIFY);

            WatchKey watchKey = service.take();

            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                Kind<?> kind = watchEvent.kind();

                if (ENTRY_MODIFY == kind) {
                    Path watchEventPath = (Path) watchEvent.context();

                    if (watchEventPath.toString().equals(watchFile)) {
                        onModified();
                    }
                }
            }

            if (!watchKey.reset()) {
                throw new IOException();
            }
        }
    }

    public abstract void onModified();
}
