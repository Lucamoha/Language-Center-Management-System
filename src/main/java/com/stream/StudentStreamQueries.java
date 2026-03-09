package com.stream;

import com.model.user.Student;
import com.model.user.UserStatus;

import java.util.List;

public final class StudentStreamQueries {

    public static List<Student> search(List<Student> students, String keyword) {
        return students.stream()
                .filter(student -> student.getFullName().toLowerCase().contains(keyword.toLowerCase())
                        || student.getPhone().toLowerCase().contains(keyword.toLowerCase())
                        || student.getEmail().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }

    public static List<Student> filterByStatus(List<Student> students, UserStatus status) {
        return students.stream()
                .filter(student -> student.getStatus().equals(status))
                .toList();
    }
}
