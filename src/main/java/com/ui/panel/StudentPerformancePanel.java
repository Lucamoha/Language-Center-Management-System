package com.ui.panel;

import com.model.academic.Result;
import com.security.SecurityContext;
import com.stream.ResultStreamQueries;
import com.ui.chart.ComparisonBarChart;
import com.ui.util.UiUtil;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StudentPerformancePanel extends JPanel {

    private final ResultStreamQueries resultStreamQueries = new ResultStreamQueries();
    private final JPanel chartContainer = new JPanel(new BorderLayout());
    private final JTextArea txtEvaluation = new JTextArea();

    public StudentPerformancePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(UiUtil.sectionTitle("Đánh Giá Hiệu suất Học Tập Cá Nhân"), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridLayout(2, 1, 0, 20));
        mainContent.setOpaque(false);// Không tự vẽ màu nền của chính nó. Thay vào đó, nó sẽ để lộ màu nền của Component cha
        mainContent.add(chartContainer);
        mainContent.add(buildEvaluationBox());
        add(mainContent, BorderLayout.CENTER);
        refreshChart();
    }

    private JScrollPane buildEvaluationBox() {
        txtEvaluation.setEditable(false);
        txtEvaluation.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtEvaluation.setLineWrap(true);
        txtEvaluation.setWrapStyleWord(true);// Set xuống dòng theo ranh giới từ
        // (false (Mặc định): Một từ dài có thể bị cắt đôi (Ví dụ: Chữ "Communication" có thể bị tách thành "Communi" ở dòng trên và "cation" ở dòng dưới)
        txtEvaluation.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(txtEvaluation);
        sp.setBorder(BorderFactory.createTitledBorder("Nhận xét từ hệ thống"));
        return sp;
    }

    public void refreshChart() {
        Long studentId = SecurityContext.get().relatedId();
        List<Result> allResults = resultStreamQueries.findAllResultsInStudentClasses(studentId);

        if (allResults.isEmpty()) {
            chartContainer.add(new JLabel("Chưa có dữ liệu điểm số để đánh giá.", JLabel.CENTER));
            return;
        }

        // Lấy điểm cao nhất của cá nhân trong mỗi khóa (tránh trường hợp học lại)
        Map<String, Double> myData = resultStreamQueries.getMaxScoreOfStudentInCourses(studentId);

        // Tính trung bình điểm của tất cả mọi người theo từng khóa học
        Map<String, Double> avgData = resultStreamQueries.findAllAverageResultsOfAllStudentsInSpecificStudentClass(studentId);

        // Gọi vẽ biểu đồ
        ChartPanel chartPanel = ComparisonBarChart.createComparisonBarChart(
                "PHÂN TÍCH NĂNG LỰC HỌC TẬP",
                "Khóa học",
                "Điểm số (Hệ 10)",
                myData,
                avgData
        );

        chartContainer.removeAll();
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        // TẠO NHẬN XÉT TỰ ĐỘNG
        txtEvaluation.setText(resultStreamQueries.generateSmartResultsEvaluation(myData, avgData));

        this.revalidate();
        this.repaint();
    }
}