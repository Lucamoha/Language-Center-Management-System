package com.ui.dialog;

import com.dto.RoomDTO;
import com.model.operation.Room;
import com.model.operation.RoomStatus;
import com.service.impl.RoomServiceImpl;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class RoomDialog extends JDialog {
    private Room existing = null;
    @Getter
    private boolean isSuccess;
    private final RoomServiceImpl service = new RoomServiceImpl();

    // Information Fields
    private final JTextField tfName = new JTextField(25);
    private final JTextField tfCapacity = new JTextField(15);
    private final JTextField tfLocation = new JTextField(25);
    private final JComboBox<RoomStatus> cbStatus = new JComboBox<>(RoomStatus.values());

    public RoomDialog(Frame parent, Room existing) {
        super(parent, existing == null ? "Thêm phòng học" : "Sửa phòng học", true);

        if (existing != null)
            prefill(existing);
        this.existing = existing;

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

        // --- Room info rows ---
        Object[][] infoRows = {
                { "Tên phòng học *", tfName },
                { "Sức chứa (ghế) *", tfCapacity },
                { "Vị trí", tfLocation },
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
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            warn("Tên phòng học không được để trống!");
            return;
        }

        RoomDTO dto = new RoomDTO();
        dto.setRoomName(name);
        try {
            dto.setCapacity(Integer.parseInt(tfCapacity.getText().trim()));
        } catch (Exception e) {
            warn("Sức chứa của phòng không hợp lệ!");
            return;
        }
        dto.setLocation(tfLocation.getText().trim());
        dto.setStatus((RoomStatus) cbStatus.getSelectedItem());

        new SwingWorker<Room, Void>() {
            @Override
            protected Room doInBackground() throws Exception {
                if(existing != null) {
                    dto.setRoomID(existing.getRoomID());
                    return service.update(dto.getRoomID(), dto);
                }
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    if(existing != null)
                        MessageBox.info(RoomDialog.this, "Cập nhật phòng học thành công.");
                    else
                        MessageBox.info(RoomDialog.this, "Thêm phòng học thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(RoomDialog.this, msg);
                }
            }
        }.execute();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Room r) {
        tfName.setText(r.getRoomName());
        if (r.getCapacity() != null)
            tfCapacity.setText(String.valueOf(r.getCapacity()));
        if (r.getLocation() != null)
            tfLocation.setText(r.getLocation());
        if (r.getStatus() != null)
            cbStatus.setSelectedItem(r.getStatus());
    }
}
