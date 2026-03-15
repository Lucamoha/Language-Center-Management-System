package com.stream;

import com.model.operation.Room;
import com.repository.RoomRepository;

import java.util.List;
import java.util.Locale;

public class RoomStreamQueries {
    private final RoomRepository roomRepository = new RoomRepository();

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    /**
     * search by name, case insensitive (không phân biệt hoa thường)
     *
     * @param keyword String
     * @return List<Room>
     */
    public List<Room> searchByName(String keyword) {
        return roomRepository.findAll().stream()
                .filter(r -> safeLower(r.getRoomName()).contains(keyword))
                .toList();
    }
}
