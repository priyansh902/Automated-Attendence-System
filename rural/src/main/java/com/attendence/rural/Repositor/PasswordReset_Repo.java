package com.attendence.rural.Repositor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.PaawordReset;
import com.attendence.rural.Model.User;


@Repository
public interface PasswordReset_Repo extends JpaRepository<PaawordReset, Integer> {
     Optional<PaawordReset>  findByToken(String token);
    
    List<PaawordReset> findByUser(User user);

    //    List<PaawordReset> findActiveTokensByUsername( String username);
    
     @Modifying
    @Query("DELETE FROM PaawordReset p WHERE p.user = :user")
     void deleteByUser( User user);
    
     @Modifying
    @Query("DELETE FROM PaawordReset p WHERE p.expiryDate < CURRENT_TIMESTAMP")
    int deleteExpiredTokens();

    // List<PaawordReset> findByUserUsernameAndUsedFalseAndExpiryDateAfter(String username, Instant currentTime);
    
}
