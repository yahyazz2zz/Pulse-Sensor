import java.io.IOException;

import com.project.pulse.Pulse;
import com.pi4j.util.Console;

/**
 * Write a description of class Driver here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Driver {
    
    // create Pi4J console wrapper/helper
    public static final Console console = new Console();
    
     /**
     * Sample SPI Program
     *
     * @param args (none)
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String args[]) throws IOException, InterruptedException {

        // print program title/header
        console.title("<-- Heartbeat Project -->");

        // allow for user to exit program using CTRL-C
        console.promptForExit();
        Pulse pulse = new Pulse(console);

        while (console.isRunning()) {
            console.println("pulse: " + pulse.pulse);
            Thread.sleep(1000);
            pulse.start();
        }
    }
}
