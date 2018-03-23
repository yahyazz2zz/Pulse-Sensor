import java.io.IOException;
import com.project.util.SpiUtil;
import com.pi4j.util.Console;
import java.util.stream.*;
import java.util.Arrays;

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


        int BPM = 0;
        int[] rate = new int[10];    // array to hold last 10 IBI values
        int sampleCounter = 0;       // used to determine pulse timing
        int lastBeatTime = 0;        // used to find IBI
        int P = 512;                 // used to find peak in pulse wave, seeded
        int T = 512;                 // used to find trough in pulse wave, seeded
        int thresh = 750;            // used to find instant moment of heart beat, seeded
        int amp = 100;               // used to hold amplitude of pulse waveform, seeded
        boolean firstBeat = true;        // used to seed rate array so we startup with reasonable BPM
        boolean secondBeat = false;      // used to seed rate array so we startup with reasonable BPM
	SpiUtil spi = new SpiUtil();
        int IBI = 600;               // int that holds the time interval between beats! Must be seeded!
        boolean Pulse = false;           // "True" when User's live heartbeat is detected. "False" when not a "live beat".
        long lastTime = System.currentTimeMillis();

        while (console.isRunning()) {
            
            int Signal = spi.getConversionValue((short) 0);
            long currentTime = System.currentTimeMillis();

            sampleCounter += currentTime - lastTime;
            lastTime = currentTime;

            int N = sampleCounter - lastBeatTime;

            if (Signal < thresh &&  N > (IBI/5)*3) {
                if (Signal < T) {
                    T = Signal;
                }
            }
            if (Signal > thresh && Signal > P){
                P = Signal;
            }
            if (N > 250){                                        // avoid high frequency noise
                if ((Signal > thresh) && (Pulse == false) && (N > (IBI / 5.0) * 3) ){
                    Pulse = true;                                //set the Pulse flag when we think there is a pulse
                    IBI = sampleCounter - lastBeatTime;          //measure time between beats in mS
                    lastBeatTime = sampleCounter;                //keep track of time for next pulse
                    if (secondBeat == true) {                     // if this is the second beat, if secondBeat == TRUE
                        secondBeat = false;                      // clear secondBeat flag
                        for (int i = 0; i < rate.length -1; i++){          // seed the running total to get a realisitic BPM at startup
                            rate[i] = IBI;
                        }
                    }
                    if (firstBeat == true) {                      //if it's the first time we found a beat, if firstBeat == TRUE
                        firstBeat = false;                      //clear firstBeat flag
                        secondBeat = true;                      //set the second beat flag
                        continue;
                    }
                    // keep a running total of the last 10 IBI values
                    System.arraycopy(rate, 1, rate, 0, rate.length -1);  // add the latest IBI to the rate array
                    rate[rate.length -1] = IBI;
                    int runningTotal = IntStream.of(rate).sum();                     // add upp oldest IBI values
                    runningTotal /= rate.length;                // average the IBI values
                    BPM = 60000/runningTotal;                   // how many beats can fit into a minute? that's BPM!
                }
            }
            
            if (Signal < thresh && Pulse == true) {       //when the values are going down, the beat is over
                Pulse = false;                        //reset the Pulse flag so we can do it again
                amp = P - T;                              //get amplitude of the pulse wave
                thresh = amp / 2 + T;                     //set thresh at 50 % of the amplitude
                P = thresh;                               //reset these for next time
                T = thresh;
            }

            if (N > 2500) {                               //if 2.5 seconds go by without a beat
                thresh = 750;                             //set thresh default
                P = 512;                              //set P default
                T = 512;                              //set T default
                lastBeatTime = sampleCounter;         //bring the lastBeatTime up to date
                firstBeat = true;                     //set these to avoid noise
                secondBeat = false;           //when we get the heartbeat back
                BPM = 0;
            }
		console.println("Pulse: " + Pulse);
            Thread.sleep(50);
        }
    }
}
