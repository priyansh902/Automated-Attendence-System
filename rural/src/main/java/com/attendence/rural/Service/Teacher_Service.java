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

@Service
@Transactional
public class Teacher_Service implements Teacher_interface {

    private final Teacher_Repo teacher_Repo;

    private final Teacher_mapper teacher_mapper;

    public Teacher_Service(Teacher_Repo teacher_Repo, Teacher_mapper teacher_mapper){
        this.teacher_Repo= teacher_Repo;
        this.teacher_mapper = teacher_mapper;
    }



    @Override
    public Teache_Resp getTeacherByusername(String username) {
        Teacher teacher = teacher_Repo.findByUsername(username)
                            .orElseThrow(() -> new TeacherNotFound("Teacher Not Found "+ username));

        return teacher_mapper.teache_Resp(teacher);
    }

    @Override
    public List<Teache_Resp> getAllTeachers() {
        return teacher_mapper.toRespList(teacher_Repo.findAll());
       }

    @Override
    public void deleteTeacher(String username) {
        Teacher teacher = teacher_Repo.findByUsername(username)
                            .orElseThrow(() -> new TeacherNotFound("Teacher Not Found " + username));
        teacher_Repo.delete(teacher);
    }
    
}
