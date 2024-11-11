import java.io.IOException;

public class Controller {
    // private Reader reader;
    private MinHeap<Record> minheap;
    private MinHeap<Record> hidden;
    private Writer writer;
    private String filename;
    private Page in;
    private Page out;
    private int count = 0;
    private Run[] runs;
    private Run[] newRuns;
    private int runStart;
    private int runEnd;

    public Controller() throws IOException {
//        Record[] records = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
//        this.minheap = new MinHeap<Record>(records, 0, ByteFile.RECORDS_PER_BLOCK);
        Record[] emptyHeap = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
        this.hidden = new MinHeap<>(emptyHeap, 0, ByteFile.RECORDS_PER_BLOCK * 8);
        this.writer = new Writer();
        this.in = null;
        this.out = new Page(null);
        this.runs = new Run[0];
        this.newRuns = new Run[0];
        this.runStart = 0;
        this.runEnd = 0;
        // this.reader = new Reader(filename);
    }

    public void run(String filename) throws IOException {
        this.filename = filename;
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

        // replacement sort
        while (reader.hasNext()) {
            this.in = reader.nextPage();
            replacementSort(this.in);
        }

        flushHeap();
        writer.close();
        writer.swapFile("test.txt");
        
        // merge sort
        while (this.runs.length > 1) {
            this.runStart = 0;
            this.runEnd = 0;
	        int newRunsLen = this.runs.length / 8;
	        if ((this.runs.length % 8) > 0) {
	        	newRunsLen++;
	        }
	        Run[] newRuns = new Run[newRunsLen];
	        for (int i = 0; i < newRunsLen; i ++) {
	        	int mergeRunLen = Math.min(8, this.runs.length - i * 8);
	        	Run[] mergeRuns = new Run[mergeRunLen];
	        	for (int j = 0; j < mergeRunLen; j++) {
	        		mergeRuns[j] = this.runs[i * 8 + j];
	        	}
	        	newRuns[i] = mergeSort(mergeRuns);
	        }
	        this.runs = newRuns;
	        writer.close();
	        writer.swapFile("test.txt");
        }
    }

    private void replacementSort(Page page) throws IOException {
        while (page.hasNext()) {
            while (minheap.heapSize() > 0 && page.hasNext()) {
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

                // add run to list
                Run run = new Run(this.runStart, this.runEnd, this.filename);
                this.runStart = this.runEnd + 1;
                appendRun(run, this.runs);
            }
        }
    }

    private Run mergeSort(Run[] runs) throws IOException {
        assert(runs.length <= 8);
        assert(this.minheap.heapSize() == 0);

        // initialize heap
        Record[] heap = new Record[ByteFile.RECORDS_PER_BLOCK * 8];
        int heapSize = 0;
        for (int i = 0; i < runs.length; i++) {
            Page page = runs[i].nextPage();
            System.arraycopy(this.in.getRecords(), 0, heap, i * ByteFile.RECORDS_PER_BLOCK, this.in.getSize());
            heapSize += this.in.getSize();
        }
        minheap = new MinHeap<Record>(heap, heapSize, 8 * ByteFile.RECORDS_PER_BLOCK);
        minheap.buildHeap();

        // merge sort
        while (hasRunning(runs)) {
        	Record min = minheap.removeMin();
    		this.out.addRecord(min);
    		// insert new page from a run
    		for (Run run : runs) {
    			if (run.isLast(min)) {
    				Page page = run.nextPage();
    				while (page.hasNext()) {
    					Record newRecord = page.nextRecord();
    					minheap.insert(newRecord);
    				}
    				break;
    			}
    		}
    		
    		// send output page to be written to memory if full
        	if (this.out.isFull()) {
        		writeOutputBuffer();
        	}
        }
        flushHeap();
        Run run = new Run(this.runStart, this.runEnd, filename);
        return run;
    }
    
    private boolean hasRunning(Run[] runs) {
    	boolean stillRunning = false;
    	for (Run run : runs) {
    		stillRunning |= run.hasNext();
    	}
    	return stillRunning;
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
        	writeOutputBuffer();
        }
        Run run = new Run(this.runStart, this.runEnd, this.filename);
        appendRun(run, this.runs);
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
        this.runEnd += this.out.getSize();
        this.out = new Page(null);
    }

    private void appendRun(Run run, Run[] runsArray) {
        Run[] newRuns = new Run[runsArray.length + 1];
        for (int i = 0; i < this.runs.length; i++) {
            newRuns[i] = runsArray[i];
        }
        newRuns[newRuns.length - 1] = run;
        runsArray = newRuns;
    }
}
