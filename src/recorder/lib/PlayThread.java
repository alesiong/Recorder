package recorder.lib;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Play sound in a new thread
 */
class PlayThread extends Thread {

    /* raw data to be played */
    private byte[] dataToPlay = null;

    /* audio format of data */
    private AudioFormat audioFormat = null;

    /* DataLine to play to */
    private SourceDataLine sourceDataLine = null;

    /**
     * Create a audio player thread
     * @param dataToPlay    Audio data to play with
     * @param audioFormat   Format of the data
     * @param sourceDataLine    DataLine to play to
     */
    PlayThread(byte[] dataToPlay, AudioFormat audioFormat,
               SourceDataLine sourceDataLine) {
        this.dataToPlay = dataToPlay;
        this.audioFormat = audioFormat;
        this.sourceDataLine = sourceDataLine;
    }

    /**
     * Main loop to play audio until the audio ends.
     */
    @Override
    public void run() {

        byte buf[] = new byte[102400];
        try (ByteArrayInputStream byteArrayIS =
                     new ByteArrayInputStream(dataToPlay);
             AudioInputStream audioIS =
                     new AudioInputStream(byteArrayIS, audioFormat,
                             dataToPlay.length / audioFormat.getFrameSize())) {
            // starts playing
            sourceDataLine.start();
            int readBytes;

            // reads data into buffer until there is no data left
            while ((readBytes = audioIS.read(buf, 0, buf.length)) != -1) {
                // writes the buffer to the DataLine(speaker)
                if (readBytes > 0) {
                    sourceDataLine.write(buf, 0, readBytes);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sourceDataLine.drain();
            sourceDataLine.stop();
        }

    }
}
