package com.stream;

import com.model.academic.Class;
import com.model.academic.Result;
import com.model.user.UserRole;
import com.repository.ResultRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultStreamQueries {
    private final ResultRepository resultRepository = new ResultRepository();
    private final ClassStreamQueries classStreamQueries = new ClassStreamQueries();

    public List<Result> findByClassAndUser(Long classId, Long userId, UserRole userRole) {
        return resultRepository.findAll().stream()
                .filter(r -> classId == null || r.getAClass().getClassID().equals(classId))
                .filter(r -> {
                    if (userRole == UserRole.STUDENT) {
                        return r.getStudent().getStudentID().equals(userId);
                    } else if (userRole == UserRole.TEACHER) {
                        return r.getAClass().getTeacher().getTeacherID().equals(userId);
                    }
                    return true;
                })
                .sorted(Comparator.comparing((Result r) -> r.getAClass().getClassID())
                        .thenComparing(r -> r.getStudent().getStudentID()))
                .toList();
    }

    public List<Result> findAllResultsInStudentClasses(Long studentId) {
        // 1. Lấy danh sách các lớp sinh viên đang học
        List<Long> classIds = classStreamQueries.findClassByStudent(studentId).stream()
                .map(Class::getClassID)
                .toList();

        // 2. Lọc tất cả điểm số thuộc về các lớp đó
        return resultRepository.findAll().stream()
                .filter(r -> classIds.contains(r.getAClass().getClassID()))
                .filter(r -> r.getScore() != null)
                .collect(Collectors.toList());
    }

    /**
     * Lấy điểm cao nhất của cá nhân trong mỗi khóa (tránh trường hợp học lại)
     */
    public Map<String, Double> getMaxScoreOfStudentInCourses(Long studentId) {
        return this.findAllResultsInStudentClasses(studentId).stream()
                .filter(r -> r.getStudent().getStudentID().equals(studentId))
                .collect(Collectors.toMap(
                        r -> r.getAClass().getCourse().getCourseName(),
                        r -> r.getScore() != null ? r.getScore() : 0.0, // tránh lỗi đưa null vào map
                        Double::max // Nếu trùng khóa học, lấy điểm cao nhất
                ));
    }

    /**
     * Tính trung bình điểm của tất cả mọi người theo từng khóa học
     */
    public Map<String, Double> findAllAverageResultsOfAllStudentsInSpecificStudentClass(Long studentId) {
        return this.findAllResultsInStudentClasses(studentId).stream()
                .collect(Collectors.groupingBy(
                        r -> r.getAClass().getCourse().getCourseName(),
                        Collectors.averagingDouble(Result::getScore)
                ));
    }

    public String generateSmartResultsEvaluation(Map<String, Double> myResults, Map<String, Double> avgResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dựa trên dữ liệu học tập hiện tại:\n\n");

        myResults.forEach((course, score) -> {
            double avg = avgResults.getOrDefault(course, 0.0);
            sb.append("- Khóa học [").append(course).append("]: ");
            if (score > avg) {
                sb.append("Bạn đang học TỐT hơn mức trung bình lớp (+").append(String.format("%.1f", score - avg)).append(" điểm). Phát huy nhé!");
            } else if (score < avg) {
                sb.append("Bạn đang thấp hơn trung bình lớp (").append(String.format("%.1f", avg - score)).append(" điểm). Cần tập trung hơn!");
            } else {
                sb.append("Bạn đang ở mức trung bình của lớp.");
            }
            sb.append("\n");
        });
        return sb.toString();
    }
}
