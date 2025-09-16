package com.attendence.rural.Controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.Excptions.AdminNotFound;
import com.attendence.rural.Model.Role;
import com.attendence.rural.Model.User;
import com.attendence.rural.Repositor.Role_Repo;
import com.attendence.rural.Repositor.User_Repo;

@RestController
@RequestMapping("/api/admin")
public class Admin_controller {

    private final User_Repo userRepo;
    private final Role_Repo roleRepo;
    private final PasswordEncoder passwordEncoder;

    public Admin_controller(User_Repo userRepo, Role_Repo roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

     @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username").toLowerCase();
        String password = request.get("password");

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new AdminNotFound("ROLE_ADMIN not found"));
        user.setRoles(Set.of(adminRole));

        userRepo.save(user);

        return ResponseEntity.ok("Admin registered successfully!");
    }
    
}
