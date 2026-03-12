package com.ui.table;

import com.model.academic.Course;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CourseTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "ID", "Tên", "Tổng số buổi", "Học phí", "Cấp độ", "Trạng thái", "Mô tả"
    };

    private List<Course> data = new ArrayList<>();

    public void setData(List<Course> courses) {
        this.data = courses == null ? new ArrayList<>() : courses;
        fireTableDataChanged();
    }

    public Course getRow(int row) {
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
        Course c = data.get(row);
        DecimalFormat df = new DecimalFormat("#,##0.##");
        return switch (col) {
            case 0 -> c.getCourseID();
            case 1 -> c.getCourseName();
            case 2 -> c.getDuration() != null ? c.getDuration().toString() : "";
            case 3 -> c.getFee() != null ? df.format(c.getFee()) : "";
            case 4 -> c.getLevel() != null ? c.getLevel() : "";
            case 5 -> c.getStatus() != null ? c.getStatus().name() : "";
            case 6 -> c.getDescription() != null ? c.getDescription() : "";
            default -> "";
        };
    }
}
