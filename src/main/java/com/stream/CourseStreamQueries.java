package com.stream;

import com.model.academic.Course;

import java.math.BigDecimal;
import java.util.List;

public class CourseStreamQueries {
    public List<Course> filterByFeeRange(List<Course> courses, BigDecimal min, BigDecimal max) {
        BigDecimal low = min == null ? BigDecimal.ZERO : min;
        BigDecimal high = max == null ? new BigDecimal("999999999") : max;
        return courses.stream().filter(c -> c.getFee() != null
                && c.getFee().compareTo(low) >= 0
                && c.getFee().compareTo(high) <= 0).toList();
    }
}
