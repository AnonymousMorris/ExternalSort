import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Reader {
    private String filename;
    private RandomAccessFile raf;
    private BufferedReader textReader;  // For reading text filesprivate int writePos;
    private boolean isBinary = true;

    public Reader(String filename) throws IOException {
        this.filename = filename;
        File file = new File(filename);
        this.raf = new RandomAccessFile(file, "r");
    }
    // public Reader(String filename, boolean isBinary) throws IOException {
    //     this.filename = filename;
    //     File file = new File(filename);
    //     this.textReader = new BufferedReader(new FileReader(filename));
    //     isBinary = isBinary;
    // }

//    public Page nextPage() throws IOException {
//        byte[] basicBuffer = new byte[ByteFile.BYTES_PER_BLOCK];
//        ByteBuffer bb = ByteBuffer.wrap(basicBuffer);
//        raf.read(basicBuffer);
//        return new Page(bb);
//    }
    
    // Method to read next page from text file
    public Page nextPage() throws IOException {
        if (textReader == null) {
            throw new IOException("Text reader not initialized");
        }

        List<Record> recordsList = new ArrayList<>();
        String line;
        int count = 0;

        // Read lines until capacity is reached or no more lines are available
        while (count < ByteFile.RECORDS_PER_BLOCK) {
        	if ((line = textReader.readLine()) == null) {
        		break;
        	}
            String[] parts = line.split(" ");
            if (parts.length != 2) {
                throw new IOException("Invalid record format in text file");
            }

            long id = Long.parseLong(parts[0]);
            double key = Double.parseDouble(parts[1]);
            recordsList.add(new Record(id, key));
            count++;
        }

        // Convert list of records to array and create a Page
        Record[] recordsArray = recordsList.toArray(new Record[0]);
        Page page = new Page(recordsArray, count); // Added alternative Page constructor

        return page;
    }

    public boolean hasNext() throws IOException {
    	if (isBinary) {
    		return raf.getFilePointer() < raf.length();
    	}
    	else {
    		return textReader.ready();
    	}
    }
    
    public void close() throws IOException {
        if (isBinary) {
        	raf.close();
        }
        else {
        	textReader.close();
        }
        
    }
}
