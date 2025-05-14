package TodoListApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TaskManager extends JFrame {
    private static String TASK_DIRECTORY = "./TodoListApp/Data/"; // ディレクトリ
    private static String TASK_FILE = TASK_DIRECTORY + "sample.csv"; // タスク保存用ファイル

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private List<Task> taskList;

    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField dueDateField; // yyyy-MM-dd形式で入力

    // UTF-8 明示指定で Scanner を作成
    Scanner scanner = new Scanner(System.in, "UTF-8");

    public TaskManager() {
        setTitle("ToDoアプリ（GUI版）");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        taskList = new ArrayList<>();
        initUI();
        loadTasks(); // 起動時にタスクを読み込む
        displayTasks();

        // setVisible(true);
    }

    // タスクを期限日の昇順でソート
    private void sortTasksByDueDate() {
        taskList.sort(Comparator.comparing(task -> task.dueDate));
    }

    private void initUI() {
        String[] columnNames = { "No", "タイトル", "内容", "完了", "期限日", "優先度", "期限切れ", "登録日", "更新日" };
        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // 下部のパネル（入力フォーム＋追加ボタン）
        JPanel inputPanel = new JPanel(new GridLayout(2, 1)); // 2行に分ける
        JPanel upperPanel = new JPanel(new FlowLayout());
        JPanel lowerPanel = new JPanel(new FlowLayout());

        titleField = new JTextField(10);
        descriptionField = new JTextField(15);
        dueDateField = new JTextField(10);

        upperPanel.add(new JLabel("タイトル:"));
        upperPanel.add(titleField);
        upperPanel.add(new JLabel("内容:"));
        upperPanel.add(descriptionField);
        upperPanel.add(new JLabel("期限日 (yyyy-MM-dd):"));
        upperPanel.add(dueDateField);

        JButton addButton = new JButton("追加");
        addButton.addActionListener(e -> addTask());
        lowerPanel.add(addButton);

        JButton deleteButton = new JButton("削除");
        deleteButton.addActionListener(e -> deleteTask());
        lowerPanel.add(deleteButton);

        JButton updateButton = new JButton("更新");
        updateButton.addActionListener(e -> updateTask());
        lowerPanel.add(updateButton);

        JButton completeButton = new JButton("完了");
        completeButton.addActionListener(e -> markTaskAsDone());
        lowerPanel.add(completeButton);

        JButton reloadButton = new JButton("最新化");
        reloadButton.addActionListener(e -> displayTasks());
        lowerPanel.add(reloadButton);

        JButton importButton = new JButton("一括登録");
        importButton.addActionListener(e -> importTasks());
        lowerPanel.add(importButton);

        inputPanel.add(upperPanel);
        inputPanel.add(lowerPanel);

        add(inputPanel, BorderLayout.SOUTH);

        taskTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < taskList.size()) {
                Task task = taskList.get(selectedRow);
                titleField.setText(task.title);
                descriptionField.setText(task.description);
                dueDateField.setText(task.dueDate.toString());
            }
        });

        // カスタムレンダラーを全列に適用
        TaskTableCellRenderer renderer = new TaskTableCellRenderer(taskList);
        for (int i = 0; i < taskTable.getColumnCount(); i++) {
            taskTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void addTask() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();

        if (title.isEmpty() || dueDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "タイトルと期限日は必須です。", "入力エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            Task newTask = new Task(title, description, false, dueDate, LocalDate.now(), LocalDate.now());
            taskList.add(newTask);
            displayTasks();

            saveTasks(); // タスクを保存する
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "期限日の日付形式が正しくありません（例: 2025-05-15）。", "日付エラー",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "削除するタスクを選択してください。", "選択エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "本当に削除しますか？", "確認", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 行番号は0からだが、taskListはインデックス0が最初のタスク
        taskList.remove(selectedRow);
        displayTasks();
        saveTasks();
    }

    private void updateTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "更新するタスクを選択してください。", "選択エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();

        if (title.isEmpty() || dueDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "タイトルと期限日は必須です。", "入力エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            Task task = taskList.get(selectedRow);
            task.title = title;
            task.description = description;
            task.dueDate = dueDate;
            task.updatedDate = LocalDate.now();
            task.updatePriorityAndOverdue();

            displayTasks();
            saveTasks();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "期限日の日付形式が正しくありません（例: 2025-05-15）。", "日付エラー",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markTaskAsDone() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "完了にするタスクを選択してください。", "選択エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task task = taskList.get(selectedRow);
        if (task.isDone) {
            JOptionPane.showMessageDialog(this, "このタスクは既に完了しています。", "情報", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        task.isDone = true;
        displayTasks();
        saveTasks();
    }

    private void importTasks() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("CSVファイルを選択してください");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                boolean isFirstLine = true; // 最初の行を判定するフラグ

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false; // 最初の行（カラム行）はスキップ
                        continue;
                    }
                    try {
                        Task task = Task.fromCSV(line);
                        taskList.add(task);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "タスクの登録中にエラーが発生しました: " + ex.getMessage(),
                                "登録エラー", JOptionPane.ERROR_MESSAGE);
                    }
                }

                sortTasksByDueDate(); // 読み込み後にソート
                saveTasks(); // 保存
                displayTasks(); // 画面更新

                JOptionPane.showMessageDialog(this, "タスクを一括登録しました。", "登録完了", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "ファイルの読み込み中にエラーが発生しました: " + e.getMessage(),
                        "読み込みエラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayTasks() {
        tableModel.setRowCount(0);
        int i = 1;
        for (Task task : taskList) {
            tableModel.addRow(new Object[] {
                    i,
                    task.title,
                    task.description,
                    task.isDone ? "✓" : "",
                    task.dueDate,
                    task.priority,
                    task.isOverdue ? "⚠" : "",
                    task.createdDate,
                    task.updatedDate != null ? task.updatedDate : ""
            });

            i++;
        }
    }

    private void saveTasks() {
        // ディレクトリが存在しない場合は作成
        File directory = new File(TASK_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASK_FILE))) {
            // ヘッダー行を追加
            writer.write("タイトル,内容,完了,期限日,登録日,更新日,優先度,期限切れ");
            writer.newLine();

            for (Task task : taskList) {
                writer.write(task.toCSV());
                writer.newLine();
            }
            sortTasksByDueDate();
            displayTasks();
            // 入力欄クリア
            titleField.setText("");
            descriptionField.setText("");
            dueDateField.setText("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "タスクの保存中にエラーが発生しました。", "保存エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTasks() {
        File file = new File(TASK_FILE);
        if (!file.exists()) {
            return; // ファイルがない場合は読み込まない
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TASK_FILE))) {
            String line;
            boolean isFirstLine = true; // 最初の行を判定するフラグ
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // 最初の行（カラム行）はスキップ
                    continue;
                }
                taskList.add(Task.fromCSV(line));
            }
            sortTasksByDueDate();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "タスクの読み込み中にエラーが発生しました。", "読み込みエラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManager::new);
    }
}
