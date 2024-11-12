import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Run {
    private int begin;
    private int end;
    private int len;
    private int curPos;
    private Record lastRecord;
    private String filename;

    public Run (int begin, int end, String filename) {
        this.begin = begin;
        this.end = end;
        this.curPos = begin;
        this.len = end - begin;
        this.lastRecord = null;
        this.filename = filename;
    }

    public boolean hasNext() {
        return curPos < end;
    }
    
    public Page nextPage() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filename, "rw");
            // Record[] records = new Record[ByteFile.RECORDS_PER_BLOCK];
            raf.seek(this.curPos);
            int readLen = ByteFile.BYTES_PER_BLOCK;
            if ((end - curPos) < ByteFile.BYTES_PER_BLOCK) {
                readLen = end - curPos;
            }
            byte[] basicBuffer = new byte[readLen];
            raf.read(basicBuffer, 0, readLen);
            ByteBuffer bb = ByteBuffer.wrap(basicBuffer);
            Page page = new Page(bb);
        raf.close();

        this.lastRecord = page.getLast();
        this.curPos += readLen;
        return page;
    }
    
    public boolean isLast(Record record) {
        return hasNext() && (this.lastRecord.getID() == record.getID());
    }
}
