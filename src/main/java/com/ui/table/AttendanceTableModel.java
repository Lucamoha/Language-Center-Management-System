package com.ui.table;

import com.model.operation.Attendance;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AttendanceTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "ID", "Mã học viên", "Mã lớp học", "Trạng thái"
    };

    private List<Attendance> data = new ArrayList<>();

    public void setData(List<Attendance> Attendances) {
        this.data = Attendances == null ? new ArrayList<>() : Attendances;
        fireTableDataChanged();
    }

    public Attendance getRow(int row) {
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
        Attendance a = data.get(row);
        return switch (col) {
            case 0 -> a.getAttendanceID();
            case 1 -> a.getStudent() != null ? a.getStudent().getStudentID() : "";
            case 2 -> a.getAClass() != null ? a.getAClass().getClassID() : "";
            case 3 -> a.getStatus() != null ? a.getStatus() : "";
            default -> "";
        };
    }
}


