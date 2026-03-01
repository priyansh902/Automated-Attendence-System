package com.attendence.rural.Scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.attendence.rural.Service.PasswordReset_service;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PasswordResetTokenSceduler {
    private final PasswordReset_service passwordReset_service;

    public PasswordResetTokenSceduler(PasswordReset_service passwordReset_service){
        this.passwordReset_service = passwordReset_service;
    }

    
    // Clean up expired password reset tokens daily at 3 AM
     
    @Scheduled(cron = "0 0 3 * * ?")  // 3 AM daily
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled cleanup of expired password reset tokens...");
        try {
            passwordReset_service.cleanupExpiredTokens();
            log.info("Scheduled token cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled token cleanup: {}", e.getMessage(), e);
        }
    }
    
    
     // Additional cleanup every 6 hours for better maintenance
     
    @Scheduled(cron = "0 0 */6 * * ?")  // Every 6 hours
    public void frequentTokenCleanup() {
        log.debug("Running frequent token cleanup check...");
        try {
            passwordReset_service.cleanupExpiredTokens();
        } catch (Exception e) {
            log.warn("Minor issue during frequent token cleanup: {}", e.getMessage());
        }
    }
}
