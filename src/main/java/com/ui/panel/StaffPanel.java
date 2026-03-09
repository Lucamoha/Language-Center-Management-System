package com.ui.panel;

import com.dto.StaffDTO;
import com.exception.AppException;
import com.model.user.*;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.StaffServiceImpl;
import com.stream.StaffStreamQueries;
import com.ui.dialog.StaffDialog;
import com.ui.table.StaffTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StaffPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(StaffPanel.class);

    private final StaffServiceImpl service = new StaffServiceImpl();
    private final StaffTableModel model = new StaffTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo tên...");
    private final JButton btnAdd = UiUtil.primaryButton("Thêm");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnDelete = UiUtil.dangerButton("Xóa");
    private final JButton btnRefresh = new JButton("Làm mới");
    private JComboBox<UserStatus> cbStatus = buildStatusComboBox();
    private JComboBox<StaffRole> cbStaffRoles = buildStaffRolesComboBox();
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

    private static JComboBox<StaffRole> buildStaffRolesComboBox() {
        JComboBox<StaffRole> cbStaffRoles = new JComboBox<>();

        cbStaffRoles.addItem(null); // "Tất cả"

        for (StaffRole s : StaffRole.values())
            cbStaffRoles.addItem(s);

        cbStaffRoles.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Tất cả" : value.toString());
                return this;
            }
        });

        return cbStaffRoles;
    }

    public StaffPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);

        wireEvents();
        loadData(null);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.add(UiUtil.sectionTitle("Quản lý Nhân viên"), BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("Vai trò:"));
        searchBar.add(cbStaffRoles);
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

    private void wireEvents() {
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> {
            isResetting = true;
            try {
                tfSearch.setText("");
                cbStatus.setSelectedItem(null);
                cbStaffRoles.setSelectedItem(null);
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

        cbStaffRoles.addActionListener(e -> {
            if (!isResetting) {
                loadData(tfSearch.getText().trim());
            }
        });
    }

    private void onAdd() {
        StaffDialog dlg = new StaffDialog(getParentFrame(), null);
        dlg.setVisible(true);
        StaffDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Staff, Void>() {
            @Override
            protected Staff doInBackground() {
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(StaffPanel.this, "Thêm nhân viên thành công.");
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
            MessageBox.warn(this, "Vui lòng chọn một nhân viên để sửa.");
            return;
        }
        Staff selected = model.getRow(row);

        StaffDialog dlg = new StaffDialog(getParentFrame(), selected);
        dlg.setVisible(true);
        StaffDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Staff, Void>() {
            @Override
            protected Staff doInBackground() {
                return service.update(selected.getStaffID(), dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(StaffPanel.this, "Cập nhật nhân viên thành công.");
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
            MessageBox.warn(this, "Vui lòng chọn một nhân viên để xóa.");
            return;
        }
        Staff selected = model.getRow(row);
        if (!MessageBox.confirm(this, "Bạn có chắc muốn xóa nhân viên: " + selected.getFullName() + "?"))
            return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.delete(selected.getStaffID());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(StaffPanel.this, "Đã xóa nhân viên.");
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
        StaffRole selectedStaffRole = (StaffRole) cbStaffRoles.getSelectedItem();

        new SwingWorker<List<Staff>, Void>() {
            @Override
            protected List<Staff> doInBackground() {
                List<Staff> all = service.findAll();

                // filter by status
                if (selectedStatus != null)
                    all = StaffStreamQueries.filterByStatus(all, selectedStatus);

                // filter by specialty
                if (selectedStaffRole != null)
                    all = StaffStreamQueries.filterByRole(all, selectedStaffRole);

                // filter by keyword
                if (keyword != null && !keyword.isBlank())
                    all = StaffStreamQueries.search(all, keyword);

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
                    boolean isAdmin = u != null && u.isAdmin();
                    btnAdd.setEnabled(isAdmin);
                    btnEdit.setEnabled(isAdmin);
                    btnDelete.setEnabled(isAdmin);
                }
            }
        }.execute();
    }

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof AppException ae)
            MessageBox.warn(this, ae.getUserMessage());
        else {
            log.error("Error in StaffPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}
