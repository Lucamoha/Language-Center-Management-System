package com.ui.table;

import com.model.user.Teacher;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TeacherTableModel extends AbstractTableModel {

    private static final String[] COLS = { "ID", "Họ và tên", "Điện thoại", "Email", "Chuyên môn", "Trạng thái" };

    private List<Teacher> data = new ArrayList<>();

    public void setData(List<Teacher> list) {
        this.data = list == null ? new ArrayList<>() : list;
        fireTableDataChanged();
    }

    public Teacher getRow(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLS[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Teacher t = data.get(row);
        return switch (col) {
            case 0 -> t.getTeacherID();
            case 1 -> t.getFullName();
            case 2 -> t.getPhone();
            case 3 -> t.getEmail();
            case 4 -> t.getSpecialty();
            case 5 -> t.getStatus();
            default -> null;
        };
    }
}
