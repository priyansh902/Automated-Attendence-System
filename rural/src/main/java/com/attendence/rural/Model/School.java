package com.attendence.rural.Model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer schoolId;

    private String name;

    private String location;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private List<Student> students;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private List<Teacher> teachers;
    
}
