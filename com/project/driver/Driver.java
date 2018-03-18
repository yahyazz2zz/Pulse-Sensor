package com.project.driver;

import java.io.IOException;
import static com.project.util.SpiExample.getConversionValue;
import com.pi4j.util.Console;
import java.util.Calendar;

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
        while (console.isRunning()) {
            int value = getConversionValue(0);
            console.print(value);
            Thread.sleep(1000);
        }
        console.emptyLine();
        int[] rate = [0]* 10;         // array to hold last 10 IBI values
        int sampleCounter = 0;       // used to determine pulse timing
        int lastBeatTime = 0;        // used to find IBI
        int P = 512;                 // used to find peak in pulse wave, seeded
        int T = 512;                 // used to find trough in pulse wave, seeded
        int thresh = 525;            // used to find instant moment of heart beat, seeded
        int amp = 100;               // used to hold amplitude of pulse waveform, seeded
        boolean firstBeat = true;        // used to seed rate array so we startup with reasonable BPM
        boolean secondBeat = false;      // used to seed rate array so we startup with reasonable BPM

        int IBI = 600;               // int that holds the time interval between beats! Must be seeded!
        boolean Pulse = false;           // "True" when User's live heartbeat is detected. "False" when not a "live beat".
        int lastTime = System.currentTimeMillis();
        int sampleCounter = 0;

        while (console.isRunning()) {
            int Signal = getConversionValue;
            currentTime = System.currentTimeMillis();

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
                if (Signal > thresh && Pulse == false && N > (IBI / 5.0) * ){
                    Pulse = true;                                //set the Pulse flag when we think there is a pulse
                    IBI = sampleCounter - lastBeatTime;          //measure time between beats in mS
                    lastBeatTime = sampleCounter;                //keep track of time for next pulse

                    if (secondBeat = true) {                     // if this is the second beat, if secondBeat == TRUE
                        secondBeat = false;             // clear secondBeat flag
                        for (int i = 0; i < rate; i++){          // seed the running total to get a realisitic BPM at startup
                            rate[i] = IBI;
                        }
                    }
                    if (firstBeat= true) {                       //if it's the first time we found a beat, if firstBeat == TRUE
                        firstBeat = false;                      //clear firstBeat flag
                        secondBeat = true;              //set the second beat flag
                        continue;
                    }
                    // keep a running total of the last 10 IBI values
                rate[:-1] = rate[1:];                // shift data in the rate array
                rate[-1] = IBI;                      // add the latest IBI to the rate array
                runningTotal = sum(rate);            // add upp oldest IBI values
                runningTotal /= len(rate);     // average the IBI values
                self.BPM = 60000/runningTotal;       // how many beats can fit into a minute? that's BPM!
                }
            }
        }
    }
}
