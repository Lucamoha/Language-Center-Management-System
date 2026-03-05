package com.service.impl;

import com.dto.ClassDTO;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.academic.*;
import com.model.academic.Class;
import com.model.operation.Room;
import com.model.operation.RoomStatus;
import com.model.user.StaffRole;
import com.model.user.Teacher;
import com.model.user.UserStatus;
import com.repository.*;
import com.security.PermissionChecker;

import java.util.List;
import java.util.Optional;

public class ClassServiceImpl {
    private final ClassRepository classRepo = new ClassRepository();
    private final CourseRepository courseRepo = new CourseRepository();
    private final TeacherRepository teacherRepo = new TeacherRepository();
    private final RoomRepository roomRepo = new RoomRepository();

    public List<Class> findAll() {
        PermissionChecker.requireAuthenticated();
        var u = com.security.SecurityContext.get();
        if (u != null && u.isTeacher()) {
            return classRepo.findByTeacher(u.relatedId());
        }
        return classRepo.findAll();
    }

    public Class findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return classRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lớp học."));
    }

    public List<Class> search(String keyword) {
        PermissionChecker.requireAuthenticated();
        return keyword == null || keyword.isBlank() ? classRepo.findAll() : classRepo.searchByName(keyword);
    }

    public Class save(ClassDTO dto) throws Exception {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);
        if (dto.getClassName() == null || dto.getClassName().isBlank())
            throw new ValidationException("Tên lớp không được để trống.");

        Optional<Course> course = courseRepo.findById(dto.getCourseID());
        if(course.isEmpty())
            throw new BusinessException("Mã khóa học không tồn tại! Hãy nhập một mã khóa học khác!");
        else if(course.get().getStatus() != CourseStatus.ACTIVE)
            throw new BusinessException("Khóa học chưa sẵn sàng! Hãy nhập một mã khóa học khác!");

        Optional<Room> room = roomRepo.findById(dto.getRoomID());
        if(room.isEmpty())
            throw new BusinessException("Mã phòng học không tồn tại! Hãy nhập một mã phòng học khác!");
        else if(room.get().getStatus() != RoomStatus.ACTIVE)
            throw new BusinessException("Phòng học chưa sẵn sàng! Hãy nhập một mã phòng học khác!");

        Optional<Teacher> teacher = teacherRepo.findById(dto.getTeacherID());
        if(teacher.isEmpty())
            throw new BusinessException("Mã giáo viên không tồn tại! Hãy nhập một mã giáo viên khác!");
        else if(teacher.get().getStatus() != UserStatus.ACTIVE)
            throw new BusinessException("Giáo viên đã bị khóa! Hãy nhập một mã giáo viên khác!");

        Class aClass = Class.builder()
                .className(dto.getClassName().trim())
                .maxStudent(dto.getMaxStudent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus() != null ? dto.getStatus() : ClassStatus.ACTIVE)
                .course(course.get())
                .teacher(teacher.get())
                .room(room.get())
                .build();
        return classRepo.save(aClass);
    }

    public Class update(Long id, ClassDTO dto) throws Exception {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);
        Class old = this.findById(id);

        if (dto.getClassName() == null || dto.getClassName().isBlank())
            throw new ValidationException("Tên lớp không được để trống.");

        Optional<Course> course = courseRepo.findById(dto.getCourseID());
        if(course.isEmpty())
            throw new BusinessException("Mã khóa học không tồn tại! Hãy nhập một mã khóa học khác!");
        else if(course.get().getStatus() != CourseStatus.ACTIVE)
            throw new BusinessException("Khóa học chưa sẵn sàng! Hãy nhập một mã khóa học khác!");

        Optional<Room> room = roomRepo.findById(dto.getRoomID());
        if(room.isEmpty())
            throw new BusinessException("Mã phòng học không tồn tại! Hãy nhập một mã phòng học khác!");
        else if(room.get().getStatus() != RoomStatus.ACTIVE)
            throw new BusinessException("Phòng học chưa sẵn sàng! Hãy nhập một mã phòng học khác!");

        Optional<Teacher> teacher = teacherRepo.findById(dto.getTeacherID());
        if(teacher.isEmpty())
            throw new BusinessException("Mã giáo viên không tồn tại! Hãy nhập một mã giáo viên khác!");
        else if(teacher.get().getStatus() != UserStatus.ACTIVE)
            throw new BusinessException("Giáo viên đã bị khóa! Hãy nhập một mã giáo viên khác!");

        old.setClassName(dto.getClassName().trim());
        old.setMaxStudent(dto.getMaxStudent());
        old.setStatus(dto.getStatus());
        old.setCourse(course.get());
        old.setTeacher(teacher.get());
        old.setRoom(room.get());
        old.setStartDate(dto.getStartDate());
        old.setEndDate(dto.getEndDate());
        return classRepo.update(old);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        classRepo.delete(id);
    }
}
