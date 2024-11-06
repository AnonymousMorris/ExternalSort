import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.ByteBuffer;

public class Reader {
    private String filename;
    private RandomAccessFile raf;

    public Reader(String filename) throws IOException {
        this.filename = filename;
        File file = new File(filename);
        this.raf = new RandomAccessFile(file, "r");
    }

    public Page nextPage() throws IOException {
        byte[] basicBuffer = new byte[ByteFile.BYTES_PER_BLOCK];
        ByteBuffer bb = ByteBuffer.wrap(basicBuffer);
        raf.read(basicBuffer);
        return new Page(bb);
    }

    public boolean hasNext() throws IOException {
        return raf.getFilePointer() < raf.length();
    }
    
    public void close() throws IOException {
        raf.close();
    }
}
