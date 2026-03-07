package com.dto;

import com.model.operation.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDTO {
    Long attendanceID;
    Long studentID;
    Long classID;
    AttendanceStatus status;
}
