package example;

import recorder.lib.Recorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

public class RecorderGUIExample {
    public static void main(String args[]) {
        new RecorderGUI();
    }
}

class RecorderGUI extends JFrame implements ActionListener {


    private JButton captureBtn, stopBtn, playBtn, saveBtn;
    private Recorder recorderCore = null;


    //构造函数
    public RecorderGUI() {


        recorderCore = new Recorder();

        //组件初始化  
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        JPanel jp3 = new JPanel();

        //定义字体  
//        Font myFont = new Font("华文新魏", Font.BOLD, 30);
        JLabel jl1 = new JLabel("Press Start Recording to record.");
//        jl1.setFont(myFont);
        jp1.add(jl1);

        captureBtn = new JButton("Start Recording");
        //对开始录音按钮进行注册监听  
        captureBtn.addActionListener(this);
        captureBtn.setActionCommand("captureBtn");
        //对停止录音进行注册监听  
        stopBtn = new JButton("Stop Recording");
        stopBtn.addActionListener(this);
        stopBtn.setActionCommand("stopBtn");
        //对播放录音进行注册监听  
        playBtn = new JButton("Play RecordThread");
        playBtn.addActionListener(this);
        playBtn.setActionCommand("playBtn");
        //对保存录音进行注册监听  
        saveBtn = new JButton("Save RecordThread to File");
        saveBtn.addActionListener(this);
        saveBtn.setActionCommand("saveBtn");


        this.add(jp1, BorderLayout.SOUTH);
//        this.add(jp2, BorderLayout.CENTER);
        this.add(jp3, BorderLayout.EAST);
        jp3.setLayout(new GridLayout(4, 1, 0, 10));
        jp3.add(captureBtn);
        jp3.add(stopBtn);
        jp3.add(playBtn);
        jp3.add(saveBtn);
        //设置按钮的属性  
        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        //设置窗口的属性  
        this.setSize(400, 300);
        this.setTitle("Recorder");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);


    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("captureBtn")) {
            //点击开始录音按钮后的动作  
            //停止按钮可以启动  
            captureBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            playBtn.setEnabled(false);
            saveBtn.setEnabled(false);

            recorderCore.start();
        } else if (e.getActionCommand().equals("stopBtn")) {
            //点击停止录音按钮的动作  
            captureBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            saveBtn.setEnabled(true);

            recorderCore.stop();

        } else if (e.getActionCommand().equals("playBtn")) {
            recorderCore.play();
        } else if (e.getActionCommand().equals("saveBtn")) {
            try {
                recorderCore.save();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }

    }

}