package com.ui.dialog;

import com.dto.EnrollmentDTO;
import com.model.academic.Enrollment;
import com.service.impl.ClassServiceImpl;
import com.service.impl.EnrollmentServiceImpl;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class EnrollmentDialog extends JDialog {
    private Enrollment existing = null;
    @Getter
    private boolean isSuccess;
    private final EnrollmentServiceImpl service = new EnrollmentServiceImpl();
    private final ClassServiceImpl classService = new ClassServiceImpl();

    // Information Fields
    private final JTextField tfStudentID = new JTextField(30);
    private final JTextField tfClassID = new JTextField(30);

    public EnrollmentDialog(Frame parent, Enrollment existing) {
        super(parent, existing == null ? "Thêm đăng ký lớp học" : "Sửa đăng ký lớp học", true);
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
                { "Mã học viên *", tfStudentID },
                { "Mã lớp học *", tfClassID },
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
        EnrollmentDTO dto = new EnrollmentDTO();

        try {
            dto.setStudentID(Long.parseLong(tfStudentID.getText().trim()));
        } catch (Exception e) {
            warn("Mã học viên không hợp lệ!");
            return;
        }

        try {
            dto.setClassID(Long.parseLong(tfClassID.getText().trim()));
        } catch (Exception e) {
            warn("Mã lớp học không hợp lệ!");
            return;
        }

        new SwingWorker<Enrollment, Void>() {
            @Override
            protected Enrollment doInBackground() throws Exception {
                if(existing != null) {
                    dto.setEnrollmentID(existing.getEnrollmentID());
                    return service.update(dto.getEnrollmentID(), dto);
                }
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    if(existing != null)
                        MessageBox.info(EnrollmentDialog.this, "Cập nhật đăng ký lớp học thành công.");
                    else
                        MessageBox.info(EnrollmentDialog.this, "Thêm đăng ký lớp học thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(EnrollmentDialog.this, msg);
                }
            }
        }.execute();
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Enrollment e) {
        if (e.getAclass() != null)
            tfClassID.setText(String.valueOf(e.getAclass().getClassID()));
        if (e.getStudent() != null)
            tfStudentID.setText(String.valueOf(e.getStudent().getStudentID()));
    }
}

