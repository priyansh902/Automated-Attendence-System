package com.attendence.rural.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.Model.Device;
import com.attendence.rural.Model.School;
import com.attendence.rural.Repositor.School_Repo;
import com.attendence.rural.Service.Device_service;

@RestController
@RequestMapping("/api/device")
public class Device_controller {

    private final Device_service device_service;
    private final School_Repo school_Repo;

    public Device_controller(Device_service device_service, School_Repo school_Repo){
        this.device_service = device_service;
        this.school_Repo = school_Repo;
    }

     @PostMapping("/register")
     @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> registerDevice(@RequestParam String deviceId,
                                                 @RequestParam Integer schoolId) {
      // Fetch school from DB
        School school = school_Repo.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found with id " + schoolId));

        Device device = device_service.registerDevice(deviceId, school);
        return ResponseEntity.ok(device);
    }
    
}
