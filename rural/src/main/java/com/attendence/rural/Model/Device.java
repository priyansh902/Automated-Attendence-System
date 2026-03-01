package com.attendence.rural.Model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "device", uniqueConstraints = {
        @UniqueConstraint(name = "uk_device_id", columnNames = "deviceId"),
        @UniqueConstraint(name = "uk_device_api_key", columnNames = "apiKey")
})
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String deviceId; // e.g. "RFID_GATE_1"

    @Column(nullable = false, length = 255)
    private String apiKey; // generated secure random key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school; // optional: link to school

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant lastSeen; // update when device reports in

      public Device(String deviceId, String apiKey, School school) {
        this.deviceId = deviceId;
        this.apiKey = apiKey;
        this.school = school;
        this.active = true;
        this.createdAt = Instant.now();
    }

}
