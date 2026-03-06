package com.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleDTO {
    Long scheduleID;
    Long classID;
    Long roomID;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
}