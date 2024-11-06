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
        this.out = new Page(null, 0, ByteFile.RECORDS_PER_BLOCK);
        // this.reader = new Reader(filename);
    }

    public void run(String filename) throws IOException {
        Reader reader = new Reader(filename);
        int counter = 0;

        // put initial 8 pages into min heap
//        for (int i = 0; i < 8 && reader.hasNext(); i++) {
//            this.in = reader.nextPage();
//            Record[] heap = this.in.records;
//        }
        this.in = reader.nextPage();
        Record[] heap = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
        System.arraycopy(this.in.records, 0, heap, 0, ByteFile.RECORDS_PER_BLOCK);
        this.minheap = new MinHeap(heap, ByteFile.RECORDS_PER_BLOCK, ByteFile.RECORDS_PER_BLOCK * 8);
        
        
        // sort
        while (reader.hasNext()) {
            this.in = reader.nextPage();

            sort(this.in);

//            if (counter++ % 5 == 4) {
//                System.out.print("\n");
//            }
        }
    }

    private void sort(Page page) throws IOException {
        while (minheap.heapSize() > 0) {
            // get Min record and push to output buffer
            Record min = this.minheap.removeMin();
            this.out.addRecord(min);

            // insert next record from input buffer page into min heap
            if (page.hasNext()) {
	            Record record = page.nextRecord();
	            this.minheap.insert(record);
            }

            // send output page to be written into memory if full
            if (this.out.isFull()) {
                String text = this.out.toString();
                System.out.println("dbg");
                System.out.print(text);
                writer.writePage(text);
                this.out = new Page(null, 0, ByteFile.RECORDS_PER_BLOCK);
            }
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
                String text = this.out.toString();
                System.out.print(text);
                writer.writePage(text);
                this.out = new Page(null, 0, ByteFile.RECORDS_PER_BLOCK);
            }
        }
    }
}
