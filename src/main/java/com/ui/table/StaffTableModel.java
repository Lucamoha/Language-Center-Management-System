package com.ui.table;

import com.model.user.Staff;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class StaffTableModel extends AbstractTableModel {

    private static final String[] COLS = { "ID", "Họ và tên", "Vai trò", "Điện thoại", "Email", "Trạng thái" };

    private List<Staff> data = new ArrayList<>();

    public void setData(List<Staff> list) {
        this.data = list == null ? new ArrayList<>() : list;
        fireTableDataChanged();
    }

    public Staff getRow(int row) {
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
        Staff s = data.get(row);
        return switch (col) {
            case 0 -> s.getStaffID();
            case 1 -> s.getFullName();
            case 2 -> s.getRole();
            case 3 -> s.getPhone();
            case 4 -> s.getEmail();
            case 5 -> s.getStatus();
            default -> null;
        };
    }
}
