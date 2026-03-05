package com.ui.table;

import com.model.operation.Room;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RoomTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "ID", "Tên", "Sức chứa (ghế)", "Vị trí", "Trạng thái",
    };

    private List<Room> data = new ArrayList<>();

    public void setData(List<Room> Rooms) {
        this.data = Rooms == null ? new ArrayList<>() : Rooms;
        fireTableDataChanged();
    }

    public Room getRow(int row) {
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
        Room r = data.get(row);
        DecimalFormat df = new DecimalFormat("#,##0.##");
        return switch (col) {
            case 0 -> r.getRoomID();
            case 1 -> r.getRoomName();
            case 2 -> r.getCapacity() != null ? r.getCapacity().toString() : "";
            case 3 -> r.getLocation() != null ? r.getLocation() : "";
            case 4 -> r.getStatus() != null ? r.getStatus().name() : "";
            default -> "";
        };
    }
}

