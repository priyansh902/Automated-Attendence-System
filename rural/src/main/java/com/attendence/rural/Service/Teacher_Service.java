package com.attendence.rural.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.Excptions.TeacherNotFound;
import com.attendence.rural.Mapper.Teacher_mapper;
import com.attendence.rural.Model.Teacher;
import com.attendence.rural.Repositor.Teacher_Repo;
import com.attendence.rural.RespDtos.Teache_Resp;
import com.attendence.rural.Service_interface.Teacher_interface;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class Teacher_Service implements Teacher_interface {

    private final Teacher_Repo teacher_Repo;
    private final Teacher_mapper teacher_mapper;

    public Teacher_Service(Teacher_Repo teacher_Repo, Teacher_mapper teacher_mapper){
        this.teacher_Repo = teacher_Repo;
        this.teacher_mapper = teacher_mapper;
    }

    @Override
    public Teache_Resp getTeacherByusername(String username) {
        log.info("[TEACHER] Fetching teacher by username={}", username);

        Teacher teacher = teacher_Repo.findByUsername(username)
                            .orElseThrow(() -> {
                                log.warn("[TEACHER] Teacher not found: {}", username);
                                return new TeacherNotFound("Teacher Not Found " + username);
                            });

        log.debug("[TEACHER] Teacher found: username={}, teacherId={}", 
                  teacher.getUsername(), teacher.getTeacherId());
        return teacher_mapper.teache_Resp(teacher);
    }

    @Override
    public List<Teache_Resp> getAllTeachers() {
        log.info("[TEACHER] Fetching all teachers...");
        var teachers = teacher_Repo.findAll();
        log.debug("[TEACHER] Total teachers fetched={}", teachers.size());
        return teacher_mapper.toRespList(teachers);
    }

    @Override
    public void deleteTeacher(String username) {
        log.info("[TEACHER] Deleting teacher with username={}", username);

        Teacher teacher = teacher_Repo.findByUsername(username)
                            .orElseThrow(() -> {
                                log.warn("[TEACHER] Teacher not found, cannot delete: {}", username);
                                return new TeacherNotFound("Teacher Not Found " + username);
                            });

        teacher_Repo.delete(teacher);
        log.info("[TEACHER] Teacher deleted successfully → username={}, teacherId={}", 
                 teacher.getUsername(), teacher.getTeacherId());
    }
}
