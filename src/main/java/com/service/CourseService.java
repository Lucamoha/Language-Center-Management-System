package com.service;

import com.dto.CourseDTO;
import com.model.academic.Course;
import java.util.List;

public interface CourseService {
    List<Course> findAll();

    List<Course> search(String keyword);

    Course findById(Long id);

    Course save(CourseDTO dto);

    Course update(Course course);

    void delete(Long id);
}
