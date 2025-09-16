package com.attendence.rural.Repositor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendence.rural.Model.User;

public interface User_Repo extends JpaRepository<User,Long> {
     Optional<User> findByUsername(String username);
}
