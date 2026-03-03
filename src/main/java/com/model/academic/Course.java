package com.model.academic;

import com.model.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long courseID;

    @Column(nullable = false)
    String courseName;

    String description;

    @Enumerated(EnumType.STRING)
    Level level;

    Integer duration; // hours / week

    @Column(precision = 18, scale = 2)
    @Builder.Default
    BigDecimal fee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    CourseStatus status = CourseStatus.ACTIVE;

    @CreationTimestamp
    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
