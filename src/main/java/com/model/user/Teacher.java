package com.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teacher")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID teacherID;
    @Column(nullable = false)
    String fullName;
    String phone;
    String email;
    Specialty specialty;
    @CreationTimestamp
    LocalDateTime hireDate;
    LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
            @Builder.Default
    UserStatus status = UserStatus.ACTIVE;
}


