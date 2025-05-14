import TodoListApp.TaskManager;
import TimerApp.TimerApp;
import CalendarApp.CalendarApp;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class LeaningJourney extends JFrame {

    private final String DEFAULT_EVENT_NAME = "sample"; // デフォルトのイベント名
    private final String DEFAULT_EVENT_CONTENT = "sample"; // デフォルトのイベント内容
    private final boolean DEFAULT_EVENT_DONE = false; // デフォルトの完了フラグ
    private final LocalDate DEFAULT_EVENT_DATE = LocalDate.of(2100, 01, 01); // デフォルトのイベント日付

    private String eventName; // イベント名
    private String eventContent; // イベント内容
    private boolean eventDone; // 完了フラグ
    private LocalDate eventDate; // イベント日付
    private boolean eventResult; // 結果（達成/未達）
    private String eventFeedback; // 感想

    private final JLabel eventLabel = new JLabel("", SwingConstants.CENTER);

    private static String TASK_DIRECTORY = "./Data/"; // ディレクトリ
    private static String TASK_FILE = TASK_DIRECTORY + "sample.csv"; // タスク保存用ファイル

    private final String CSV_COLUMN_NAME = "イベント名,イベント内容,完了,イベント日付,結果,感想\n";

    // ✅ 任意の保存先（ここを変更可能）
    private final Path eventCsvPath = Paths.get(TASK_FILE);

    public LeaningJourney() {
        setTitle("統合アプリ：タイマー & ToDoリスト");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLayout(new BorderLayout());

        loadEventInfo(); // ← 最初に読み込み

        if (eventDate != null && !eventDone && eventDate.isBefore(LocalDate.now())) {
            SwingUtilities.invokeLater(this::openResultInputDialog);
        }

        // ✅ 上部パネル
        JPanel topPanel = new JPanel(new BorderLayout());

        eventLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        eventLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateEventLabel();

        JButton openDialogButton = new JButton("イベント更新");
        openDialogButton.addActionListener(e -> openUpdateDialog());

        topPanel.add(eventLabel, BorderLayout.CENTER);
        topPanel.add(openDialogButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ✅ 中央：タブ
        JTabbedPane tabbedPane = new JTabbedPane();

        TimerApp timerApp = new TimerApp();
        tabbedPane.addTab("タイマー", timerApp.getContentPane());

        TaskManager taskManager = new TaskManager();
        tabbedPane.addTab("ToDoリスト", taskManager.getContentPane());

        YearMonth now = YearMonth.now();
        CalendarApp calendarApp = new CalendarApp(now);
        tabbedPane.addTab("カレンダー", calendarApp.getContentPane());

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // ✅ イベント情報をCSVから読み込む
    private void loadEventInfo() {
        try {
            if (Files.notExists(Paths.get(TASK_DIRECTORY))) {
                Files.createDirectories(Paths.get(TASK_DIRECTORY));
            }

            if (Files.notExists(eventCsvPath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(eventCsvPath)) {
                    writer.write(CSV_COLUMN_NAME);
                    writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                            escapeCsv(DEFAULT_EVENT_NAME),
                            escapeCsv(DEFAULT_EVENT_CONTENT),
                            DEFAULT_EVENT_DONE,
                            DEFAULT_EVENT_DATE,
                            false,
                            ""));
                }
                eventName = DEFAULT_EVENT_NAME;
                eventContent = DEFAULT_EVENT_CONTENT;
                eventDate = DEFAULT_EVENT_DATE;
                eventDone = DEFAULT_EVENT_DONE;
                eventResult = false;
                eventFeedback = "";
                return;
            }

            try (BufferedReader reader = Files.newBufferedReader(eventCsvPath)) {
                String header = reader.readLine(); // ヘッダー読み飛ばし
                String line;
                LocalDate nearestDate = LocalDate.MAX;
                String[] nearestParts = null;

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 6) {
                        boolean done = Boolean.parseBoolean(parts[2]);
                        LocalDate date = LocalDate.parse(parts[3]);
                        if (!done && date.isBefore(nearestDate)) {
                            nearestDate = date;
                            nearestParts = parts;
                        }
                    }
                }
                if (nearestParts != null) {
                    eventName = nearestParts[0];
                    eventContent = nearestParts[1];
                    eventDone = false;
                    eventDate = LocalDate.parse(nearestParts[3]);
                    eventResult = Boolean.parseBoolean(nearestParts[4]);
                    eventFeedback = nearestParts[5];
                } else {
                    // 未完了イベントがない場合はデフォルト
                    eventName = DEFAULT_EVENT_NAME;
                    eventContent = DEFAULT_EVENT_CONTENT;
                    eventDate = DEFAULT_EVENT_DATE;
                    eventDone = DEFAULT_EVENT_DONE;
                    eventResult = false;
                    eventFeedback = "";
                }
            }
        } catch (IOException | DateTimeException e) {
            System.err.println("CSV読み込みまたは作成失敗: " + e.getMessage());
        }
    }

    // ✅ CSVの特殊文字エスケープ
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    // ✅ 表示ラベル更新
    private void updateEventLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), eventDate);
        String text = String.format("%s：%s（あと%d日）", eventName, eventDate.format(formatter), daysRemaining);
        eventLabel.setText(text);
    }

    // ✅ 更新ダイアログ
    private void openUpdateDialog() {
        JDialog dialog = new JDialog(this, "イベント情報の更新", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField(eventName);
        JTextField contentField = new JTextField(eventContent);
        JTextField dateField = new JTextField(eventDate.toString());

        dialog.add(new JLabel("イベント名：", SwingConstants.CENTER));
        dialog.add(nameField);
        dialog.add(new JLabel("イベント内容：", SwingConstants.CENTER));
        dialog.add(contentField);
        dialog.add(new JLabel("イベント日付（yyyy-MM-dd）：", SwingConstants.CENTER));
        dialog.add(dateField);

        JPanel buttonPanel = new JPanel();
        JButton updateBtn = new JButton("更新");
        updateBtn.addActionListener(e -> {
            try {
                String newName = nameField.getText().trim();
                String newContent = contentField.getText().trim();
                LocalDate newDate = LocalDate.parse(dateField.getText().trim());

                // CSV全体を読み込み、該当イベントのみ更新
                List<String> lines = Files.readAllLines(eventCsvPath);
                try (BufferedWriter writer = Files.newBufferedWriter(eventCsvPath)) {
                    writer.write(CSV_COLUMN_NAME);
                    for (int i = 1; i < lines.size(); i++) {
                        String[] parts = lines.get(i).split(",", -1);
                        if (parts.length >= 6 &&
                                parts[0].equals(eventName) &&
                                parts[1].equals(eventContent) &&
                                parts[3].equals(eventDate.toString()) &&
                                parts[2].equals("false")) {
                            // 該当イベントを更新
                            writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                                    escapeCsv(newName),
                                    escapeCsv(newContent),
                                    "false",
                                    newDate.toString(),
                                    parts[4],
                                    escapeCsv(parts[5])));
                            // メモリ上の値も更新
                            eventName = newName;
                            eventContent = newContent;
                            eventDate = newDate;
                        } else {
                            writer.write(lines.get(i) + "\n");
                        }
                    }
                }
                updateEventLabel();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "日付の形式が正しくありません（例: 2025-10-12）", "エラー",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(updateBtn);

        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }

    private void openResultInputDialog() {
        JDialog dialog = new JDialog(this, "イベント結果入力", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JLabel label = new JLabel("このイベントは達成できましたか？", SwingConstants.CENTER);

        // チェックボックスで達成 or 未達
        JCheckBox checkBox = new JCheckBox("達成");
        checkBox.setSelected(false); // デフォルトは未達

        JTextArea feedbackArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("感想や結果の詳細"));

        JButton updateButton = new JButton("結果を保存");

        updateButton.addActionListener(e -> {
            eventDone = true;
            eventResult = checkBox.isSelected(); // true:達成, false:未達
            eventFeedback = feedbackArea.getText().trim();

            // CSV全体を読み込み、該当イベントのみ完了・結果・感想を更新
            try {
                List<String> lines = Files.readAllLines(eventCsvPath);
                try (BufferedWriter writer = Files.newBufferedWriter(eventCsvPath)) {
                    writer.write(CSV_COLUMN_NAME);
                    for (int i = 1; i < lines.size(); i++) {
                        String[] parts = lines.get(i).split(",", -1);
                        if (parts.length >= 6 &&
                                parts[0].equals(eventName) &&
                                parts[1].equals(eventContent) &&
                                parts[3].equals(eventDate.toString()) &&
                                parts[2].equals("false")) {
                            // 該当イベントを完了・結果・感想で更新
                            writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                                    escapeCsv(eventName),
                                    escapeCsv(eventContent),
                                    "true",
                                    eventDate.toString(),
                                    eventResult,
                                    escapeCsv(eventFeedback)));
                        } else {
                            writer.write(lines.get(i) + "\n");
                        }
                    }
                }
            } catch (IOException ex) {
                System.err.println("CSV保存失敗: " + ex.getMessage());
            }
            dialog.dispose();
            SwingUtilities.invokeLater(() -> openNextEventDialog());
        });

        dialog.add(label);
        dialog.add(checkBox);
        dialog.add(scrollPane);
        dialog.add(new JLabel()); // 空白
        dialog.add(updateButton);

        dialog.setVisible(true);
    }

    private void openNextEventDialog() {
        JDialog dialog = new JDialog(this, "新しいイベントを追加", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField();
        JTextField contentField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().plusDays(1).toString());

        dialog.add(new JLabel("イベント名：", SwingConstants.CENTER));
        dialog.add(nameField);
        dialog.add(new JLabel("イベント内容：", SwingConstants.CENTER));
        dialog.add(contentField);
        dialog.add(new JLabel("イベント日付（yyyy-MM-dd）：", SwingConstants.CENTER));
        dialog.add(dateField);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("イベント追加");

        addBtn.addActionListener(e -> {
            try {
                String newName = nameField.getText().trim();
                String newContent = contentField.getText().trim();
                LocalDate newDate = LocalDate.parse(dateField.getText().trim());

                // 追記モードで新規イベントを追加
                try (BufferedWriter writer = Files.newBufferedWriter(eventCsvPath, StandardOpenOption.APPEND)) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                            escapeCsv(newName),
                            escapeCsv(newContent),
                            "false",
                            newDate.toString(),
                            "false",
                            ""));
                }
                // メモリ上の値も更新
                eventName = newName;
                eventContent = newContent;
                eventDate = newDate;
                eventDone = false;
                eventResult = false;
                eventFeedback = "";
                updateEventLabel();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "日付の形式が正しくありません（例: 2025-10-12）", "エラー",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(addBtn);
        dialog.add(buttonPanel);

        dialog.setVisible(true);
    }

    private void appendNewEvent() {
        try (BufferedWriter writer = Files.newBufferedWriter(eventCsvPath, StandardOpenOption.APPEND)) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(eventName),
                    escapeCsv(eventContent),
                    false,
                    eventDate.toString(),
                    false,
                    ""));
        } catch (IOException e) {
            System.err.println("CSV追記失敗: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LeaningJourney::new);
    }
}
