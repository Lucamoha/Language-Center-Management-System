package com.model.academic;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(nullable = false)
    Integer duration; // hours / week

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    BigDecimal fee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    CourseStatus status = CourseStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
