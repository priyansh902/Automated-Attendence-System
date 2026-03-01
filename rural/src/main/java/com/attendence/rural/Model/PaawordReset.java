package com.attendence.rural.Model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_tokens")
public class PaawordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Instant expiryDate;
    
    private boolean used = false;
    
    public PaawordReset(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = Instant.now().plus(1, ChronoUnit.HOURS); // 1 hour expiry
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

     public boolean isValid() {
        return !this.used && !isExpired();
    }
    
}
