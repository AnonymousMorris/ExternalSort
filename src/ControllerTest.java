
import java.io.IOException;

import student.TestCase;

/**
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class ControllerTest extends TestCase {


    /**
     * set up for tests
     */
    public void setUp() {
        //nothing to set up.
    }

    /**
     * T
     * @throws IOException 
     */
    public void testExternalsort() throws IOException {
        //        String args = "data/sampleInput16.bin";
        //        String args = "data/test.txt";
        //    	String args = "gen/test.txt";
        //    	String args = "test.bin";
        //        Controller controller = new Controller();
        //        controller.run(args);
    }

    public void testWithGen200() throws IOException{
        String filename = "test.bin";
        // generate data
        ByteFile bytefile = new ByteFile(filename, 200);
        bytefile.writeRandomRecords();
        // sort data
        Controller controller = new Controller();
        controller.run(filename);
        // check data
        assertTrue(bytefile.isSorted());
    }

    public void testWithGen1000() throws IOException{
        String filename = "test.bin";
        // generate data
        ByteFile bytefile = new ByteFile(filename, 1000);
        bytefile.writeRandomRecords();
        // sort data
        Controller controller = new Controller();
        controller.run(filename);
        // check data
        assertTrue(bytefile.isSorted());
    }

    public void testWithGenWeirdNumber() throws IOException{
        String filename = "test.bin";
        // generate data
        ByteFile bytefile = new ByteFile(filename, 666);
        bytefile.writeRandomRecords();
        // sort data
        Controller controller = new Controller();
        controller.run(filename);
        // check data
        assertTrue(bytefile.isSorted());
    }

}


