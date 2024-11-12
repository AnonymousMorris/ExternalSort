
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
    	String args = "test.txt";
        Controller controller = new Controller();
        controller.run(args);
    }

}


