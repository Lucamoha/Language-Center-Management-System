package com.service.impl;

import com.dto.EnrollmentDTO;
import com.exception.BusinessException;
import com.exception.InvalidStatusException;
import com.exception.SystemException;
import com.model.academic.Class;
import com.model.academic.ClassStatus;
import com.model.academic.Enrollment;
import com.model.user.Student;
import com.model.user.UserRole;
import com.model.user.UserStatus;
import com.repository.ClassRepository;
import com.repository.EnrollmentRepository;
import com.repository.StudentRepository;
import com.security.CurrentUser;
import com.security.PermissionChecker;

import java.util.List;

public class EnrollmentServiceImpl {
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepository();
    private final ClassRepository classRepo = new ClassRepository();
    private final StudentRepository studentRepo = new StudentRepository();

    public List<Enrollment> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.role() == UserRole.STUDENT) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : enrollmentRepo.findByStudent(sid);
        }
        return enrollmentRepo.findAll();
    }

    public List<Enrollment> findByStudent(Long studentId) {
        PermissionChecker.requireAuthenticated();
        return enrollmentRepo.findByStudent(studentId);
    }

    /**
     * Enroll a student into a class, enforcing maxStudent limit.
     */
    public Enrollment save(EnrollmentDTO dto) {
        CurrentUser user = PermissionChecker.requireAuthenticated();
        Student aStudent = studentRepo.findById(dto.getStudentID())
                .orElseThrow(() -> new BusinessException("Mã học viên không tồn tại! Hãy nhập mã học viên khác!"));
        if(aStudent.getStatus() != UserStatus.ACTIVE)
            throw new InvalidStatusException("Học viên bị khóa! Nhập mã học viên khác!");
        Class aClass = classRepo.findById(dto.getClassID())
                .orElseThrow(() -> new BusinessException("Mã lớp học không tồn tại! Hãy nhập mã lớp học khác!"));
        if(aClass.getStatus() != ClassStatus.ACTIVE)
            throw new InvalidStatusException("Lớp học bị khóa! Nhập lớp học khác!");
        if(user.isStudent()){
            long current = enrollmentRepo.countByClass(dto.getClassID());
            if (aClass.getMaxStudent() > 0 && current >= aClass.getMaxStudent()) {
                throw new BusinessException(
                        "Lớp học đã đủ số học viên tối đa (" + aClass.getMaxStudent() + " người).");
            }
        } else if(!user.isAdmin() && !user.isConsultant()){
            throw new BusinessException(
                    "Bạn không có quyền đăng ký lớp học!");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setAclass(aClass);
        enrollment.setStudent(aStudent);

        try {
            return enrollmentRepo.save(enrollment);
        } catch (SystemException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause.getMessage().contains("Duplicate entry")) {
                    throw new BusinessException("Học viên đã từng đăng ký lớp học này! Hãy điều chỉnh lại các trường giá trị!");
                }
                cause = cause.getCause();
            }
            throw e;// Nếu là lỗi khác thì ném tiếp ra ngoài
        }
    }

    public Enrollment update(Long id, EnrollmentDTO dto) {
        CurrentUser user = PermissionChecker.requireAuthenticated();
        Enrollment old = enrollmentRepo.findById(dto.getEnrollmentID())
                .orElseThrow(() -> new BusinessException("Lịch sử đăng ký lớp học không tồn tại!"));

        Student aStudent = studentRepo.findById(dto.getStudentID())
                .orElseThrow(() -> new BusinessException("Mã học viên không tồn tại! Hãy nhập mã học viên khác!"));
        if(aStudent.getStatus() != UserStatus.ACTIVE)
            throw new InvalidStatusException("Học viên bị khóa! Nhập mã học viên khác!");

        Class aClass = classRepo.findById(dto.getClassID())
                .orElseThrow(() -> new BusinessException("Mã lớp học không tồn tại! Hãy nhập mã lớp học khác!"));
        if(aClass.getStatus() != ClassStatus.ACTIVE)
            throw new InvalidStatusException("Lớp học bị khóa! Nhập lớp học khác!");

        if(user.isStudent()){
            long current = enrollmentRepo.countByClass(dto.getClassID());
            if (aClass.getMaxStudent() > 0 && current >= aClass.getMaxStudent()) {
                throw new BusinessException(
                        "Lớp học đã đủ số học viên tối đa (" + aClass.getMaxStudent() + " người).");
            }
        } else if(!user.isAdmin() && !user.isConsultant()){
            throw new BusinessException(
                    "Bạn không có quyền đăng ký lớp học!");
        }

        old.setAclass(aClass);
        old.setStudent(aStudent);

        try {
            return enrollmentRepo.update(old);
        } catch (SystemException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause.getMessage().contains("Duplicate entry")) {
                    throw new BusinessException("Học viên đã từng đăng ký lớp học này! Hãy điều chỉnh lại các trường giá trị!");
                }
                cause = cause.getCause();
            }
            throw e;// Nếu là lỗi khác thì ném tiếp ra ngoài
        }
    }

    public void delete(Long id) {
        PermissionChecker.requireAdminOrAnyStaff();
        enrollmentRepo.delete(id);
    }
}
