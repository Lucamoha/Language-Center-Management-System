package com.service.impl;

import com.dto.AttendanceDTO;
import com.exception.BusinessException;
import com.exception.SystemException;
import com.model.academic.Class;
import com.model.operation.Attendance;
import com.model.user.Student;
import com.model.user.UserRole;
import com.repository.AttendanceRepository;
import com.repository.ClassRepository;
import com.repository.StudentRepository;
import com.security.PermissionChecker;

import java.util.List;
import java.util.Optional;

public class AttendanceServiceImpl {
    private final AttendanceRepository repo = new AttendanceRepository();
    private final ClassRepository classRepo = new ClassRepository();
    private final StudentRepository studentRepo = new StudentRepository();

    public List<Attendance> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.isTeacher()) {
            Long tid = u.relatedId();
            return repo.findAll().stream()
                    .filter(a -> a.getAClass() != null
                            && a.getAClass().getTeacher() != null
                            && a.getAClass().getTeacher().getTeacherID().equals(tid)
                    )
                    .toList();
        }
        if (u.isStudent()) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : repo.findByStudent(sid);
        }
        return repo.findAll();
    }

    public Attendance save(AttendanceDTO dto) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isTeacher() && !u.isAdmin())
            throw new BusinessException("Bạn không có quyền tạo điểm danh.");

        Optional<Class> aClass = classRepo.findById(dto.getClassID());
        if (aClass.isEmpty())
            throw new BusinessException("Không tìm thấy lớp học");

        Optional<Student> student = studentRepo.findById(dto.getStudentID());
        if (student.isEmpty())
            throw new BusinessException("Không tìm thấy học viên");

        if (u.isTeacher()) {
            Long tid = u.relatedId();
            if (aClass.get().getTeacher() == null
                    || !aClass.get().getTeacher().getTeacherID().equals(tid))
                throw new BusinessException("Bạn chỉ được điểm danh lớp mình dạy.");
        }

        Attendance attendance = Attendance.builder()
                .student(student.get())
                .aClass(aClass.get())
                .status(dto.getStatus()).build();

        try {
            return repo.save(attendance);
        } catch (SystemException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause.getMessage().contains("Duplicate entry")) {
                    throw new BusinessException("Sinh viên này đã từng được điểm danh trong lớp này! Hãy điều chỉnh lại các trường giá trị!");
                }
                cause = cause.getCause();
            }
            throw e;// Nếu là lỗi khác thì ném tiếp ra ngoài
        }
    }

    public Attendance update(AttendanceDTO dto) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isTeacher() && !u.isAdmin())
            throw new BusinessException("Bạn không có quyền sửa điểm danh.");

        Optional<Attendance> old = repo.findById(dto.getAttendanceID());

        if (old.isEmpty())
            throw new BusinessException("Không tìm thấy mã điểm danh.");

        Optional<Class> aClass = classRepo.findById(dto.getClassID());
        if (aClass.isEmpty())
            throw new BusinessException("Không tìm thấy mã lớp học! Hãy nhập mã lớp học khác!");

        Optional<Student> student = studentRepo.findById(dto.getStudentID());
        if (student.isEmpty())
            throw new BusinessException("Không tìm thấy mã học viên! Hãy nhập mã học viên khác!");

        old.get().setAClass(aClass.get());
        old.get().setStudent(student.get());
        old.get().setStatus(dto.getStatus());

        try {
            return repo.update(old.get());
        } catch (SystemException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause.getMessage().contains("Duplicate entry")) {
                    throw new BusinessException("Sinh viên này đã từng được điểm danh trong lớp này! Hãy điều chỉnh lại các trường giá trị!");
                }
                cause = cause.getCause();
            }
            throw e;// Nếu là lỗi khác thì ném tiếp ra ngoài
        }
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }

    public List<Attendance> search(String keyword) {
        PermissionChecker.requireAuthenticated();
        return repo.findByClass(Long.parseLong(keyword));
    }
}
