import java.nio.ByteBuffer;

import jdk.jfr.consumer.RecordedClass;

public class Page {
    public Record[] records;
    public int capacity;
    public int size;
    private int readPos;
    private int writePos;
    
    
    public Page(ByteBuffer dataBuffer, int inputRecordCnt, int capacity) {
        // TODO: Make sure that all pages will be full
        this.capacity = capacity;
        this.size = inputRecordCnt;
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
        // assert(!this.isFull()) : "failed to add record because the page is full";
        if (this.isFull()) {
            return false;
        }
        else {
            this.records[writePos] = record;
            this.writePos++;
            this.size++;
            return true;
        }
    }

    public boolean hasNext() {
        return readPos < capacity;
    }

    public boolean isFull() {
        return writePos == capacity;
    }

    public String toString() {
        String ans = "";
        for (int i = 0; i < records.length; i++) {
            ans += records[i].getID() + " " + records[i].getKey() + " ";
            if (i % 5 == 4) {
                ans += "\n";
            }
        }
        return ans;
    }

    public boolean isEqual(Page other) {
        if (this.capacity != other.capacity) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        while (this.hasNext()) {
            Record record = this.nextRecord();
            Record otherRecord = other.nextRecord();

            if (!record.equals(otherRecord)) {
                return false;
            }
        }
        return true;
    }
}
