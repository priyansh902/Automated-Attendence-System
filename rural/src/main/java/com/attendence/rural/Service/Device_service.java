package com.attendence.rural.Service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.attendence.rural.Model.Device;
import com.attendence.rural.Model.School;
import com.attendence.rural.Repositor.Device_Repo;

@Service
public class Device_service {
    
    private final Device_Repo device_Repo;

    public Device_service(Device_Repo device_Repo){
        this.device_Repo = device_Repo;
    }

      // Register a new device
    public Device registerDevice(String deviceId, School school) {
        String apiKey = generateApiKey();
        Device device = new Device(deviceId, apiKey, school);
        return device_Repo.save(device);
    }

    // Generate a secure random API key
    private String generateApiKey() {
        byte[] randomBytes = new byte[32]; // 256-bit
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}
