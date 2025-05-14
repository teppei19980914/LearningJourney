import TodoListApp.TaskManager;
import TimerApp.TimerApp;
import CalendarApp.CalendarApp;

import java.time.YearMonth;

import javax.swing.*;

public class LeaningJourney extends JFrame {
    public LeaningJourney() {
        setTitle("統合アプリ：タイマー & ToDoリスト");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);

        // タブ用のペイン
        JTabbedPane tabbedPane = new JTabbedPane();

        // TimerAppをインスタンス化し、ContentPaneをタブに追加
        TimerApp timerApp = new TimerApp();
        tabbedPane.addTab("タイマー", timerApp.getContentPane());

        // TaskManagerをインスタンス化し、ContentPaneをタブに追加
        TaskManager taskManager = new TaskManager();
        tabbedPane.addTab("ToDoリスト", taskManager.getContentPane());

        // CalendarAppをインスタンス化し、ContentPaneをタブに追加
        YearMonth now = YearMonth.now();
        CalendarApp calendarApp = new CalendarApp(now);
        tabbedPane.addTab("カレンダー", calendarApp.getContentPane());

        // タブペインをメインフレームに追加
        add(tabbedPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LeaningJourney::new);
    }
}
