import java.io.IOException;

public class Controller {
    // private Reader reader;
    private MinHeap<Record> minheap;
    private Writer writer;
    private Page in;
    private Page out;
    private int count = 0;

    public Controller() throws IOException {
        Record[] records = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
        this.minheap = new MinHeap<Record>(records, 0, ByteFile.RECORDS_PER_BLOCK);
        this.writer = new Writer();
        this.in = null;
        this.out = new Page(null);
        // this.reader = new Reader(filename);
    }

    public void run(String filename) throws IOException {
        Reader reader = new Reader(filename);

        // put initial 8 pages into min heap
        int heapSize = 0;
        Record[] heap = new Record[8 * ByteFile.RECORDS_PER_BLOCK];
        for (int i = 0; i < 8 && reader.hasNext(); i++) {
            this.in = reader.nextPage();
            System.arraycopy(this.in.getRecords(), 0, heap, i * ByteFile.RECORDS_PER_BLOCK, this.in.getSize());
            heapSize += this.in.getSize();
            System.err.println("iter: " + i);
        }
        minheap = new MinHeap<Record>(heap, heapSize, 8 * ByteFile.RECORDS_PER_BLOCK);
        System.err.println("heapsize: " + heapSize);

        // sort
        while (reader.hasNext()) {
            this.in = reader.nextPage();
            sort(this.in);
        }

        // TODO: account for hidden nodes
        flushHeap();
        writer.close();
    }

    private void sort(Page page) throws IOException {
        while (page.hasNext()) {
            while (minheap.heapSize() > 0 && page.hasNext()) {
                // TODO double check with video
                // get Min record and push to output buffer
                Record min = this.minheap.removeMin();
                Record newRecord = page.nextRecord();
                if (newRecord.getKey() < min.getKey()) {
                    // hide new record
                    this.minheap.modify(minheap.heapSize() - 1, newRecord);
                }
                else {
                    this.minheap.insert(newRecord);
                }

                this.out.addRecord(min);

                // send output page to be written into memory if full
                if (this.out.isFull()) {
//                    System.out.println("dbg");
                    writeOutputBuffer();
                }
            }
            if (minheap.heapSize() == 0) {
            	if (this.out.getSize() > 0) {
	                // flush output buffer
	                writeOutputBuffer();
            	}

                // unhide records
                minheap.setHeapSize(8 * ByteFile.RECORDS_PER_BLOCK);
                minheap.buildHeap();
            }
        }
        // print out remaining records in the min heap
        flushHeap();
    }

    private void flushHeap() throws IOException {
        while (minheap.heapSize() != 0) {
        	assert(!this.out.isFull()): "output buffer is full and cannot take another record";
            Record min = minheap.removeMin();
            this.out.addRecord(min);

            // Send page to be written to memory if full
            if (this.out.isFull()) {
            	System.err.println(minheap.heapSize());
                writeOutputBuffer();
            }
        }
        if (this.out.getSize() > 0) {
        	System.err.println("dbg");
        	writeOutputBuffer();
        }
    }

    private void writeOutputBuffer() throws IOException {
        String text = this.out.toString();
//        System.out.println("dbg: " + count);
    	if (this.count == 5) {
    		text = "\n" + text;
    		this.count = 0;
    	}
        this.count++;
        
        System.out.print(text);
        writer.writePage(text);
        this.out = new Page(null);
    }
}
