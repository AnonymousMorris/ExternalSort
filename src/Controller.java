import java.io.IOException;

public class Controller {
    // private Reader reader;
    private MinHeap<Record> minheap;
    private MinHeap<Record> hidden;
    private Writer writer;
    private Page in;
    private Page out;
    private int count = 0;

    public Controller() throws IOException {
//        Record[] records = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
//        this.minheap = new MinHeap<Record>(records, 0, ByteFile.RECORDS_PER_BLOCK);
        Record[] emptyHeap = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
        this.hidden = new MinHeap<>(emptyHeap, 0, ByteFile.RECORDS_PER_BLOCK * 8);
        this.writer = new Writer();
        this.in = null;
        this.out = new Page(null);
        // this.reader = new Reader(filename);
    }

    public void run(String filename) throws IOException {
        Reader reader = new Reader(filename, false);

        // put initial 8 pages into min heap
        int heapSize = 0;
        Record[] heap = new Record[8 * ByteFile.RECORDS_PER_BLOCK];
        for (int i = 0; i < 8 && reader.hasNext(); i++) {
            this.in = reader.nextPage();
            System.arraycopy(this.in.getRecords(), 0, heap, i * ByteFile.RECORDS_PER_BLOCK, this.in.getSize());
            heapSize += this.in.getSize();
        }
        minheap = new MinHeap<Record>(heap, heapSize, 8 * ByteFile.RECORDS_PER_BLOCK);
        minheap.buildHeap();

        // sort
        while (reader.hasNext()) {
            this.in = reader.nextPage();
            sort(this.in);
        }

        // TODO: account for hidden nodes
        flushHeap();
        writer.close();
        writer.swapFile("test.txt");
    }

    private void sort(Page page) throws IOException {
        while (page.hasNext()) {
            while (minheap.heapSize() > 0 && page.hasNext()) {
                // TODO double check with video
//            	System.err.print("dbg");
                // get Min record and push to output buffer
                Record min = this.minheap.removeMin();
                Record newRecord = page.nextRecord();
                assert(newRecord != null);
            	System.err.print("n: " + newRecord.getKey() + "  ");
                if (newRecord.compareTo(min) < 0) {
                	System.err.println("n: " + newRecord.getKey());
                    // hide new record
                    this.hidden.insert(newRecord);
                }
                else {
                	System.err.println("i: " + newRecord.getKey());
                    this.minheap.insert(newRecord);
                }

                this.out.addRecord(min);

                // send output page to be written into memory if full
                if (this.out.isFull()) {
                    writeOutputBuffer();
                }
            }
            if (minheap.heapSize() == 0) {
            	System.err.print("minheap is empty");
            	if (this.out.getSize() > 0) {
	                // flush output buffer
	                writeOutputBuffer();
            	}

                // unhide records
                this.minheap = this.hidden;
                Record emptyHeap[] = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
                this.hidden = new MinHeap<>(emptyHeap, 0, ByteFile.RECORDS_PER_BLOCK * 8);
            }
        }
    }

    private void flushHeap() throws IOException {
        while (minheap.heapSize() > 0) {
        	assert(!this.out.isFull()): "output buffer is full and cannot take another record";
            Record min = minheap.removeMin();
            this.out.addRecord(min);

            // Send page to be written to memory if full
            if (this.out.isFull()) {
                writeOutputBuffer();
            }
        }
        if (this.out.getSize() > 0) {
//        	System.err.println("dbg");
        	writeOutputBuffer();
        }
    }

    private void writeOutputBuffer() throws IOException {
        String text = this.out.toString();
    	if (this.count == 5) {
    		text = "\n" + text;
    		this.count = 0;
    	}
        this.count++;
        
        System.out.print(text);
        System.err.println();
        writer.writePage(text);
        this.out = new Page(null);
    }
}
