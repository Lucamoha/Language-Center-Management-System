package com.ui.panel;

import com.dto.TeacherDTO;
import com.exception.AppException;
import com.model.user.Teacher;
import com.security.SecurityContext;
import com.security.CurrentUser;
import com.service.impl.TeacherServiceImpl;
import com.ui.dialog.TeacherDialog;
import com.ui.table.TeacherTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeachersPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(TeachersPanel.class);

    private final TeacherServiceImpl service = new TeacherServiceImpl();
    private final TeacherTableModel model = new TeacherTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo tên...");
    private final JButton btnAdd = UiUtil.primaryButton("Thêm");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnDelete = UiUtil.dangerButton("Xóa");
    private final JButton btnRefresh = new JButton("Làm mới");

    public TeachersPanel() {
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

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.add(UiUtil.sectionTitle("Quản lý Giáo viên"), BorderLayout.WEST);

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

    private void applyRoleVisibility() {
        CurrentUser u = SecurityContext.get();
        boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
        btnAdd.setVisible(canWrite);
        btnEdit.setVisible(canWrite);
        boolean canDelete = u != null && u.isAdmin();
        btnDelete.setVisible(canDelete);
    }

    private void wireEvents() {
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData(null));
        tfSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
    }

    private void onAdd() {
        TeacherDialog dlg = new TeacherDialog(getParentFrame(), null);
        dlg.setVisible(true);
        TeacherDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Teacher, Void>() {
            @Override
            protected Teacher doInBackground() {
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(TeachersPanel.this, "Thêm giáo viên thành công.");
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một giáo viên để sửa.");
            return;
        }
        Teacher selected = model.getRow(row);

        TeacherDialog dlg = new TeacherDialog(getParentFrame(), selected);
        dlg.setVisible(true);
        TeacherDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Teacher, Void>() {
            @Override
            protected Teacher doInBackground() {
                return service.update(selected.getTeacherID(), dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(TeachersPanel.this, "Cập nhật giáo viên thành công.");
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một giáo viên để xóa.");
            return;
        }
        Teacher selected = model.getRow(row);
        if (!MessageBox.confirm(this, "Bạn có chắc muốn xóa giáo viên: " + selected.getFullName() + "?"))
            return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.delete(selected.getTeacherID());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(TeachersPanel.this, "Đã xóa giáo viên.");
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

        new SwingWorker<List<Teacher>, Void>() {
            @Override
            protected List<Teacher> doInBackground() {
                return (keyword == null || keyword.isBlank())
                        ? service.findAll()
                        : service.search(keyword);
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
                    boolean canDelete = u != null && u.isAdmin();
                    btnAdd.setEnabled(canWrite);
                    btnEdit.setEnabled(canWrite);
                    btnDelete.setEnabled(canDelete);
                }
            }
        }.execute();
    }

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof AppException ae)
            MessageBox.warn(this, ae.getUserMessage());
        else {
            log.error("Error in TeachersPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}
