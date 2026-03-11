package com.dto;

import com.model.academic.ClassStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassDTO {
    Long classID;
    String className;
    Integer maxStudent;
    Long courseID;
    Long teacherID;
    Long roomID;
    ClassStatus status;
    LocalDate startDate;
    String daysOfWeek;
    LocalTime startTime;
    LocalTime endTime;
}
