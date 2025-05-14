package TodoListApp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class TaskTableCellRenderer extends DefaultTableCellRenderer {
    List<Task> taskList;

    public TaskTableCellRenderer(List<Task> taskList) {
        this.taskList = taskList;
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (row < taskList.size()) {
            Task task = taskList.get(row);
            if (task.isDone) {
                c.setBackground(new Color(173, 216, 230)); // 完了 → 青 (Light Blue)
            } else {
                c.setBackground(new Color(255, 255, 153)); // 未完了 → 黄 (Light Yellow)
            }
        }

        if (isSelected) {
            c.setBackground(Color.GRAY); // 選択時はグレー
        }

        // 選択時の色が優先されないようにする
        if (isSelected) {
            c.setBackground(c.getBackground().darker());
        }

        return c;
    }
}