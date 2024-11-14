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
        this.tmpFile = new File("run.bin");
//        this.tmpFile = File.createTempFile(filename, ".run");
        this.tmpFile.createNewFile();
        this.raf = new RandomAccessFile(tmpFile, "rw");
        raf.seek(0);
    }

    public void writePage(String text) throws IOException {
        this.raf.write(text.getBytes(StandardCharsets.UTF_8));
    }

    public void writePage(Page page) throws IOException {
        byte[] basicBuffer = new byte[page.getSize() * ByteFile.BYTES_PER_RECORD];
        ByteBuffer bb = ByteBuffer.wrap(basicBuffer);
        while (page.hasNext()) {
            Record record = page.nextRecord();
            bb.putLong(record.getID());
            bb.putDouble(record.getKey());
        }
        this.raf.write(basicBuffer);
    }

    public void swapFile(String filename) throws IOException {
    	this.raf.close();
        File originalFile = new File(filename);
        if (originalFile.exists()) {
            originalFile.delete();
        }
        this.tmpFile.renameTo(originalFile);
//        Files.move(tmpFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
