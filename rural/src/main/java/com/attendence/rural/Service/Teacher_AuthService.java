package com.attendence.rural.Service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.DTos.Teacher_dto;
import com.attendence.rural.Excptions.Custom_ex;
import com.attendence.rural.Excptions.SchoolNotFound;
import com.attendence.rural.Jwt.Teacher_jwt;
import com.attendence.rural.Mapper.Teacher_mapper;
import com.attendence.rural.Model.Role;
import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Teacher;
import com.attendence.rural.Model.User;
import com.attendence.rural.Repositor.School_Repo;
import com.attendence.rural.Repositor.Teacher_Repo;
import com.attendence.rural.RespDtos.Teache_Resp;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class Teacher_AuthService {

    private final Teacher_Repo teacher_Repo;
    private final School_Repo school_Repo;
    private final Teacher_mapper teacher_mapper;    
    private final PasswordEncoder passwordEncoder;
    private final Teacher_jwt teacher_jwt;


    public Teacher_AuthService(Teacher_Repo teacher_Repo,
                               School_Repo school_Repo,
                               Teacher_mapper teacher_mapper,
                               PasswordEncoder passwordEncoder,
                               Teacher_jwt teacher_jwt) {
        this.teacher_Repo = teacher_Repo;
        this.teacher_jwt = teacher_jwt;
        this.teacher_mapper = teacher_mapper;
        this.school_Repo = school_Repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Teache_Resp register(Teacher_dto dto){
        log.info("[AUTH][REGISTER] Attempting registration for username={} in school={}", 
                 dto.username(), dto.schoolName());

        School school = school_Repo.findByName(dto.schoolName())
            .orElseThrow(() -> {
                log.error("[AUTH][REGISTER] School not found: {}", dto.schoolName());
                return new SchoolNotFound("School Not Found: " + dto.schoolName());
            });

        String normalizedUsername = dto.username().toLowerCase();

        if(teacher_Repo.findByUsername(normalizedUsername).isPresent()) {
            log.warn("[AUTH][REGISTER] Username already exists: {}", normalizedUsername);
            throw new Custom_ex("Username already exists: " + dto.username());
        }

        Teacher teacher = teacher_mapper.toEntity(dto, school);
        teacher.setPassword(passwordEncoder.encode(dto.password())); // hash password

         // Create User for login
         User user = new User();
         user.setUsername(normalizedUsername);
         user.setPassword(teacher.getPassword()); // hashed password
         Role teacherRole = new Role(); 
         teacherRole.setName("ROLE_TEACHER");
         user.setRoles(Set.of(teacherRole));

        teacher.setUser(user);
        user.setTeacher(teacher);

        Teacher saved = teacher_Repo.save(teacher);

        log.info("[AUTH][REGISTER] Registration successful → username={}, teacherId={}, school={}", 
                 saved.getUsername(), saved.getTeacherId(), saved.getSchool().getName());
        log.debug("[AUTH][REGISTER] Teacher password hashed, DTO details: {}", dto);

        return teacher_mapper.teache_Resp(saved);
    }

//     public login_REsp login(Login_dto dto){
//         String normalizedUsername = dto.username().toLowerCase();
//         log.info("[AUTH][LOGIN] Attempt login for username={}", normalizedUsername);

//         Teacher teacher = teacher_Repo.findByUsername(normalizedUsername)
//             .orElseThrow(() -> {
//                 log.warn("[AUTH][LOGIN] Invalid username: {}", normalizedUsername);
//                 return new TeacherNotFound("Invalid Username/Password");
//             });

//         if(!passwordEncoder.matches(dto.password(), teacher.getPassword())) {
//             log.warn("[AUTH][LOGIN] Invalid password attempt for username={}", normalizedUsername);
//             throw new Custom_ex("Invalid Username/Password");
//         }

//         User user = teacher.getUser();
//         List<String> roles = user.getRoles().stream()
//                                 .map(Role::getName)
//                                 .toList();

//         // Generate JWT token with roles
//         String token = teacher_jwt.generateToken(teacher.getUsername(),roles);

//         log.info("[AUTH][LOGIN] Login successful → username={}, teacherId={}", 
//              teacher.getUsername(), teacher.getTeacherId());
//      log.debug("[AUTH][LOGIN] JWT token generated: {}", token);

//         return new login_REsp(token, teacher.getUsername(),roles);
//     }
}
