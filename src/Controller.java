import java.io.IOException;

public class Controller {
    // private Reader reader;
    
    public Controller() {
        // this.reader = new Reader(filename);
    }

    public void run(String filename) throws IOException {
        Reader reader = new Reader(filename);
        while (reader.hasNext()) {
            Page page = reader.nextPage();
            printPage(page);
        }
    }

    private void printPage(Page page) {
        System.out.print(page.records[0].getID() + " ");
        System.out.print(page.records[0].getKey() + " ");
    }
}
