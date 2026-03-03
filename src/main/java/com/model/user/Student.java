package com.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student")
@ToString
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long studentID;

    @Column(nullable = false)
    String fullName;

    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    Gender gender;

    String phone;

    String email;

    String address;

    @CreationTimestamp
    LocalDateTime registeredAt;

    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    UserStatus status = UserStatus.ACTIVE;
}
