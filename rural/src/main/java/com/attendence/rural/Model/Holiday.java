package com.attendence.rural.Model;




import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "holidays", uniqueConstraints = {
    @UniqueConstraint(name = "uk_holiday_date", columnNames = {"date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private LocalDate date;
    
    @Column(nullable = false, length = 100)
    private String description; // "Diwali", "Republic Day", "Summer Break"
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private LocalDate createdAt = LocalDate.now();
    
    public Holiday(LocalDate date, String description) {
        this.date = date;
        this.description = description;
    }
}