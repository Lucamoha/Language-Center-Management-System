package com.dto;

import com.model.user.StaffRole;
import com.model.user.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StaffDTO {
    private Long staffID;
    private String fullName;
    private StaffRole role;
    private String phone;
    private String email;
    private UserStatus status;
    // Account credentials (used only on create)
    private String username;
    private String password;
}
