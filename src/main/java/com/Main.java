package com;

import com.model.user.Staff;
import com.model.user.StaffRole;
import com.model.user.UserStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        //test only
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("dataSource");
        Staff staff = new Staff(Long.valueOf("1"), "K Duy", StaffRole.CONSULTANT, "", "", UserStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        emf.close();
    }
}