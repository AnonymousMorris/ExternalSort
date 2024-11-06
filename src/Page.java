import java.nio.ByteBuffer;

import jdk.jfr.consumer.RecordedClass;

public class Page {
    private Record[] records;
    private int capacity;
    private int size;
    private int readPos;
    private int writePos;
    
    
    public Page(ByteBuffer dataBuffer) {
        this.capacity = ByteFile.RECORDS_PER_BLOCK;
        this.size = 0;
        this.readPos = 0;
        this.writePos = 0;
        this.records = new Record[capacity];
        if (dataBuffer == null) {
            return;
        }
        else {
            while (dataBuffer.hasRemaining()) {
                long recID = dataBuffer.getLong();
                double recKey = dataBuffer.getDouble();
                this.records[size] = new Record(recID, recKey);
                this.size++;
            }
            this.writePos = size;
        }
    }

    public Record nextRecord() {
        assert(hasNext());
        return this.records[readPos++];
    }
    
    public Record[] getRecords() {
        return records;
    }

    public int getSize() {
        return size;
    }
    
    public boolean addRecord(Record record) {
         assert(!this.isFull()) : "failed to add record because the page is full";
//        if (this.isFull()) {
//            return false;
//        }
//        else {
            this.records[writePos] = record;
            this.writePos++;
            this.size++;
            return true;
//        }
    }

    public boolean hasNext() {
        return readPos < capacity;
    }

    public boolean isFull() {
        return writePos == capacity;
    }

    public String toString() {
    	assert(size > 0) : "Failed to generate string becuase page is empty";
        return records[0].getID() + " " + records[0].getKey() + " ";
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
