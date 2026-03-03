package com.ui.dialog;

import com.dto.TeacherDTO;
import com.model.user.Specialty;
import com.model.user.Teacher;
import com.model.user.UserStatus;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class TeacherDialog extends JDialog {

    private final boolean isEdit;

    @Getter
    private TeacherDTO result;

    private final JTextField tfName = new JTextField(25);
    private final JTextField tfPhone = new JTextField(15);
    private final JTextField tfEmail = new JTextField(25);
    private final JComboBox<Specialty> cbSpecialty = new JComboBox<>(Specialty.values());
    private final JComboBox<UserStatus> cbStatus = new JComboBox<>(UserStatus.values());

    // Account credentials (only on create)
    private final JTextField tfUsername = new JTextField(20);
    private final JPasswordField pfPassword = new JPasswordField(20);

    public TeacherDialog(Frame parent, Teacher existing) {
        super(parent, existing == null ? "Thêm giáo viên" : "Sửa giáo viên", true);
        isEdit = existing != null;

        if (isEdit)
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

        Object[][] rows = {
                { "Họ và tên *", tfName },
                { "Điện thoại", tfPhone },
                { "Email", tfEmail },
                { "Chuyên môn", cbSpecialty },
                { "Trạng thái", cbStatus },
        };

        int row = 0;
        for (Object[] r : rows) {
            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            p.add(new JLabel((String) r[0]), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add((Component) r[1], c);
            row++;
        }

        if (!isEdit) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.weightx = 1;
            c.insets = new Insets(12, 5, 4, 5);
            JLabel sep = new JLabel("Tài khoản đăng nhập");
            sep.setFont(UiUtil.FONT_BOLD);
            sep.setForeground(UiUtil.COLOR_PRIMARY);
            p.add(sep, c);
            c.gridwidth = 1;
            c.insets = new Insets(4, 5, 4, 5);
            row++;

            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            p.add(new JLabel("Tên đăng nhập *"), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add(tfUsername, c);
            row++;

            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            p.add(new JLabel("Mật khẩu *"), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add(pfPassword, c);
        }
        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnOk = UiUtil.primaryButton(isEdit ? "Cập nhật" : "Lưu");
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
            warn("Họ tên không được để trống.");
            return;
        }

        if (!isEdit) {
            if (tfUsername.getText().trim().isEmpty()) {
                warn("Tên đăng nhập không được để trống.");
                return;
            }
            if (new String(pfPassword.getPassword()).trim().isEmpty()) {
                warn("Mật khẩu không được để trống.");
                return;
            }
        }

        TeacherDTO dto = new TeacherDTO();
        dto.setFullName(name);
        dto.setPhone(tfPhone.getText().trim());
        dto.setEmail(tfEmail.getText().trim());
        dto.setSpecialty((Specialty) cbSpecialty.getSelectedItem());
        dto.setStatus((UserStatus) cbStatus.getSelectedItem());

        if (!isEdit) {
            dto.setUsername(tfUsername.getText().trim());
            dto.setPassword(new String(pfPassword.getPassword()));
        }

        result = dto;
        dispose();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Teacher t) {
        tfName.setText(t.getFullName());
        if (t.getPhone() != null)
            tfPhone.setText(t.getPhone());
        if (t.getEmail() != null)
            tfEmail.setText(t.getEmail());
        if (t.getSpecialty() != null)
            cbSpecialty.setSelectedItem(t.getSpecialty());
        if (t.getStatus() != null)
            cbStatus.setSelectedItem(t.getStatus());
    }
}
