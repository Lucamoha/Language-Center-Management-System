package com.dto;

import com.model.user.Specialty;
import com.model.user.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeacherDTO {
    private Long teacherID;
    private String fullName;
    private String phone;
    private String email;
    private Specialty specialty;
    private UserStatus status;
    // Account credentials (used only on create)
    private String username;
    private String password;
}
