package com.ui.table;

import com.model.operation.Period;
import com.model.operation.Schedule;
import lombok.Getter;

import javax.swing.table.AbstractTableModel;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ScheduleTableModel extends AbstractTableModel {
    private final Period[] periods = Period.values();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @Getter
    private LocalDate mondayOfSelectedWeek;

    public ScheduleTableModel() {
        // Mặc định lấy ngày thứ Hai của tuần hiện tại
        this.mondayOfSelectedWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public void setWeek(LocalDate dateInWeek) {
        this.mondayOfSelectedWeek = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        fireTableStructureChanged(); // Cập nhật lại tiêu đề cột
    }


    private static final String[] COLUMNS = {
            "Khung giờ", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"
    };

    private List<Schedule> data = new ArrayList<>();

    public void setData(List<Schedule> Schedules) {
        this.data = Schedules == null ? new ArrayList<>() : Schedules;
        fireTableDataChanged();
    }

    public Schedule getRow(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return periods.length;
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) return "Khung giờ";
        // Tính ngày cho từng cột thứ
        LocalDate dateOfCol = mondayOfSelectedWeek.plusDays(col - 1);
        String[] days = {"", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};
        return "<html><center>" + days[col] + "<br>" +
                "<font size='3' color='white'>" + dateOfCol.format(dateFormatter) + "</font>" +
                "</center></html>";
    }

    @Override
    public Object getValueAt(int row, int col) {
        Period p = periods[row];
        if (col == 0) return p.toString(); // Lấy format hh:mm - hh:mm từ Enum

        for (Schedule s : data) {
            // 1. Kiểm tra Thứ: col 1 = Monday (1), ..., col 7 = Sunday (7)
            if (s.getDate() != null && s.getDate().getDayOfWeek().getValue() == col) {

                // 2. Kiểm tra giờ: Khớp chính xác startTime từ Enum
                if (s.getStartTime() != null && s.getStartTime().equals(p.getStartTime())) {
                    return "<html><center><b>" + s.getAClass().getClassName() + "</b><br>"
                            + "<font color='blue'>" + s.getRoom().getRoomName() + "</font></center></html>";
                }
            }
        }
        return "";
    }

    public Schedule getScheduleAt(int row, int col) {
        if (col == 0) return null; // Cột khung giờ không có schedule

        Period p = periods[row];
        for (Schedule s : data) {
            // Khớp Thứ và Khớp Giờ bắt đầu
            if (s.getDate() != null && s.getDate().getDayOfWeek().getValue() == col) {
                if (s.getStartTime() != null && s.getStartTime().equals(p.getStartTime())) {
                    return s;
                }
            }
        }
        return null;
    }
}


