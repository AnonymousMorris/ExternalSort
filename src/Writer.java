import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Writer {

    private File tmpFile;
    private RandomAccessFile raf;
    
    public Writer() throws IOException {
        String filename = "tmp";
        this.tmpFile = File.createTempFile(filename, ".run");
        this.raf = new RandomAccessFile(tmpFile, "rw");
        raf.seek(0);
    }

    public void writePage(String text) throws IOException {
        this.raf.write(text.getBytes(StandardCharsets.UTF_8));
    }

    public void swapFile(String filename) throws IOException {
        File originalFile = new File(filename);
        Files.move(tmpFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        raf.close();
    }

    public void close() throws IOException {
        this.raf.close();
    }
}
