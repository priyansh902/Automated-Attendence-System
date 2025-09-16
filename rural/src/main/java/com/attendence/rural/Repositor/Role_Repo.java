package com.attendence.rural.Repositor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendence.rural.Model.Role;

public interface Role_Repo extends JpaRepository<Role, Long> {
     Optional<Role> findByName(String name);
}
