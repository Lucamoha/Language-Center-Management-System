package com.ui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.Map;

public class ComparisonBarChart {

    /**
     * Tạo một ChartPanel so sánh 2 đại lượng trên các hạng mục khác nhau.
     *
     * @param title         Tiêu đề biểu đồ
     * @param categoryLabel Nhãn trục X (ví dụ: Khóa học)
     * @param valueLabel    Nhãn trục Y (ví dụ: Điểm số)
     * @param personalData  Map chứa dữ liệu cá nhân {Tên hạng mục : Giá trị}
     * @param averageData   Map chứa dữ liệu trung bình {Tên hạng mục : Giá trị}
     * @return ChartPanel để add vào giao diện Swing
     */
    public static ChartPanel createComparisonBarChart(
            String title,
            String categoryLabel,
            String valueLabel,
            Map<String, Double> personalData,
            Map<String, Double> averageData) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Đổ dữ liệu vào Dataset
        // JFreeChart sẽ nhóm theo 'Key' (Tên khóa học)
        personalData.forEach((course, score) -> {
            dataset.addValue(score, "Điểm cá nhân", course);
            dataset.addValue(averageData.getOrDefault(course, 0.0), "Trung bình lớp", course);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                categoryLabel,
                valueLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true, // Hiển thị chú thích (Legend) để phân biệt màu cột
                true,
                false
        );

        // --- Tùy chỉnh giao diện (Styling) ---
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        // Tùy chỉnh màu sắc cột
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1);    // Khoảng cách giữa cột Cá nhân và Trung bình (0.1 = 10% độ rộng)
        renderer.setMaximumBarWidth(0.15); // Giới hạn độ rộng tối đa của cột để tránh bị quá to khi ít dữ liệu
        renderer.setShadowVisible(false);

        // 3. Tùy chọn: Hiển thị giá trị số ngay trên đầu mỗi cột
        renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 10));

        // Giới hạn trục Y
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 11.5);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.2);

        return new ChartPanel(chart);
    }
}
