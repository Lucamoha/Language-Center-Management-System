package com.model.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Getter
public enum Period {
    PERIOD_1(LocalTime.of(7, 30), LocalTime.of(9, 0)),
    PERIOD_2(LocalTime.of(9, 0), LocalTime.of(10, 30)),
    PERIOD_3(LocalTime.of(10, 30), LocalTime.of(12, 0)),
    PERIOD_4(LocalTime.of(13, 0), LocalTime.of(14, 30)),
    PERIOD_5(LocalTime.of(14, 30), LocalTime.of(16, 0)),
    PERIOD_6(LocalTime.of(16, 0), LocalTime.of(17, 30)),
    PERIOD_7(LocalTime.of(17, 30), LocalTime.of(19, 0)),
    PERIOD_8(LocalTime.of(19, 0), LocalTime.of(20, 30)),
    PERIOD_9(LocalTime.of(20, 30), LocalTime.of(22, 30)),
    ;

    private final LocalTime startTime;
    private final LocalTime endTime;

    @Override
    public String toString() {
        return startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + " - "
                + endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}