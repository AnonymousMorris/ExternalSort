import java.io.IOException;

public class Controller {
    // private Reader reader;
    
    public Controller() {
        // this.reader = new Reader(filename);
    }

    public void run(String filename) throws IOException {
        Reader reader = new Reader(filename);
        int counter = 0;
        while (reader.hasNext()) {
            Page page = reader.nextPage();
            printPage(page);
            if (counter % 5 == 4) {
            	System.out.print("\n");
            }
            counter++;
        }
    }

    private void printPage(Page page) {
        System.out.print(page.records[0].getID() + " ");
        System.out.print(page.records[0].getKey() + " ");
    }
}
