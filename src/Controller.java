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
        // this.reader = new Reader(filename);
    }

    public void run(String filename) throws IOException {
        Reader reader = new Reader(filename);
        int counter = 0;
        while (reader.hasNext()) {
            this.in = reader.nextPage();

            sort(this.in);

            if (counter++ % 5 == 4) {
                System.out.print("\n");
            }
        }
    }

    private void sort(Page page) throws IOException {
        printPage(page);

        while (page.hasNext()) {
            Record min = this.minheap.removeMin();
            this.out.addRecord(min);

            Record record = page.nextRecord();
            this.minheap.insert(record);

            if (this.out.isFull()) {
                printPage(this.out);
                writer.writePage(this.out);
            }
        }
    }

    private void printPage(Page page) {
        System.out.print(page.records[0].getID() + " ");
        System.out.print(page.records[0].getKey() + " ");
    }
}
