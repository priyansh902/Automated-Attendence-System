package com.attendence.rural.Model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Student> students= new ArrayList<>();

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Teacher> teachers = new ArrayList<>();
    
}
