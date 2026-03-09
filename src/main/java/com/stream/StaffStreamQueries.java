package com.stream;

import com.model.user.Staff;
import com.model.user.StaffRole;
import com.model.user.UserStatus;

import java.util.List;

public final class StaffStreamQueries {

    public static List<Staff> search(List<Staff> staffList, String keyword) {
        return staffList.stream()
                .filter(staff -> staff.getFullName().toLowerCase().contains(keyword.toLowerCase())
                        || staff.getPhone().toLowerCase().contains(keyword.toLowerCase())
                        || staff.getEmail().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }

    public static List<Staff> filterByStatus(List<Staff> staffList, UserStatus status) {
        return staffList.stream()
                .filter(staff -> staff.getStatus().equals(status))
                .toList();
    }

    public static List<Staff> filterByRole(List<Staff> staffList, StaffRole staffRole) {
        return staffList.stream()
                .filter(staff -> staff.getRole().equals(staffRole))
                .toList();
    }
}
