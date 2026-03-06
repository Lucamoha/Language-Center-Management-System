package com.ui.panel;

import com.exception.AppException;
import com.model.operation.Schedule;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.ScheduleServiceImpl;
import com.ui.dialog.ScheduleDialog;
import com.ui.table.ScheduleTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class SchedulesPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(SchedulesPanel.class);

    private final ScheduleServiceImpl service = new ScheduleServiceImpl();
    private final ScheduleTableModel model = new ScheduleTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo mã lớp học...");
    private final JButton btnAdd = UiUtil.primaryButton("Thêm");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnDelete = UiUtil.dangerButton("Xóa");
    private final JButton btnRefresh = new JButton("Làm mới");

    public SchedulesPanel() {
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
        p.add(UiUtil.sectionTitle("Quản lý Lịch học"), BorderLayout.WEST);

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
        UiUtil.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
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
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData(null));
        tfSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
    }

    private void onAdd() {
        ScheduleDialog dlg = new ScheduleDialog(getParentFrame(), null);
        dlg.setVisible(true);

        if (dlg.isSuccess()) {
            loadData(null);
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một lịch học để sửa.");
            return;
        }
        Schedule selected = model.getRow(row);

        ScheduleDialog dlg = new ScheduleDialog(getParentFrame(), selected);
        dlg.setVisible(true);

        if (dlg.isSuccess()) {
            loadData(null);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một lịch học để xóa.");
            return;
        }
        Schedule selected = model.getRow(row);

        if (!MessageBox.confirm(this, "Bạn có chắc muốn xóa lịch học có mã: " + selected.getScheduleID() + "?"))
            return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.delete(selected.getScheduleID());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(SchedulesPanel.this, "Đã xóa lịch học.");
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void loadData(String keyword) {
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        new SwingWorker<java.util.List<Schedule>, Void>() {
            @Override
            protected List<Schedule> doInBackground() {
                return (keyword == null || keyword.isBlank())
                        ? service.findAll()
                        : service.searchByClassID(keyword);
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
            log.error("Error in SchedulePanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}

