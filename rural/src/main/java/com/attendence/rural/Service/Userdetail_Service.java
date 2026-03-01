package com.attendence.rural.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.attendence.rural.Repositor.User_Repo;

    @Service
    public class Userdetail_Service implements UserDetailsService {
    
     private final User_Repo userRepo;

    public Userdetail_Service(User_Repo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

}
