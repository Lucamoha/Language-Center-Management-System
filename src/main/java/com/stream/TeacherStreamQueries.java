package com.stream;

import com.model.user.Specialty;
import com.model.user.Teacher;
import com.model.user.UserStatus;

import java.util.List;

public final class TeacherStreamQueries {

    public static List<Teacher> search(List<Teacher> teachers, String keyword) {
        return teachers.stream()
                .filter(teacher -> teacher.getFullName().toLowerCase().contains(keyword.toLowerCase())
                        || teacher.getPhone().toLowerCase().contains(keyword.toLowerCase())
                        || teacher.getEmail().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }

    public static List<Teacher> filterByStatus(List<Teacher> teachers, UserStatus status) {
        return teachers.stream()
                .filter(teacher -> teacher.getStatus().equals(status))
                .toList();
    }

    public static List<Teacher> filterBySpecialty(List<Teacher> teachers, Specialty specialty) {
        return teachers.stream()
                .filter(teacher -> teacher.getSpecialty().equals(specialty))
                .toList();
    }
}
