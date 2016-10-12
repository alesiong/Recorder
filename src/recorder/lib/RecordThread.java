package recorder.lib;

import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to record sound in a new thread.
 */
public class RecordThread extends Thread {

    private boolean continueRecording;
    private byte[] recordData;
    private TargetDataLine targetDataLine;
    private Logger logger;

    /**
     * @param targetDataLine A TargetDataLine object from which the audio is read.
     */
    public RecordThread(TargetDataLine targetDataLine) {
        this.targetDataLine = targetDataLine;
        continueRecording = true;
        logger = Logger.getLogger("recorder.lib.RecordThread");
    }

    //将字节数组包装到流里，最终存入到baos中
    @Override
    public void run() {
        // Buffer to store audio data
        byte buf[] = new byte[102400];
        try (ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream()) {
            logger.log(Level.INFO, "Start Recording");
            // start data recording
            targetDataLine.start();
            while (continueRecording) {

                //当停止录音没按下时，该线程一直执行
                //从数据行的输入缓冲区读取音频数据。
                //要读取bts.length长度的字节,cnt 是实际读取的字节数
                int readBytes = targetDataLine.read(buf, 0, buf.length);
                if (readBytes > 0)
                    byteArrayOS.write(buf, 0, readBytes);
            }
            logger.log(Level.INFO, "Stop Recording");
            targetDataLine.stop();
            logger.log(Level.INFO, "Draining data from buffer...");
            targetDataLine.drain();
            int readBytes = targetDataLine.read(buf, 0, buf.length);
            if (readBytes > 0)
                byteArrayOS.write(buf, 0, readBytes);
            recordData = byteArrayOS.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return continueRecording;
    }

    public void stopRecording() {
        continueRecording = false;
    }

    public byte[] getRecordData() {
        if (isRecording())
            return null;
        return recordData;
    }
}
