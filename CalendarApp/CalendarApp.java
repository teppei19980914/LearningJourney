package CalendarApp;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.*;

public class CalendarApp extends JFrame {
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private YearMonth currentMonth;

    public CalendarApp(YearMonth yearMonth) {
        this.currentMonth = yearMonth;

        setTitle("カレンダー");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ヘッダー（ボタンと月ラベル）
        JPanel headerPanel = new JPanel(new BorderLayout());

        JButton prevButton = new JButton("◀ 前の月");
        JButton nextButton = new JButton("次の月 ▶");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // カレンダー描画パネル
        calendarPanel = new JPanel(new GridLayout(0, 7));
        add(calendarPanel, BorderLayout.CENTER);

        // イベント登録
        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendarPanel();
        });

        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendarPanel();
        });

        updateCalendarPanel();
        // setVisible(true);
    }

    private void updateCalendarPanel() {
        calendarPanel.removeAll();

        // ヘッダー更新
        monthLabel.setText(currentMonth.getYear() + "年 " + currentMonth.getMonthValue() + "月");

        // 曜日ラベル
        String[] days = {"日", "月", "火", "水", "木", "金", "土"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            calendarPanel.add(lbl);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // 日曜=0
        int daysInMonth = currentMonth.lengthOfMonth();

        // 空白を追加
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // 日付を追加
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            JLabel lbl = new JLabel(String.valueOf(day), SwingConstants.CENTER);

            DayOfWeek dow = date.getDayOfWeek();
            if (dow == DayOfWeek.SUNDAY || HolidayUtil.isHoliday(date)) {
                lbl.setForeground(Color.RED);
            } else if (dow == DayOfWeek.SATURDAY) {
                lbl.setForeground(Color.BLUE);
            }

            calendarPanel.add(lbl);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            YearMonth now = YearMonth.now();
            new CalendarApp(now);
        });
    }
}
