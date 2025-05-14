package TimerApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TimerApp extends JFrame {
    private Timer timer;
    private int remainingSeconds;
    private JLabel timeLabel;
    private JTextField hourField, minuteField, secondField;
    private JButton startButton, stopButton, resetButton, tenMinuteButton, fiftyMinuteButton, oneHourButton,
            fourHourButton, nineHourButton;

    public TimerApp() {
        setTitle("タイマーアプリ（時間指定対応）");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        // --- 1段目：時間表示ラベル（時:分:秒） ---
        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(timeLabel);

        // --- 2段目：標準ボタン（10分・50分・1時間・4時間・9時間） ---
        JPanel standardPanel = new JPanel();
        tenMinuteButton = new JButton("10分");
        fiftyMinuteButton = new JButton("50分");
        oneHourButton = new JButton("1時間");
        fourHourButton = new JButton("4時間");
        nineHourButton = new JButton("9時間");
        standardPanel.add(tenMinuteButton);
        standardPanel.add(fiftyMinuteButton);
        standardPanel.add(oneHourButton);
        standardPanel.add(fourHourButton);
        standardPanel.add(nineHourButton);
        add(standardPanel);

        // --- 3段目：時・分・秒の入力欄 ---
        JPanel inputPanel = new JPanel();
        hourField = new JTextField(2);
        minuteField = new JTextField(2);
        secondField = new JTextField(2);
        inputPanel.add(new JLabel("時:"));
        inputPanel.add(hourField);
        inputPanel.add(new JLabel("分:"));
        inputPanel.add(minuteField);
        inputPanel.add(new JLabel("秒:"));
        inputPanel.add(secondField);
        add(inputPanel);

        // --- 4段目：各ボタン（スタート・ストップ・リセット） ---
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("スタート");
        stopButton = new JButton("ストップ");
        resetButton = new JButton("リセット");
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        add(buttonPanel);

        // --- タイマー処理（1秒ごと） ---
        timer = new Timer(1000, e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                updateLabel();
            } else {
                timer.stop();
                JOptionPane.showMessageDialog(this, "タイムアップ！");
            }
        });

        // --- 標準ボタン(10分) ---
        tenMinuteButton.addActionListener(e -> {
            if (!timer.isRunning()) {
                remainingSeconds = 600;
                updateLabel();
                timer.start();
            }
        });

        // --- 標準ボタン(50分) ---
        fiftyMinuteButton.addActionListener(e -> {
            if (!timer.isRunning()) {
                remainingSeconds = 3000;
                updateLabel();
                timer.start();
            }
        });

        // --- 標準ボタン(1時間) ---
        oneHourButton.addActionListener(e -> {
            if (!timer.isRunning()) {
                remainingSeconds = 3600;
                updateLabel();
                timer.start();
            }
        });

        // --- 標準ボタン(4時間) ---
        fourHourButton.addActionListener(e -> {
            if (!timer.isRunning()) {
                remainingSeconds = 3600 * 4;
                updateLabel();
                timer.start();
            }
        });

        // --- 標準ボタン(9時間) ---
        nineHourButton.addActionListener(e -> {
            if (!timer.isRunning()) {
                remainingSeconds = 3600 * 9;
                updateLabel();
                timer.start();
            }
        });

        // --- スタート処理 ---
        startButton.addActionListener(e -> {
            if (!timer.isRunning()) {
                try {
                    int hours = Integer.parseInt(hourField.getText());
                    int minutes = Integer.parseInt(minuteField.getText());
                    int seconds = Integer.parseInt(secondField.getText());
                    remainingSeconds = hours * 3600 + minutes * 60 + seconds;
                    updateLabel();
                    timer.start();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "時・分・秒を正しく入力してください。");
                }
            }
        });

        // --- ストップ処理 ---
        stopButton.addActionListener(e -> timer.stop());

        // --- リセット処理 ---
        resetButton.addActionListener(e -> {
            timer.stop();
            remainingSeconds = 0;
            updateLabel();
        });

        // setVisible(true);
    }

    // --- ラベルの更新（HH:mm:ss 形式） ---
    private void updateLabel() {
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TimerApp::new);
    }
}
