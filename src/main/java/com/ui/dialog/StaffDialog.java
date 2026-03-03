package com.ui.dialog;

import com.dto.StaffDTO;
import com.model.user.Staff;
import com.model.user.StaffRole;
import com.model.user.UserStatus;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class StaffDialog extends JDialog {

    private final boolean isEdit;

    @Getter
    private StaffDTO result;

    private final JTextField tfName = new JTextField(25);
    private final JComboBox<StaffRole> cbRole = new JComboBox<>(StaffRole.values());
    private final JTextField tfPhone = new JTextField(15);
    private final JTextField tfEmail = new JTextField(25);
    private final JComboBox<UserStatus> cbStatus = new JComboBox<>(UserStatus.values());

    // Account credentials (only on create)
    private final JTextField tfUsername = new JTextField(20);
    private final JPasswordField pfPassword = new JPasswordField(20);

    public StaffDialog(Frame parent, Staff existing) {
        super(parent, existing == null ? "Thêm nhân viên" : "Sửa nhân viên", true);
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
                { "Vai trò", cbRole },
                { "Điện thoại", tfPhone },
                { "Email", tfEmail },
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

        StaffDTO dto = new StaffDTO();
        dto.setFullName(name);
        dto.setRole((StaffRole) cbRole.getSelectedItem());
        dto.setPhone(tfPhone.getText().trim());
        dto.setEmail(tfEmail.getText().trim());
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

    private void prefill(Staff s) {
        tfName.setText(s.getFullName());
        if (s.getRole() != null)
            cbRole.setSelectedItem(s.getRole());
        if (s.getPhone() != null)
            tfPhone.setText(s.getPhone());
        if (s.getEmail() != null)
            tfEmail.setText(s.getEmail());
        if (s.getStatus() != null)
            cbStatus.setSelectedItem(s.getStatus());
    }
}
