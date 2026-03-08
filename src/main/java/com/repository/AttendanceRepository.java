package com.repository;

import com.exception.SystemException;
import com.model.operation.Attendance;
import com.model.user.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AttendanceRepository extends BaseRepository<Attendance, Long> {

    public AttendanceRepository() {
        super(Attendance.class);
    }

    @Override
    public List<Attendance> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT a FROM Attendance a " +
                            "LEFT JOIN FETCH a.student " +
                            "LEFT JOIN FETCH a.schedule s " +
                            "ORDER BY s.date, s.startTime DESC",
                    Attendance.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh: " + e.getMessage(), e);
        }
    }

    public List<Attendance> findByClassAndUser(Long classId, Long relatedId, UserRole userRole) {
        try (EntityManager em = em()) {
            String jpql = "SELECT a FROM Attendance a " +
                    "WHERE a.aClass.classID = :cid ";
            boolean hasUid = false;
            if (userRole == UserRole.STUDENT) {
                jpql += "AND a.student.studentID = :uid ";
                hasUid = true;
            } else if (userRole == UserRole.TEACHER) {
                jpql += "AND a.aClass.teacher.teacherID = :uid ";
                hasUid = true;
            }
            jpql += "ORDER BY a.createdAt DESC";

            TypedQuery<Attendance> typedQuery = em.createQuery(jpql, Attendance.class)
                    .setParameter("cid", classId);
            if (hasUid) {
                typedQuery.setParameter("uid", relatedId);
                // do role khác teacher và student thì hàm này không có uid (không có giới hạn tìm kiếm)
            }
            return typedQuery.getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh theo lớp: " + e.getMessage(), e);
        }
    }

    public List<Attendance> findByStudent(Long studentId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT a FROM Attendance a WHERE a.student.studentID = :sid ORDER BY a.createdAt DESC",
                            Attendance.class)
                    .setParameter("sid", studentId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh theo học viên: " + e.getMessage(), e);
        }
    }
}
