package recorder.lib;

import javax.sound.sampled.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core of recorder. Implements some fundamental methods for audio recording.
 *
 * @author alesiong
 */
public class Recorder {

    /* The thread to record sound from microphone */
    private RecordThread recordThread = null;

    /* The format of audio that is recorded */
    private AudioFormat audioFormat = null;

    /* The raw data of audio recorded */
    private byte[] recordData = null;

    /* DataLine to access to input from microphone */
    private TargetDataLine targetDataLine = null;

    /* DataLine to access to output to speaker */
    private SourceDataLine sourceDataLine = null;

    private Logger logger = null;

    /**
     * Default constructor, setting the audio format to default value as:
     * <table border=1>
     * <tr>
     * <th>Attribute</th>
     * <th>Value(Type)</th>
     * </tr>
     * <tr>
     * <td>Sample Rate</td>
     * <td>44100.0({@link java.lang.Float Float})</td>
     * </tr>
     * <tr>
     * <td>Sample Size in Bits</td>
     * <td>16({@link java.lang.Integer Integer})</td>
     * </tr>
     * <tr>
     * <td>Channel Number</td>
     * <td>1({@link java.lang.Integer Integer})</td>
     * </tr>
     * <tr>
     * <td>Singed</td>
     * <td><code>true</code>({@link java.lang.Boolean Boolean})</td>
     * </tr>
     * <tr>
     * <td>Big Endian</td>
     * <td><code>false</code>({@link java.lang.Boolean Boolean})</td>
     * </tr>
     * </table>
     */
    public Recorder() {
        this(
                44100f, // sample rate
                16,     // sample bits
                1,      // channels
                true,   // signed
                false); // bigEndian
    }

    /**
     * Create a constructor with custom audio format.
     *
     * @param sampleRate Sample rate
     * @param sampleBits Sample size in bts
     * @param channels   Channel number
     * @param signed     Whether data signed or unsigned
     * @param bigEndian  Whether data big or small endian
     */
    public Recorder(float sampleRate, int sampleBits, int channels,
                    boolean signed, boolean bigEndian) {

        this(new AudioFormat(sampleRate, sampleBits, channels,
                signed, bigEndian));

    }

    /**
     * Create a constructor with custom {@link javax.sound.sampled.AudioFormat
     * AudioFormat} object.
     *
     * @param audioFormat Custom audio format
     */
    public Recorder(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
        // create target DataLine to record from mic
        try {
            targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        // create source DataLine to play to speaker
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        logger = Logger.getLogger("recorder.lib.Recorder");
    }


    /**
     * Start recording, to stop recording, see {@link #stop()}.
     */
    public void start() {
        // create a new thread to record in background
        recordThread = new RecordThread(targetDataLine);
        recordThread.start();
    }


    /**
     * Stop recording and save the raw audio data to buffer.
     * This method may block when draning data from the DataLine buffer.
     *
     * @throws NullPointerException The method is called when the recording
     *                              did not start.
     */
    public void stop() {
        if (!recordThread.isRecording()) {
            // TODO: Change to more specific Exception
            throw new NullPointerException();
        }
        recordThread.stopRecording();
        try {
            // wait for actually stopping
            recordThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recordData = recordThread.getRecordData();
    }

    /**
     * Write the audio data to an {@link java.io.OutputStream OutpuStream}.
     * Actually the stream is written with .wav file style, maybe changed into
     * sampled data it the future.
     *
     * @param os Stream to write data to
     */
    public void save(OutputStream os) {

        try (ByteArrayInputStream byteArrayIS =
                     new ByteArrayInputStream(recordData);
             AudioInputStream audioIS =
                     new AudioInputStream(byteArrayIS, audioFormat,
                             recordData.length / audioFormat.getFrameSize())) {

            AudioSystem.write(audioIS, AudioFileFormat.Type.WAVE, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the audio data to a file.
     *
     * @param file File to write to
     * @throws FileNotFoundException If the target file cannot be written
     */
    public void save(File file) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(file);
        logger.log(Level.INFO, "Saving to file:" + file.getAbsolutePath());
        save(fos);
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the audio data to a file naming <b>record_{current time}.wav</b>.
     *
     * @throws FileNotFoundException If the file cannot be written
     */
    public void save() throws FileNotFoundException {
        File file = new File(new File("").getAbsolutePath() + File.separator +
                "record_" + System.currentTimeMillis() + ".wav");
        save(file);
    }

    /**
     * Play the recorded audio to the speaker.
     */
    public void play() {
        // create a thread to play audio in background
        PlayThread player = new PlayThread(recordData, audioFormat, sourceDataLine);
        player.start();

    }

    @Override
    protected void finalize() throws Throwable {
        targetDataLine.close();
        sourceDataLine.close();
        super.finalize();
    }
}
