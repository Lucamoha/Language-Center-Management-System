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
import java.util.stream.Collectors;

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
        if (col == 0) return periods[row].toString();

        List<Schedule> list = getSchedulesAt(row, col);
        if (list.isEmpty()) return "";

//        // Ghép tất cả lịch trong ô thành 1 chuỗi HTML
//        StringBuilder sb = new StringBuilder("<html><center>");
//        for (int i = 0; i < list.size(); i++) {
//            Schedule s = list.get(i);
//            sb.append("<b>").append(s.getAClass().getClassName()).append("</b><br>")
//                    .append("<font color='blue'>").append(s.getRoom().getRoomName()).append("</font>");
//            if (i < list.size() - 1) {
//                // Đường kẻ phân cách giữa các lịch trong cùng ô
//                sb.append("<br><font color='gray'>------------</font><br>");
//            }
//        }
//        sb.append("</center></html>");
//        return sb.toString();

        // Thêm style width: 100% để ép text xuống dòng
        StringBuilder sb = new StringBuilder("<html><body style='width: 100px; text-align: center;'>");
        for (int i = 0; i < list.size(); i++) {
            Schedule s = list.get(i);
            sb.append("<b style='color: black;'>").append(s.getAClass().getClassName()).append("</b><br>")
                    .append("<font color='blue'>").append(s.getRoom().getRoomName()).append("</font>");

            if (i < list.size() - 1) {
                sb.append("<br><hr style='border: 0.5px solid #ccc;'>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    // Lấy tất cả Schedule khớp với ô [row, col].
    // Một ô có thể có nhiều lịch trùng giờ, trùng ngày nhưng khác phòng.
    public List<Schedule> getSchedulesAt(int row, int col) {
        if (col == 0) return List.of(); // Cột khung giờ không có schedule

        Period p = periods[row];
        return data.stream()
                .filter(s -> s.getDate() != null
                        && s.getDate().getDayOfWeek().getValue() == col
                        && s.getStartTime() != null
                        && s.getStartTime().equals(p.getStartTime()))
                .collect(Collectors.toList());
    }

     //Trả về Schedule đầu tiên tại ô (dùng cho trường hợp chỉ có 1 lịch).
     // Nếu ô có nhiều lịch, dùng getSchedulesAt() thay thế.
    public Schedule getScheduleAt(int row, int col) {
        List<Schedule> list = getSchedulesAt(row, col);
        return list.isEmpty() ? null : list.get(0);
    }
}


