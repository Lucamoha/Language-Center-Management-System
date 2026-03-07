package com.ui.dialog;

import com.dto.AttendanceDTO;
import com.model.operation.Attendance;
import com.model.operation.AttendanceStatus;
import com.service.impl.AttendanceServiceImpl;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class AttendanceDialog extends JDialog {
    private Attendance existing;
    @Getter
    private boolean isSuccess;
    private final AttendanceServiceImpl service = new AttendanceServiceImpl();

    // Information Fields
    private final JTextField tfStudentID = new JTextField(30);
    private final JTextField tfClassID = new JTextField(30);
    private final JComboBox<AttendanceStatus> cbStatus = new JComboBox<>(AttendanceStatus.values());


    public AttendanceDialog(Frame parent, Attendance existing) {
        super(parent, existing == null ? "Thêm điểm  danh" : "Sửa điểm  danh", true);
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
                { "Trạng thái", cbStatus },
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
        String studentIDStr = tfStudentID.getText().trim();
        if (studentIDStr.isEmpty()) {
            warn("Mã sinh viên không được để trống!");
            return;
        }

        String classIDStr = tfClassID.getText().trim();
        if (classIDStr.isEmpty()) {
            warn("Mã lớp học không được để trống!");
            return;
        }

        AttendanceDTO dto = new AttendanceDTO();
        try {
            dto.setStudentID(Long.parseLong(studentIDStr));
        } catch (Exception e) {
            warn("Mã học viên không hợp lệ!");
            return;
        }

        try {
            dto.setClassID(Long.parseLong(classIDStr));
        } catch (Exception e) {
            warn("Mã lớp học không hợp lệ!");
            return;
        }

        dto.setStatus((AttendanceStatus) cbStatus.getSelectedItem());

        new SwingWorker<Attendance, Void>() {
            @Override
            protected Attendance doInBackground() throws Exception {
                if(existing != null) {
                    dto.setAttendanceID(existing.getAttendanceID());
                    return service.update(dto);
                }
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    if(existing != null)
                        MessageBox.info(AttendanceDialog.this, "Cập nhật điểm danh thành công.");
                    else
                        MessageBox.info(AttendanceDialog.this, "Thêm điểm danh thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(AttendanceDialog.this, msg);
                }
            }
        }.execute();
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Attendance a) {
        if (a.getStudent() != null)
            tfStudentID.setText(String.valueOf(a.getStudent().getStudentID()));
        if (a.getAClass() != null)
            tfClassID.setText(String.valueOf(a.getAClass().getClassID()));
        if (a.getStatus() != null)
            cbStatus.setSelectedItem(a.getStatus());
    }
}

