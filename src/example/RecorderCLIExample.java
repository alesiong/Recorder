package example;


import recorder.lib.Recorder;

import javax.sound.sampled.AudioFormat;
import java.io.*;

public class RecorderCLIExample {

    public static void main(String args[]) {
        new RecorderCLI();
    }
}

class RecorderCLI {
    private Recorder recorder = null;

    RecorderCLI() {
        AudioFormat format = getOptions();
        recorder = new Recorder(format);
        try {
            mainLoop();
        } catch (IOException e) {
            throw new RuntimeException("Can not read from the command line!");
        }
    }

    private void mainLoop() throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        PrintStream stdout = System.out;
        for(;;) {
            printMenu();
            String command = stdin.readLine();
            switch (command) {
                case "record":
                case "r":
                    stdout.println("Type in `stop` or `s` to stop recording");
                    recorder.start();
                    break;
                case "play":
                case "p":
                    recorder.play();
                    break;
                case "save":
                    recorder.save();
                    break;
                case "stop":
                case "s":
                    recorder.stop();
                    break;
                case "quit":
                case "q":
                    return;
                default:
                    stdout.println("Unknown command");
            }
        }
    }

    private void printMenu() {
        System.out.print("Type in\n" +
                "`record` or `r` to start recording\n" +
                "`play` or `p` to play recorded data\n" +
                "`save` to save data to file\n" +
                "`quit` or `q` to quit\n" +
                "command>");
    }

    private AudioFormat getOptions() {
        float sampleRate = 44100f;
        int sampleBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleBits, channels,
                signed, bigEndian);
    }
}
