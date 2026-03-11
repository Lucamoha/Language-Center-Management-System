package com.ui.panel;

import com.exception.AppException;
import com.model.operation.Schedule;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.ScheduleServiceImpl;
import com.ui.dialog.ScheduleDialog;
import com.ui.dialog.TimetableCellRenderer;
import com.ui.table.ScheduleTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
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

//        JTabbedPane tabs = new JTabbedPane();
//        tabs.addTab("Danh sách", buildListTab());
//        tabs.addTab("Thời khóa biểu", buildTimetableTab());
        
        // Listener để tải dữ liệu timetable khi chuyển sang tab
//        tabs.addChangeListener(e -> {
//            if (tabs.getSelectedIndex() == 1) { // Timetable tab
//                refreshData();
//            }
//        });

        add(UiUtil.sectionTitle("Quản lý Lịch học"), BorderLayout.NORTH);
//        add(tabs, BorderLayout.CENTER);
        add(buildTimetableTab(), BorderLayout.CENTER);

        wireEvents();
        applyRoleVisibility();
        loadData(null);
    }

    private JPanel buildListTab() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setOpaque(false);

        panel.add(buildHeader(), BorderLayout.NORTH);
        panel.add(buildTable(), BorderLayout.CENTER);
        panel.add(buildToolbar(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildTimetableTab() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setOpaque(false);

        panel.add(buildTimetableNav(), BorderLayout.NORTH);
        panel.add(buildTable(), BorderLayout.CENTER);
        panel.add(buildToolbar(), BorderLayout.SOUTH);

        return panel;
    }

    // ---- builders ----

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

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

    private JPanel buildTimetableNav() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnPrev = new JButton("< Tuần trước");
        JButton btnToday = new JButton("Hôm nay");
        JButton btnNext = new JButton("Tuần sau >");

        // Hàm cập nhật dữ liệu và hiển thị
        Runnable refreshTimetable = () -> {
            LocalDate monday = model.getMondayOfSelectedWeek();
            LocalDate sunday = monday.plusDays(6);

            //Gọi Service lấy lịch trong khoảng 1 tuần này
            List<Schedule> data = service.findSchedulesByRange(monday, sunday);
            model.setData(data);
            // Áp dụng lại styles để đảm bảo định dạng nhất quán
            applyTableStyles();
        };

        btnPrev.addActionListener(e -> {
            model.setWeek(model.getMondayOfSelectedWeek().minusWeeks(1));
            refreshTimetable.run();
        });

        btnToday.addActionListener(e -> {
            // Lấy ngày thứ 2 của tuần chứa ngày hôm nay
            LocalDate today = LocalDate.now();
            model.setWeek(today);

            // Cập nhật lại dữ liệu từ database cho tuần này
            refreshData();
        });

        btnNext.addActionListener(e -> {
            model.setWeek(model.getMondayOfSelectedWeek().plusWeeks(1));
            refreshTimetable.run();
        });

        navPanel.add(btnPrev);
        navPanel.add(btnToday);
        navPanel.add(btnNext);

        return navPanel;
    }

    private JScrollPane buildTable() {
        UiUtil.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new TimetableCellRenderer());
        // Tăng chiều cao dòng để hiển thị được HTML 2 dòng
        table.setRowHeight(50);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return sp;
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.add(btnEdit);
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
        int col = table.getSelectedColumn();
        if (row < 0 || col < 0) {//
            MessageBox.warn(this, "Vui lòng chọn một ô lịch học trên bảng để sửa.");
            return;
        }

        Schedule selected = model.getScheduleAt(row, col);

        if (selected == null) {
            MessageBox.warn(this, "Ô bạn chọn không có lịch học.");
            return;
        }

        ScheduleDialog dlg = new ScheduleDialog(getParentFrame(), selected);
        dlg.setVisible(true);

        if (dlg.isSuccess()) {
            refreshData();
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

    private void refreshData() {
        LocalDate start = model.getMondayOfSelectedWeek();
        LocalDate end = start.plusDays(6);

        List<Schedule> weeklySchedules = service.findSchedulesByRange(start, end);
        model.setData(weeklySchedules);

        SwingUtilities.invokeLater(this::applyTableStyles);
    }

//    private void reapplyStyles() {
//        table.setDefaultRenderer(Object.class, new TimetableCellRenderer());
//        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
//        table.setRowHeight(50);
//        table.getColumnModel().getColumn(0).setPreferredWidth(120);
//    }

    private void loadData(String keyword) {
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        new SwingWorker<java.util.List<Schedule>, Void>() {
            @Override
            protected List<Schedule> doInBackground() {
                return (keyword == null || keyword.isBlank())
                        ? service.findSchedulesByRange(model.getMondayOfSelectedWeek(), model.getMondayOfSelectedWeek().plusDays(6))
                        : service.searchByClassID(keyword);
            }

            @Override
            protected void done() {
                try {
                    model.setData(get());
                    // Chờ UI cập nhật structure xong rồi mới ép Style
                    SwingUtilities.invokeLater(() -> applyTableStyles());
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

//    private void applyTableStyles() {
//        // 1. Ép Renderer cho toàn bộ các cột hiện có trong Model
//        TimetableCellRenderer renderer = new TimetableCellRenderer();
//        for (int i = 0; i < table.getColumnCount(); i++) {
//            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
//        }
//
//        // 2. Ép lại chiều cao dòng (vì structure changed sẽ reset về mặc định)
//        table.setRowHeight(60);
//
//        // 3. Chỉnh độ rộng cột Khung giờ
//        if (table.getColumnCount() > 0) {
//            table.getColumnModel().getColumn(0).setPreferredWidth(100);
//            table.getColumnModel().getColumn(0).setMaxWidth(120);
//        }
//    }

    private void applyTableStyles() {
        // Luôn set Renderer cho Header (để giữ màu tiêu đề)
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Ép Renderer cho từng cột (quan trọng nhất để giữ màu xanh)
        TimetableCellRenderer renderer = new TimetableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Chiều cao dòng đồng nhất
        table.setRowHeight(60);

        // Độ rộng cột khung giờ
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(100);
            table.getColumnModel().getColumn(0).setMinWidth(100);
            table.getColumnModel().getColumn(0).setMaxWidth(120);
        }
    }
}

