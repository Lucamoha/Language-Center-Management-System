package com.ui.dialog;

import com.dto.ScheduleDTO;
import com.model.operation.Schedule;
import com.service.impl.ScheduleServiceImpl;
import com.ui.util.JTextFieldPlaceholder;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ScheduleDialog extends JDialog {
    private final Schedule existing;
    @Getter
    private boolean isSuccess;
    private final ScheduleServiceImpl service = new ScheduleServiceImpl();

    // Information Fields
    private final JTextField tfClassID = new JTextField(30);
    private final JTextField tfRoomID = new JTextField(30);
    private final JTextFieldPlaceholder tfDate = new JTextFieldPlaceholder("dd/MM/yyyy");
    private final JTextFieldPlaceholder tfStartTime = new JTextFieldPlaceholder("HH:mm");
    private final JTextFieldPlaceholder tfEndTime = new JTextFieldPlaceholder("HH:mm");

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduleDialog(Frame parent, Schedule existing) {
        super(parent, existing == null ? "Thêm lịch học" : "Sửa lịch học", true);
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
                { "Mã lớp học *", tfClassID },
                { "Mã phòng học *", tfRoomID },
                { "Ngày học *", tfDate },
                { "Giờ bắt đầu *", tfStartTime },
                { "Giờ kết thúc *", tfEndTime },
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
        String classID = tfClassID.getText().trim();
        if (classID.isEmpty()) {
            warn("Mã lớp học không được để trống!");
            return;
        }

        String roomID = tfRoomID.getText().trim();
        if (roomID.isEmpty()) {
            warn("Mã phòng học không được để trống!");
            return;
        }

        String date = tfDate.getText().trim();
        if (date.isEmpty()) {
            warn("Ngày học không được để trống!");
            return;
        }

        String startTimeStr = tfStartTime.getText().trim();
        if (startTimeStr.isEmpty()) {
            warn("Giờ bắt đầu không được để trống!");
            return;
        }

        String endTimeStr = tfEndTime.getText().trim();
        if (endTimeStr.isEmpty()) {
            warn("Giờ kết thúc không được để trống!");
            return;
        }

        ScheduleDTO dto = new ScheduleDTO();

        try {
            dto.setClassID(Long.parseLong(classID));
        } catch (Exception e) {
            warn("Mã lớp học không hợp lệ!");
            return;
        }

        try {
            dto.setRoomID(Long.parseLong(roomID));
        } catch (Exception e) {
            warn("Mã phòng học không hợp lệ!");
            return;
        }

        try {
            dto.setDate(LocalDate.parse(date, dateFormatter));
        } catch (DateTimeParseException e) {
            warn("Ngày học không hợp lệ! (Vui lòng nhập theo định dạng (dd/MM/yyyy) và phải là ngày, tháng, năm hợp lệ!)");
            return;
        }

        LocalTime startTime;
        try {
            startTime = LocalTime.parse(startTimeStr, hourFormatter);
        } catch (DateTimeParseException e) {
            warn("Giờ bắt đầu không hợp lệ! (Vui lòng nhập theo định dạng (HH:mm) và phải là giờ, phút hợp lệ!)");
            return;
        }

        LocalTime endTime;
        try {
            endTime = LocalTime.parse(endTimeStr, hourFormatter);
        } catch (DateTimeParseException e) {
            warn("Giờ kết thúc không hợp lệ! (Vui lòng nhập theo định dạng (HH:mm) và phải là giờ, phút hợp lệ!)");
            return;
        }

        if (endTime.isBefore(startTime)){
            warn("Giờ kết thúc phải sau giờ bắt đầu!");
            return;
        }

        dto.setStartTime(startTime);
        dto.setEndTime(endTime);

        new SwingWorker<Schedule, Void>() {
            @Override
            protected Schedule doInBackground() throws Exception {
                if(existing != null) {
                    dto.setScheduleID(existing.getScheduleID());
                    return service.update(dto);
                }
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    if(existing != null)
                        MessageBox.info(ScheduleDialog.this, "Cập nhật lịch học thành công.");
                    else
                        MessageBox.info(ScheduleDialog.this, "Thêm lịch học thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(ScheduleDialog.this, msg);
                }
            }
        }.execute();
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Schedule s) {
        if (s.getAClass() != null)
            tfClassID.setText(String.valueOf(s.getAClass().getClassID()));
        if (s.getRoom() != null)
            tfRoomID.setText(String.valueOf(s.getRoom().getRoomID()));
        if (s.getDate() != null)
            tfDate.setText(s.getDate().format(dateFormatter));
        if (s.getStartTime() != null)
            tfStartTime.setText(s.getStartTime().format(hourFormatter));
        if (s.getEndTime() != null)
            tfEndTime.setText(s.getEndTime().format(hourFormatter));
    }
}
