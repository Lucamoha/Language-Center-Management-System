package com.ui.panel;

import com.exception.AppException;
import com.model.academic.Class;
import com.model.academic.ClassStatus;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.ClassServiceImpl;
import com.ui.table.ClassTableModel;
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

    //private final EnrollmentServiceImpl service = new EnrollmentServiceImpl();
    private final ClassServiceImpl service = new ClassServiceImpl();
    private final EnrollmentTableModel enrollmentTableModel = new EnrollmentTableModel();
    private final ClassTableModel classTableModel = new ClassTableModel();
    private final JTable enrollmentTable = new JTable(enrollmentTableModel);
    private final JTable classTable = new JTable(classTableModel);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo mã học viên...");
    private final JButton btnAdd = UiUtil.primaryButton("Dăng ký");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
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
        searchBar.add(new JLabel("Tìm kiếm:"));
        searchBar.add(tfSearch);
        JButton btnSearch = UiUtil.primaryButton("Tìm");
        btnSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        searchBar.add(btnSearch);
        p.add(searchBar, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildTable() {
        UiUtil.styleTable(classTable);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(classTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return sp;
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);
        p.add(btnRefresh);
        return p;
    }

    // ---- role visibility ----
    private void applyRoleVisibility() {
        CurrentUser u = SecurityContext.get();
        boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
        btnAdd.setVisible(canWrite);
        btnEdit.setVisible(canWrite);
        btnDelete.setVisible(canWrite);
    }

    // ---- events ----
    private void wireEvents() {
        btnAdd.addActionListener(e -> onAdd());
        //btnEdit.addActionListener(e -> onEdit());
        //btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData(null));
        tfSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
    }

    private void onAdd() {
        int row = classTable.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một lớp để đăng ký.");
            return;
        }

//        EnrollmentDialog dlg = new EnrollmentDialog(getParentFrame(), null);
//        dlg.setVisible(true);
//
//        if (dlg.isSuccess()) {
//            loadData(null);
//        }
    }

//    private void onEdit() {
//        int row = classTable.getSelectedRow();
//        if (row < 0) {
//            MessageBox.warn(this, "Vui lòng chọn một lớp đăng ký để sửa.");
//            return;
//        }
//        Class selected = classTable.getRow(row);
//
//        EnrollmentDialog dlg = new EnrollmentDialog(getParentFrame(), selected);
//        dlg.setVisible(true);
//
//        if (dlg.isSuccess()) {
//            loadData(null);
//        }
//    }

//    private void onDelete() {
//        int row = classTable.getSelectedRow();
//        if (row < 0) {
//            MessageBox.warn(this, "Vui lòng chọn một lịch sử đăng ký để xóa.");
//            return;
//        }
//        Enrollment selected = classTable.getRow(row);
//
//        if (!MessageBox.confirm(this, "Bạn có chắc muốn xóa lịch sử đăng ký: " + selected.getEnrollmentID() + "?"))
//            return;
//
//        new SwingWorker<Void, Void>() {
//            @Override
//            protected Void doInBackground() {
//                service.delete(selected.getEnrollmentID());
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    get();
//                    MessageBox.info(EnrollmentsPanel.this, "Đã xóa lịch sử đăng ký lớp vừa chọn.");
//                    loadData(null);
//                } catch (Exception ex) {
//                    handleException(ex);
//                }
//            }
//        }.execute();
//    }

    private void loadData(String keyword) {
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        new SwingWorker<java.util.List<com.model.academic.Class>, Void>() {
            @Override
            protected List<Class> doInBackground() {
//                    return (keyword == null || keyword.isBlank())
//                            ? service.findAll()
//                            : service.findByStudent(Long.parseLong(keyword));
                return service.findAll().stream().
                        filter(c -> c.getStatus() == ClassStatus.ACTIVE).toList();
            }

            @Override
            protected void done() {
                try {
                    classTableModel.setData(get());
                } catch (Exception ex) {
                    handleException(ex);
                } finally {
                    CurrentUser u = SecurityContext.get();
                    boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
                    btnAdd.setEnabled(canWrite);
                    btnEdit.setEnabled(canWrite);
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

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}

