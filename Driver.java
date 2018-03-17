import java.io.IOException;
import com.project.util.SpiUtil;
import com.pi4j.util.Console;

/**
 * Write a description of class Driver here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Driver {
    
    // create Pi4J console wrapper/helper
    protected static final Console console = new Console();
    
        /**
     * Sample SPI Program
     *
     * @param args (none)
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String args[]) throws InterruptedException, IOException {

        // print program title/header
        console.title("<-- Heartbeat Project -->");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // continue running program until user exits using CTRL-C
        while(console.isRunning()) {
            SpiUtil spi = new SpiUtil();
            int value = spi.getConversionValue((short) 0);
            console.println(value);
            Thread.sleep(200);
        }
        console.emptyLine();
    }
}
