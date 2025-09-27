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
import com.attendence.rural.Repositor.Role_Repo;
import com.attendence.rural.Repositor.School_Repo;
import com.attendence.rural.Repositor.Teacher_Repo;
import com.attendence.rural.Repositor.User_Repo;
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
    private final Role_Repo role_Repo;
    private final User_Repo user_Repo;


    public Teacher_AuthService(Teacher_Repo teacher_Repo,
                               School_Repo school_Repo,
                               Teacher_mapper teacher_mapper,
                               PasswordEncoder passwordEncoder,
                               Teacher_jwt teacher_jwt,
                               Role_Repo role_Repo,
                               User_Repo user_Repo) {
        this.teacher_Repo = teacher_Repo;
        this.teacher_jwt = teacher_jwt;
        this.teacher_mapper = teacher_mapper;
        this.school_Repo = school_Repo;
        this.passwordEncoder = passwordEncoder;
        this.role_Repo = role_Repo;
        this.user_Repo = user_Repo;
    }

     
    public Teache_Resp register(Teacher_dto dto) {
        log.info("[AUTH][REGISTER] Attempting registration for username={} in school={}", 
             dto.username(), dto.schoolName());

        // 1. Validate school
        School school = school_Repo.findByName(dto.schoolName())
            .orElseThrow(() -> {
            log.error("[AUTH][REGISTER] School not found: {}", dto.schoolName());
            return new SchoolNotFound("School Not Found: " + dto.schoolName());
            });

        // 2. Normalize username
        String normalizedUsername = dto.username().toLowerCase();

        if (teacher_Repo.findByUsername(normalizedUsername).isPresent()) {
          log.warn("[AUTH][REGISTER] Username already exists: {}", normalizedUsername);
         throw new Custom_ex("Username already exists: " + dto.username());
        }

        // 3. Hash password
         String hashedPassword = passwordEncoder.encode(dto.password());

        // 4. Create User entity and persist it first
         User user = new User();
        user.setUsername(normalizedUsername);
        user.setPassword(hashedPassword);

         Role teacherRole = role_Repo.findByName("ROLE_TEACHER")
          .orElseThrow(() -> new Custom_ex("ROLE_TEACHER not found"));
        user.setRoles(Set.of(teacherRole));

         user = user_Repo.save(user); // ✅ persist User first

         // 5. Create Teacher and link persisted User
        Teacher teacher = teacher_mapper.toEntity(dto, school);
        teacher.setPassword(hashedPassword);
        teacher.setUser(user);

        Teacher saved = teacher_Repo.save(teacher);

         // 6. Link back from User → Teacher (optional if bidirectional)
            user.setTeacher(saved);
             user_Repo.save(user);

            log.info("[AUTH][REGISTER] Registration successful → username={}, teacherId={}, school={}", 
                 saved.getUsername(), saved.getTeacherId(), saved.getSchool().getName());

        // ⚠️ don’t log password / dto directly if it contains password
         log.debug("[AUTH][REGISTER] Teacher registered with roles={}, school={}", 
                 user.getRoles(), school.getName());

         return teacher_mapper.teache_Resp(saved);
    }


}