package com.model.operation;

import com.model.academic.Class;
import com.model.user.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long scheduleID;
    @ManyToOne
            @JoinColumn(name = "class_id", nullable = false)
    Class aClass;
    @ManyToOne
            @JoinColumn(name = "student_id", nullable = false)
    Student student;
    @OneToOne
            @JoinColumn(name = "room_id", nullable = false)
    Room room;
    LocalDate date;
    LocalDateTime startTime;
    LocalDateTime endTime;
}
