package recorder.lib;

import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to record sound in a new thread.
 */
class RecordThread extends Thread {

    /* Flag to tell the main loop whether to stop recording */
    private volatile boolean continueRecording;

    /* Buffer to store recorded data */
    private byte[] recordData;

    /* DataLine to record from */
    private TargetDataLine targetDataLine;

    private Logger logger;

    /**
     * Create record thread with a {@link TargetDataLine}.
     * @param targetDataLine A TargetDataLine object from which the audio is read.
     */
    RecordThread(TargetDataLine targetDataLine) {
        this.targetDataLine = targetDataLine;
        continueRecording = true;
        logger = Logger.getLogger("recorder.lib.RecordThread");
    }

    /**
     * Main loop of the thread, continues reading audio data to buffer until
     * {@link #stopRecording()} is called.
     */
    @Override
    public void run() {
        // Buffer to store audio data
        byte buf[] = new byte[102400];
        try (ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream()) {
            logger.log(Level.INFO, "Start Recording");

            // start data recording
            targetDataLine.start();
            while (continueRecording) {
                // reads data from DataLine
                int readBytes = targetDataLine.read(buf, 0, buf.length);
                if (readBytes > 0)
                    byteArrayOS.write(buf, 0, readBytes);
            }
            logger.log(Level.INFO, "Stop Recording");
            targetDataLine.stop();
            logger.log(Level.INFO, "Draining data from buffer...");

            // this method may block
            targetDataLine.drain();
            // writes the data left in the DataLine to buffer
            int readBytes = targetDataLine.read(buf, 0, buf.length);
            if (readBytes > 0)
                byteArrayOS.write(buf, 0, readBytes);
            recordData = byteArrayOS.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return  Whether the recording loop is running
     */
    boolean isRecording() {
        return continueRecording;
    }

    /**
     * Stop recording. Caller can invoke {@link #join()} to wait until the
     * recording is actually stopped.
     */
    void stopRecording() {
        continueRecording = false;
    }

    /**
     * Returns the audio data in byte array, the recording needs to be stopped
     * first.
     * @return  The audio data in buffer, <code>null</code> if it recording is
     * not stopped
     */
    byte[] getRecordData() {
        if (isRecording())
            return null;
        return recordData;
    }
}
