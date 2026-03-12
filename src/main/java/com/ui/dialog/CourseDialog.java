package com.ui.dialog;

import com.dto.CourseDTO;
import com.model.academic.Course;
import com.model.academic.CourseStatus;
import com.model.academic.Level;
import com.service.impl.CourseServiceImpl;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CourseDialog extends JDialog {
    @Getter
    private boolean isSuccess;
    private final Course existing;
    private final CourseServiceImpl service = new CourseServiceImpl();

    // Information Fields
    private final JTextField tfName = new JTextField(25);
    private final JTextField tfDuration = new JTextField(15);
    private final JComboBox<Level> cbLevel = new JComboBox<>(Level.values());
    private final JTextField tfFee = new JTextField(25);
    private final JComboBox<CourseStatus> cbStatus = new JComboBox<>(CourseStatus.values());
    private final JTextField tfDescription = new JTextField(30);


    public CourseDialog(Frame parent, Course existing) {
        super(parent, existing == null ? "Thêm khoá học" : "Sửa khóa học", true);

        this.existing = existing;

        if (existing != null)
            prefill(existing);

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // --- Course info rows ---
        Object[][] infoRows = {
                { "Tên khóa học *", tfName },
                { "Số tiết trong tuần *", tfDuration },
                { "Cấp độ", cbLevel },
                { "Học phí", tfFee },
                { "Trạng thái", cbStatus },
                { "Mô tả", tfDescription },
        };

        int row = 0;
        for (Object[] r : infoRows) {
            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            p.add(new JLabel((String) r[0]), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add((Component) r[1], c);
            row++;
        }
        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnOk = UiUtil.primaryButton(existing != null ? "Cập nhật" : "Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel);
        p.add(btnOk);
        return p;
    }

    private void onOk() {
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            warn("Tên khóa học không được để trống.");
            return;
        }

        CourseDTO dto = new CourseDTO();
        dto.setCourseName(name);
        dto.setDescription(tfDescription.getText().trim());
        try {
            dto.setDuration(Integer.parseInt(tfDuration.getText().trim()));
        } catch (Exception e) {
            warn("Số tiết trong tuần không hợp lệ!");
            return;
        }
        try {
            dto.setFee(new BigDecimal(tfFee.getText().trim()));
        } catch (Exception e) {
            warn("Học phí khóa học không hợp lệ!");
            return;
        }
        dto.setLevel((Level) cbLevel.getSelectedItem());
        dto.setStatus((CourseStatus) cbStatus.getSelectedItem());

        new SwingWorker<Course, Void>() {
            @Override
            protected Course doInBackground() {
                if (existing != null) {
                    dto.setCourseID(existing.getCourseID());
                    return service.update(dto.getCourseID(), dto);
                }
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    if (existing != null)
                        MessageBox.info(CourseDialog.this, "Cập nhật khóa học thành công.");
                    else
                        MessageBox.info(CourseDialog.this, "Thêm khóa học thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(CourseDialog.this, msg);
                }
            }
        }.execute();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Course c) {
        tfName.setText(c.getCourseName());
        if (c.getDescription() != null)
            tfDescription.setText(c.getDescription());
        if (c.getDuration() != null)
            tfDuration.setText(String.valueOf(c.getDuration()));
        if (c.getFee() != null)
            tfFee.setText(String.valueOf(c.getFee()));
        if (c.getLevel() != null)
            cbLevel.setSelectedItem(c.getLevel());
        if (c.getStatus() != null)
            cbStatus.setSelectedItem(c.getStatus());
    }
}
