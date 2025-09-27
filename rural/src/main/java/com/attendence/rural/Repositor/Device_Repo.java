package com.attendence.rural.Repositor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.Device;

@Repository
public interface Device_Repo extends JpaRepository<Device, Integer> {
     Optional<Device> findByApiKeyAndActiveTrue(String apiKey);
    Optional<Device> findByDeviceId(String deviceId);
}
