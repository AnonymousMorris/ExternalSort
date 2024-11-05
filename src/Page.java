import java.nio.ByteBuffer;

public class Page {
    public Record[] records;
    public int capacity;
    private int readPos;
    private int writePos;
    
    
    public Page(ByteBuffer dataBuffer, int inputRecordCnt, int capacity) {
        // TODO: Make sure that all pages will be full
        this.capacity = capacity;
        this.readPos = 0;
        this.writePos = inputRecordCnt;
        this.records = new Record[capacity];
        for (int i = 0; i < inputRecordCnt; i++) {
            long recID = dataBuffer.getLong();
            double recKey = dataBuffer.getDouble();
            this.records[i] = new Record(recID, recKey);
        }
    }

    public Record nextRecord() {
        assert(!hasNext());
        return this.records[readPos++];
    }
    
    public boolean addRecord(Record record) {
        if (this.isFull()) {
            return false;
        }
        else {
            this.records[writePos] = record;
            writePos++;
            return true;
        }
    }

    public boolean hasNext() {
        return readPos < capacity;
    }

    public boolean isFull() {
        return writePos == capacity;
    }
}
