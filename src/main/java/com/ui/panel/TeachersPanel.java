package com.ui.panel;

import com.dto.TeacherDTO;
import com.exception.AppException;
import com.model.user.Specialty;
import com.model.user.Teacher;
import com.model.user.UserStatus;
import com.security.SecurityContext;
import com.security.CurrentUser;
import com.service.impl.TeacherServiceImpl;
import com.stream.TeacherStreamQueries;
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
    private JComboBox<UserStatus> cbStatus = buildStatusComboBox();
    private JComboBox<Specialty> cbSpecialty = buildSpecialtyComboBox();
    private boolean isResetting = false;

    private static JComboBox<UserStatus> buildStatusComboBox() {
        JComboBox<UserStatus> cbStatus = new JComboBox<>();

        cbStatus.addItem(null); // "Tất cả"

        for (UserStatus r : UserStatus.values())
            cbStatus.addItem(r);

        cbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Tất cả" : value.toString());
                return this;
            }
        });

        return cbStatus;
    }

    private static JComboBox<Specialty> buildSpecialtyComboBox() {
        JComboBox<Specialty> cbSpecialty = new JComboBox<>();

        cbSpecialty.addItem(null); // "Tất cả"

        for (Specialty s : Specialty.values())
            cbSpecialty.addItem(s);

        cbSpecialty.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Tất cả" : value.toString());
                return this;
            }
        });

        return cbSpecialty;
    }

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
        searchBar.add(new JLabel("Chuyên môn:"));
        searchBar.add(cbSpecialty);
        searchBar.add(new JLabel("Trạng thái:"));
        searchBar.add(cbStatus);
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
        btnRefresh.addActionListener(e -> {
            isResetting = true;
            try {
                tfSearch.setText("");
                cbStatus.setSelectedItem(null);
                cbSpecialty.setSelectedItem(null);
            } finally {
                isResetting = false;
            }
            loadData(null);
        });

        tfSearch.addActionListener(e -> {
            if (!isResetting) {
                loadData(tfSearch.getText().trim());
            }
        });

        cbStatus.addActionListener(e -> {
            if (!isResetting) {
                loadData(tfSearch.getText().trim());
            }
        });

        cbSpecialty.addActionListener(e -> {
            if (!isResetting) {
                loadData(tfSearch.getText().trim());
            }
        });
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
        UserStatus selectedStatus = (UserStatus) cbStatus.getSelectedItem();
        Specialty selectedSpecialty = (Specialty) cbSpecialty.getSelectedItem();

        new SwingWorker<List<Teacher>, Void>() {
            @Override
            protected List<Teacher> doInBackground() {
                List<Teacher> all = service.findAll();

                // filter by status
                if (selectedStatus != null)
                    all = TeacherStreamQueries.filterByStatus(all, selectedStatus);

                // filter by specialty
                if (selectedSpecialty != null)
                    all = TeacherStreamQueries.filterBySpecialty(all, selectedSpecialty);

                // filter by keyword
                if (keyword != null && !keyword.isBlank())
                    all = TeacherStreamQueries.search(all, keyword);

                return all;
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
