package com.service.impl;

import com.dto.CourseDTO;
import com.exception.BusinessException;
import com.model.academic.Course;
import com.model.academic.CourseStatus;
import com.repository.CourseRepository;
import com.security.PermissionChecker;
import com.service.BaseService;

import java.util.List;

public class CourseServiceImpl implements BaseService<Course, Long, CourseDTO> {
    private final CourseRepository repo = new CourseRepository();

    @Override
    public List<Course> findAll() {
        PermissionChecker.requireAuthenticated();
        return repo.findAll();
    }

    @Override
    public List<Course> search(String keyword) {
        PermissionChecker.requireAuthenticated();
        return keyword == null || keyword.isBlank() ? repo.findAll() : repo.searchByName(keyword);
    }

    @Override
    public Course findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy khóa học."));
    }

    @Override
    public Course save(CourseDTO dto) {
        PermissionChecker.requireAdminOrAnyStaff();

        if (dto.getCourseName() == null || dto.getCourseName().isBlank())
            throw new com.exception.ValidationException("Tên khóa học không được để trống.");

        Course course = Course.builder()
                .courseName(dto.getCourseName().trim())
                .description(dto.getDescription().trim())
                .duration(dto.getDuration())
                .fee(dto.getFee())
                .level(dto.getLevel())
                .status(dto.getStatus() != null ? dto.getStatus() : CourseStatus.ACTIVE)
                .build();
        return repo.save(course);
    }

    @Override
    public Course update(Long id, CourseDTO dto) {
        PermissionChecker.requireAdminOrAnyStaff();

        if (dto.getCourseName() == null || dto.getCourseName().isBlank())
            throw new com.exception.ValidationException("Tên khóa học không được để trống.");

        Course old = findById(id);
        if (old == null)
            throw new BusinessException("Không tìm thấy khóa học!");

        old.setCourseName(dto.getCourseName().trim());
        old.setDescription(dto.getDescription().trim());
        old.setDuration(dto.getDuration());
        old.setFee(dto.getFee());
        old.setLevel(dto.getLevel());
        old.setStatus(dto.getStatus());
        return repo.update(old);
    }

    @Override
    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
