package com.attendence.rural.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer studentid;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int rollno;

    private String classname;

    @Column(unique = true)
    private String Uniquecode;

    @ManyToOne
    @JoinColumn(name= "school_id")
    private School school;


    
}
