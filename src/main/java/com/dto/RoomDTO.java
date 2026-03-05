package com.dto;

import com.model.operation.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    Long roomID;
    String roomName;
    Integer capacity;
    String location;
    RoomStatus status;
}
