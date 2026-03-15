package com.ui.dialog;

import com.dto.EnrollmentDTO;
import com.model.academic.Class;
import com.model.academic.Enrollment;
import com.service.impl.EnrollmentServiceImpl;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class EnrollmentDialog extends JDialog {
    @Getter
    private boolean isSuccess;
    private final EnrollmentServiceImpl service = new EnrollmentServiceImpl();

    // Information Fields
    private final JTextField tfStudentID = new JTextField(30);
    private final JTextField tfClassID = new JTextField(30);
    private final JTextField tfClassName = new JTextField(30);
    private final JTextField tfMaxStudent = new JTextField(30);
    private final JTextField tfEnrolledStudents = new JTextField(30);

    public EnrollmentDialog(Frame parent, Class existing) {
        super(parent,"Đăng ký lớp học", true);

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        prefill(existing);
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

        // --- Enrollment info rows ---
        Object[][] infoRows = {
                { "Mã lớp học *", tfClassID },
                { "Tên lớp học *", tfClassName },
                { "Số học viên tối đa", tfMaxStudent },
                { "Số học viên hiện tại *", tfEnrolledStudents },
                { "Mã học viên *", tfStudentID },
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
        JButton btnOk = UiUtil.primaryButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel);
        p.add(btnOk);
        return p;
    }

    private void onOk() {
        EnrollmentDTO dto = new EnrollmentDTO();

        try {
            dto.setStudentID(Long.parseLong(tfStudentID.getText().trim()));
        } catch (Exception e) {
            warn("Mã học viên không hợp lệ!");
            return;
        }

        dto.setClassID(Long.parseLong(tfClassID.getText()));

        new SwingWorker<Enrollment, Void>() {
            @Override
            protected Enrollment doInBackground() {
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    MessageBox.info(EnrollmentDialog.this, "Đăng ký lớp học thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(EnrollmentDialog.this, msg);
                }
            }
        }.execute();
    }

    private void prefill(Class existing) {
        tfClassID.setText(existing.getClassID().toString());
        tfClassName.setText(existing.getClassName());
        tfMaxStudent.setText(existing.getMaxStudent().toString());
        tfEnrolledStudents.setText(String.valueOf(existing.getEnrollments().size()));

        tfClassID.setEditable(false);
        tfClassName.setEditable(false);
        tfMaxStudent.setEditable(false);
        tfEnrolledStudents.setEditable(false);
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }
}