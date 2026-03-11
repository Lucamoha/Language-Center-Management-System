package com.ui.dialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TimetableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Căn giữa nội dung cho tất cả các ô
        setHorizontalAlignment(JLabel.CENTER);

        if (column == 0) {
            c.setBackground(new Color(240, 240, 240));
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        } else {
            // Ô có chứa dữ liệu
            if (value != null && !value.toString().isEmpty()) {
                c.setBackground(new Color(204, 229, 255)); // Màu xanh nhạt
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(Color.WHITE); // Ô trống để màu trắng
            }
        }

        // Nếu người dùng đang click chọn ô đó
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        }

        return c;
    }
}