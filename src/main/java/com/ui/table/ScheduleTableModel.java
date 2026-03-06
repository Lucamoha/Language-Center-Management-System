package com.ui.table;

import com.model.operation.Schedule;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ScheduleTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "ID", "ID lớp học", "ID phòng học", "Ngày học", "Giờ bắt đầu", "Giờ kết thúc"
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
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Schedule s = data.get(row);
        return switch (col) {
            case 0 -> s.getScheduleID();
            case 1 -> s.getAClass() != null ? s.getAClass().getClassID() : "";
            case 2 -> s.getRoom() != null ? s.getRoom().getRoomID() : "";
            case 3 -> s.getDate() != null ? s.getDate() : "";
            case 4 -> s.getStartTime() != null ? s.getStartTime() : "";
            case 5 -> s.getEndTime() != null ? s.getEndTime() : "";
            default -> "";
        };
    }
}


