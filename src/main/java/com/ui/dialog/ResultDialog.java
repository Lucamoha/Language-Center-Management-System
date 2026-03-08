package com.ui.dialog;

import com.dto.ResultDTO;
import com.exception.ValidationException;
import com.model.academic.Result;
import com.service.impl.ResultServiceImpl;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class ResultDialog extends JDialog {
    @Getter
    private boolean isSuccess;
    private final ResultServiceImpl service = new ResultServiceImpl();

    private final Result existing;

    // Information Fields
    private final JTextField tfScore = new JTextField(30);
    private final JTextField tfComment = new JTextField(30);

    public ResultDialog(Frame parent, Result existing) {
        super(parent, "Sửa điểm", true);

        prefill(existing);

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);

        this.existing = existing;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // --- Course info rows ---
        Object[][] infoRows = {
                {"Điểm", tfScore},
                {"Nhận xét", tfComment},
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
        JButton btnOk = UiUtil.primaryButton("Cập nhật");
        JButton btnCancel = new JButton("Hủy");
        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel);
        p.add(btnOk);
        return p;
    }

    private void onOk() {
        ResultDTO dto = new ResultDTO();
        if (!tfScore.getText().trim().isBlank()) {
            Double score = null;
            try {
                score = Double.parseDouble(tfScore.getText().trim());
            } catch (Exception e) {
                MessageBox.warn(ResultDialog.this, "Điểm không hợp lệ!");
                return;
            }
            if (score < 0) {
                MessageBox.warn(ResultDialog.this, "Điểm phải lớn hơn 0!");
                return;
            }
            dto.setScore(score);
        } else {
            dto.setScore(null);
        }
        dto.setComment(tfComment.getText().trim());

        new SwingWorker<Result, Void>() {
            @Override
            protected Result doInBackground() throws Exception {
                dto.setResultID(existing.getResultID());
                return service.update(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    MessageBox.info(ResultDialog.this, "Cập nhật điểm thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(ResultDialog.this, msg);
                }
            }
        }.execute();
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Result r) {
        if (r.getScore() != null)
            tfScore.setText(r.getScore().toString());
        if (r.getComment() != null)
            tfComment.setText(r.getComment());
    }
}


