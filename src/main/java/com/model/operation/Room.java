package com.model.operation;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long roomID;
    @Column(unique = true, nullable = false)
    String roomName;
    @Column(nullable = false)
            @Builder.Default
    Integer capacity = 0;
    String location;
    @Builder.Default
    RoomStatus status = RoomStatus.ACTIVE;
    @CreationTimestamp
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
