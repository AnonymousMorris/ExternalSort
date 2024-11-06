import java.io.IOException;

public class Controller {
    // private Reader reader;
    private MinHeap<Record> minheap;
    private Writer writer;
    private Page in;
    private Page out;

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
        int counter = 0;

        // put initial 8 pages into min heap
        int heapSize = 0;
        Record[] heap = new Record[8 * ByteFile.RECORDS_PER_BLOCK];
        for (int i = 0; i < 8 && reader.hasNext(); i++) {
            this.in = reader.nextPage();
            System.arraycopy(this.in.getRecords(), 0, heap, i * ByteFile.RECORDS_PER_BLOCK, this.in.getSize());
            heapSize += this.in.getSize();
        }
        minheap = new MinHeap<Record>(heap, heapSize, 8 * ByteFile.RECORDS_PER_BLOCK);

        // sort
        while (reader.hasNext()) {
            this.in = reader.nextPage();
            sort(this.in);
            // if (counter++ % 5 == 4) {
            //     System.out.print("\n");
            // }
        }

        flushHeap();
        writer.close();
    }

    private void sort(Page page) throws IOException {
        while (page.hasNext()) {
            while (minheap.heapSize() > 0 && page.hasNext()) {
                // get Min record and push to output buffer
                Record min = this.minheap.removeMin();
                Record newRecord = page.nextRecord();
                if (newRecord.getKey() < min.getKey()) {
                    // hide new record
                    this.minheap.modify(minheap.heapSize(), newRecord);
                }
                else {
                    this.minheap.insert(newRecord);
                }

                this.out.addRecord(min);

                // send output page to be written into memory if full
                if (this.out.isFull()) {
                    System.out.println("dbg");
                    writeOutputBuffer();
                }
            }
            // flush output buffer
            writeOutputBuffer();

            // unhide records
            minheap.setHeapSize(8 * ByteFile.RECORDS_PER_BLOCK);
            minheap.buildHeap();
        }
        // print out remaining records in the min heap
        flushHeap();
    }

    private void flushHeap() throws IOException {
        while (minheap.heapSize() != 0) {
            Record min = minheap.removeMin();
            this.out.addRecord(min);

            // Send page to be written to memory if full
            if (this.out.isFull()) {
                writeOutputBuffer();
            }
        }
        writeOutputBuffer();
    }

    private void writeOutputBuffer() throws IOException {
        String text = this.out.toString();
        System.out.print(text);
        writer.writePage(text);
        this.out = new Page(null);
    }
}
