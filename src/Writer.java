import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Writer {

    private File tmpFile;
    private RandomAccessFile raf;
    
    public Writer() throws IOException {
        String filename = "tmp";
        this.tmpFile = File.createTempFile(filename, ".run");
        this.raf = new RandomAccessFile(tmpFile, "w");
        raf.seek(0);
    }

    public void writePage(Page page) throws IOException {
        byte[] basicBuffer = new byte[page.capacity];
        ByteBuffer bb = ByteBuffer.wrap(basicBuffer);
        while (page.hasNext()) {
            Record record = page.nextRecord();
            bb.putLong(record.getID());
            bb.putDouble(record.getKey());
        }
        this.raf.write(basicBuffer);
    }

    public void swapFile() throws IOException {
        raf.close();
    }
}
