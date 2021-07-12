package logreader;


import java.io.*;

public class ReversedLinesFileReader extends InputStream {

    private RandomAccessFile randomAccessFile;

    private long currentLineStart;
    private long currentLineEnd;
    private long currentPos;
    private long lastPosInFile;
    private int lastChar;


    ReversedLinesFileReader(File file) throws FileNotFoundException {
        randomAccessFile = new RandomAccessFile(file, "r");
        currentLineStart = file.length();
        currentLineEnd = file.length();
        lastPosInFile = file.length() == 0 ? 0 : file.length() - 1;
        currentPos = currentLineEnd;

    }

    public int read() throws IOException {

        if (currentPos < currentLineEnd) {
            randomAccessFile.seek(currentPos++);
            return (int) randomAccessFile.readByte();
        } else if (currentPos > lastPosInFile && currentLineStart < currentLineEnd) {
            // last line in file (first returned)
            findPrevLine();
            if (lastChar != '\n' && lastChar != '\r') {
                // last line is not terminated
                return '\n';
            } else {
                return read();
            }
        } else if (currentPos < 0) {
            return -1;
        } else {
            findPrevLine();
            return read();
        }
    }

    @Override
    public void close() throws IOException {
        if (randomAccessFile != null) {
            randomAccessFile.close();
            randomAccessFile = null;
        }
    }

    private void findPrevLine() throws IOException {
        if (lastChar == -1) {
            randomAccessFile.seek(lastPosInFile);
        }

        currentLineEnd = currentLineStart;

        // There are no more lines, since we are at the beginning of the file and no lines.
        if (currentLineEnd == 0) {
            currentLineEnd = -1;
            currentLineStart = -1;
            currentPos = -1;
            return;
        }

        long filePointer = currentLineStart - 1;

        while (true) {
            filePointer--;

            // we are at start of file so this is the first line in the file.
            if (filePointer < 0) {
                break;
            }

            randomAccessFile.seek(filePointer);
            int readByte = randomAccessFile.readByte();

            // We ignore last LF in file. search back to find the previous LF.
            if (readByte == 0xA && filePointer != lastPosInFile) {
                break;
            }
        }
        // we want to start at pointer +1 so we are after the LF we found or at 0 the start of the file.
        currentLineStart = filePointer + 1;
        currentPos = currentLineStart;
    }
}
