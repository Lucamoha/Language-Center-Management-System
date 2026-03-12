package com.ui.panel;

import com.exception.AppException;
import com.model.academic.Enrollment;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.EnrollmentServiceImpl;
import com.ui.table.EnrollmentTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EnrollmentResultsPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentResultsPanel.class);

    private final EnrollmentServiceImpl service = new EnrollmentServiceImpl();
    private final EnrollmentTableModel model = new EnrollmentTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfStudentSearch = UiUtil.searchField("Tìm theo mã học viên...");
    private final JButton btnDelete = UiUtil.dangerButton("Xóa");
    private final JButton btnRefresh = new JButton("Làm mới");

    public EnrollmentResultsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);

        wireEvents();
        applyRoleVisibility();
        loadData(null);
    }

    // ---- builders ----
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.add(UiUtil.sectionTitle("Quản lý đăng ký lớp học"), BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("Tìm kiếm theo mã học viên:"));
        searchBar.add(tfStudentSearch);
        JButton btnSearch = UiUtil.primaryButton("Tìm");
        btnSearch.addActionListener(e -> loadData(tfStudentSearch.getText().trim()));
        searchBar.add(btnSearch);
        p.add(searchBar, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildTable() {
        UiUtil.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return sp;
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.add(btnDelete);
        p.add(btnRefresh);
        return p;
    }

    // ---- role visibility ----
    private void applyRoleVisibility() {
        CurrentUser u = SecurityContext.get();
        boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
        btnDelete.setVisible(canWrite);
    }

    // ---- events ----
    private void wireEvents() {
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData(null));
        tfStudentSearch.addActionListener(e -> loadData(tfStudentSearch.getText().trim()));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một lịch sử đăng ký để xóa.");
            return;
        }
        Enrollment selected = model.getRow(row);

        if (!MessageBox.confirm(this, "Bạn có chắc muốn xóa lịch sử đăng ký có mã đăng ký: " + selected.getEnrollmentID() + "?"))
            return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.delete(selected.getEnrollmentID());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(EnrollmentResultsPanel.this, "Đã xóa lịch sử đăng ký lớp vừa chọn.");
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void loadData(String keyword) {
        btnDelete.setEnabled(false);

        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() {
                return (keyword == null || keyword.isBlank())
                            ? service.findAll()
                            : service.findByStudent(Long.parseLong(keyword));
            }

            @Override
            protected void done() {
                try {
                    model.setData(get());
                } catch (Exception ex) {
                    handleException(ex);
                } finally {
                    CurrentUser u = SecurityContext.get();
                    boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
                    btnDelete.setEnabled(canWrite);
                }
            }
        }.execute();
    }

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof com.exception.ValidationException || cause instanceof com.exception.BusinessException) {
            MessageBox.warn(this, ((AppException) cause).getUserMessage());
        } else {
            log.error("Error in EnrollmentPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }
}

