package com.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teacher")
@ToString
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    Long teacherID;

    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "phone")
    String phone;

    @Column(name = "email")
    String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "specialty")
    Specialty specialty;

    @CreationTimestamp
    @Column(name = "hire_date")
    LocalDateTime hireDate;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    UserStatus status = UserStatus.ACTIVE;
}


