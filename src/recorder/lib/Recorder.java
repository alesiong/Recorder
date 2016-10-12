package recorder.lib;

import javax.sound.sampled.*;
import java.io.*;

public class Recorder {

    private RecordThread recordThread = null;
    private AudioFormat audioFormat = null;
    private byte[] recordData = null;
    private TargetDataLine targetDataLine = null;
    private SourceDataLine sourceDataLine = null;

    public Recorder() {
        this(
                44100f, // sample rate
                16,     // sample bits
                1,      // chennels
                true,   // signed
                false); // bigEndian
    }

    public Recorder(float sampleRate, int sampleBits, int channels,
                    boolean signed, boolean bigEndian) {

        audioFormat = new AudioFormat(sampleRate, sampleBits, channels,
                signed, bigEndian);

        //af为AudioFormat也就是音频格式
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            targetDataLine = (TargetDataLine) (AudioSystem.getLine(targetInfo));
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        System.out.println(targetDataLine.getBufferSize());

        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }


    public void start() {
        //创建播放录音的线程
        recordThread = new RecordThread(targetDataLine);
        recordThread.start();
    }


    public void stop() {
        recordThread.stopRecording();
        try {
            recordThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recordData = recordThread.getRecordData();
    }

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

    public void save(File file) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(file);
        System.out.println("Saving to file:" + file.getAbsolutePath());
        save(fos);
    }

    public void save() throws FileNotFoundException {
        File file = new File(new File("").getAbsolutePath() + File.separator +
                "record_" + System.currentTimeMillis() + ".wav");
        save(file);
    }

    public void play() {
        //创建播放进程
        PlayThread py = new PlayThread(recordData, audioFormat, sourceDataLine);
        Thread player = new Thread(py);
        player.start();

    }

    @Override
    protected void finalize() throws Throwable {
        targetDataLine.close();
        sourceDataLine.close();
        super.finalize();
    }
}
