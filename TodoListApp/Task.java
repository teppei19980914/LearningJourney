package TodoListApp;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

// タスク情報を保持するクラス
public class Task {
    String title; // タイトル
    String description; // 内容
    LocalDate dueDate; // 期限日
    LocalDate createdDate; // 登録日
    boolean isDone; // 完了フラグ
    String priority; // 優先度（高・中・低）
    boolean isOverdue; // 期限切れフラグ
    public LocalDate updatedDate; // 更新日

    // コンストラクタ
    public Task(String title, String description, boolean isDone, LocalDate dueDate, LocalDate createdDate, LocalDate updatedDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdDate = createdDate;
        this.isDone = isDone;
        this.updatedDate = updatedDate;
        updatePriorityAndOverdue();
    }

    // 優先度と期限切れ状態を更新
    public void updatePriorityAndOverdue() {
        LocalDate today = LocalDate.now();
        long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);

        if (daysUntilDue < 0) {
            isOverdue = true;
            priority = "高";
        } else if (daysUntilDue <= 1) {
            priority = "高";
        } else if (daysUntilDue <= 7) {
            priority = "中";
        } else {
            priority = "低";
        }
    }

    // タスク情報を見やすい形式で表示
    @Override
    public String toString() {
        return String.format("[%s] %s (内容: %s, 登録: %s, 期限: %s, 優先度: %s%s)",
                isDone ? "✓" : " ",
                title,
                description,
                createdDate,
                dueDate,
                priority,
                isOverdue ? ", ⚠期限切れ" : "");
    }

    // CSV形式に変換
    public String toCSV() {
        return String.join(",",
                title,
                description,
                Boolean.toString(isDone),
                dueDate.toString(),
                createdDate.toString(),
                updatedDate != null ? updatedDate.toString() : "",
                priority,
                Boolean.toString(isOverdue));
    }

    // JSON形式に変換
    public String toJson() {
        return String.format(
                "{\"title\":\"%s\",\"description\":\"%s\",\"dueDate\":\"%s\",\"isDone\":%s,\"createdDate\":\"%s\"}",
                escape(title), escape(description), dueDate, isDone, createdDate);
    }

    // JSON文字列からTaskオブジェクトに変換
    public static Task fromJson(String jsonLine) {
        Map<String, String> map = new HashMap<>();
        jsonLine = jsonLine.trim().replaceAll("[{}\"]", "");
        for (String pair : jsonLine.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2)
                map.put(kv[0].trim(), kv[1].trim());
        }
        return new Task(
                unescape(map.get("title")),
                unescape(map.get("description")),
                Boolean.parseBoolean(map.get("isDone")),
                LocalDate.parse(map.get("dueDate")),
                LocalDate.parse(map.get("createdDate")),
                LocalDate.parse(map.get("updatedDate")));
    }

    public static Task fromCSV(String csvLine) {
        // CSV行の前後の余分なスペースをトリム
        csvLine = csvLine.trim();

        String csvString = (String) csvLine.replaceAll("\"", "");
        // CSVの区切り文字で分割（ダブルクォーテーション内のカンマは無視する）
        String[] fields = csvString.split(",");

        // フィールド数が正しいか確認
        if (fields.length != 8) {
            System.err.println("CSVのフィールド数が不正です。予想されるフィールド数は8つですが、実際のフィールド数は " + fields.length + " です。");
            for (int i = 0; i < fields.length; i++) {
                System.out.println("フィールド[" + i + "]: " + fields[i]);
            }
            throw new IllegalArgumentException("CSVのフィールド数が不正です。予想されるフィールド数は5つです。");
        }

        // 各フィールドを適切にパース
        String title = fields[0];
        String description = fields[1];
        boolean isDone = Boolean.parseBoolean(fields[2]); // 完了フラグ
        LocalDate dueDate = LocalDate.parse(fields[3]); // 期限日
        LocalDate createdDate = LocalDate.parse(fields[4]); // 登録日
        LocalDate updatedDate = LocalDate.parse(fields[5]); // 更新日

        return new Task(title, description, isDone, dueDate, createdDate, updatedDate);
    }

    // JSONエスケープ
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // JSONアンエスケープ
    private static String unescape(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    // テキストファイル用にデータを文字列に変換
    public String toDataString() {
        return String.join("|",
                title,
                description,
                dueDate.toString(),
                Boolean.toString(isDone),
                createdDate.toString());
    }

    // テキストデータからTaskオブジェクトに変換
    public static Task fromDataString(String data) {
        String[] parts = data.split("\\|");
        return new Task(
                parts[0],
                parts[1],
                Boolean.parseBoolean(parts[3]),
                LocalDate.parse(parts[2]),
                LocalDate.parse(parts[4]),
                LocalDate.parse(parts[5]));
    }
}