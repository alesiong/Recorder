package recorder.lib;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class PlayThread implements Runnable {

    private byte[] recordData = null;
    private AudioFormat audioFormat = null;
    private SourceDataLine sourceDataLine = null;

    public PlayThread(byte[] recordData, AudioFormat audioFormat,
                      SourceDataLine sourceDataLine) {
        this.recordData = recordData;
        this.audioFormat = audioFormat;
        this.sourceDataLine = sourceDataLine;
    }

    //播放baos中的数据即可
    @Override
    public void run() {
        //转换为输入流

        byte buf[] = new byte[102400];
        try (ByteArrayInputStream byteArrayIS =
                     new ByteArrayInputStream(recordData);
             AudioInputStream audioIS =
                     new AudioInputStream(byteArrayIS, audioFormat,
                             recordData.length / audioFormat.getFrameSize())) {
            sourceDataLine.start();
            int readBytes;
            //读取数据到缓存数据
            while ((readBytes = audioIS.read(buf, 0, buf.length)) != -1) {
                if (readBytes > 0) {
                    //写入缓存数据
                    //将音频数据写入到混频器
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
