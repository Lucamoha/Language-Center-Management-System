package com.ui.util;
import javax.swing.*;
import java.awt.*;

public class JTextFieldPlaceholder extends JTextField {
    private String placeholder;

    public JTextFieldPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Kiểm tra nếu text trống và không có focus thì vẽ placeholder
        if (getText().isEmpty() && !(FocusManager.getCurrentManager().getFocusOwner() == this)) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            // Căn chỉnh vị trí chữ placeholder
            g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getAscent() + getInsets().top);
        }
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
}
