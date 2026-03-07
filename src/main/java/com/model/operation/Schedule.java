package com.model.operation;

import com.model.academic.Class;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule", uniqueConstraints = {
        @UniqueConstraint(
                name = "unq_room_time_in_schedule",
                columnNames = {"class_id", "room_id", "date", "start_time", "end_time"}
        )
})
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    Long scheduleID;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    Class aClass;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    Room room;

    @Column(name = "date")
    LocalDate date;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;
}
