package com.repository;

import com.exception.SystemException;
import com.model.academic.Result;
import com.model.user.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ResultRepository extends BaseRepository<Result, Long> {

    public ResultRepository() {
        super(Result.class);
    }

    @Override
    public List<Result> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT r FROM Result r LEFT JOIN FETCH r.student LEFT JOIN FETCH r.aClass",
                    Result.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn kết quả: " + e.getMessage(), e);
        }
    }

    public List<Result> findByClassAndUser(Long classId, Long userId, UserRole userRole) {
        try (EntityManager em = em()) {
            String jpql = "SELECT r FROM Result r WHERE r.aClass.classID = :cid ";
            boolean hasUid = false;
            if (userRole == UserRole.STUDENT) {
                jpql += "AND r.student.studentID = :uid ";
                hasUid = true;
            } else if (userRole == UserRole.TEACHER) {
                jpql += "AND r.aClass.teacher.teacherID = :uid ";
                hasUid = true;
            }
            jpql += "ORDER BY r.student.studentID DESC";

            TypedQuery<Result> typedQuery = em.createQuery(jpql, Result.class)
                    .setParameter("cid", classId);
            if (hasUid) {
                typedQuery.setParameter("uid", userId);
                // do role khác teacher và student thì hàm này không có uid (không có giới hạn tìm kiếm)
            }
            return typedQuery.getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn kết quả theo lớp: " + e.getMessage(), e);
        }
    }

    public List<Result> findByStudent(Long studentId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT r FROM Result r WHERE r.student.studentID = :sid",
                            Result.class)
                    .setParameter("sid", studentId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn kết quả theo học viên: " + e.getMessage(), e);
        }
    }
}
