package com.attendence.rural.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.Excptions.Invalid;
import com.attendence.rural.Excptions.PasswordNotFound;
import com.attendence.rural.Excptions.UserException;
import com.attendence.rural.Model.PaawordReset;
import com.attendence.rural.Model.User;
import com.attendence.rural.Repositor.PasswordReset_Repo;
import com.attendence.rural.Repositor.User_Repo;
import com.attendence.rural.RespDtos.PasswordResetResponse;
import com.attendence.rural.Service_interface.PasswordReset_interface;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j



public class PasswordReset_service implements PasswordReset_interface {
    
    private final User_Repo userRepository;
    private final PasswordReset_Repo tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordReset_service(User_Repo userRepository,PasswordReset_Repo tokenRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Initiate password reset for a username
    @Override
    public PasswordResetResponse initiatePasswordReset(String username) {

          log.info("Initiating password reset for username: {}", username);
        
        try {
            // Find user by username
            User user = userRepository.findByUsername(username.toLowerCase())
                    .orElseThrow(() -> {
                        log.warn("Password reset attempted for non-existent username: {}", username);
                        return new PasswordNotFound("If the username exists, reset instructions will be provided");
                    });
            
            // Check if user is a student (students shouldn't reset password this way)
            if (user.getStudent() != null) {
                log.warn("Password reset attempted for student account: {}", username);
                throw new PasswordNotFound("Student password reset not supported via this method");
            }
            
            // Invalidate any existing tokens for this user
            tokenRepository.deleteByUser(user);
            
            // Generate new token
            String token = generateSecureToken();
            PaawordReset resetToken = new PaawordReset(token, user);
            tokenRepository.save(resetToken);
            
            // Log the token for admin/teacher retrieval
            logResetToken(username, token);
            
            log.info("Password reset token generated for user: {}", username);
            
            return new PasswordResetResponse(
                "If the username exists, reset instructions will be provided", 
                true,
                token  // For testing - remove in production
            );
            
        } catch (RuntimeException e) {
            // Always return success to prevent username enumeration
            log.debug("Password reset process completed for username: {}", username);
            return new PasswordResetResponse(
                "If the username exists, reset instructions will be provided", 
                true,
                null
            );
        }


    }

    // Reset password using token
    @Override
    public PasswordResetResponse resetPassword(String token, String newPassword) {
  
         log.info("Attempting password reset with token");
        
        PaawordReset resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Invalid password reset token used: {}", token);
                    return new Invalid("Invalid or expired reset token");
                });
        
        // Validate token
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            log.warn("Expired password reset token used: {}", token);
            throw new Invalid("Reset token has expired. Please request a new one.");
        }
        
        if (resetToken.isUsed()) {
            log.warn("Already used password reset token: {}", token);
            throw new Invalid("Reset token has already been used. Please request a new one.");
        }
        
        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        log.info("Password successfully reset for user: {}", user.getUsername());
        
        return new PasswordResetResponse(
            "Password has been reset successfully", 
            true,
            null
        );

    }

    @Override
 
    //Admin endpoint to reset user password (for admin users)
    public PasswordResetResponse adminResetUserPassword(String adminUsername, String targetUsername) {
 
        log.info("Admin {} resetting password for user: {}", adminUsername, targetUsername);
        
        // Find target user
        User targetUser = userRepository.findByUsername(targetUsername.toLowerCase())
                .orElseThrow(() -> new UserException("User not found: " + targetUsername));
        
        // Generate temporary password
        String tempPassword = generateTemporaryPassword();
        targetUser.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(targetUser);
        
        // Invalidate any existing tokens
        tokenRepository.deleteByUser(targetUser);
        
        log.info("Admin {} reset password for user {}. Temporary password: {}", 
                adminUsername, targetUsername, tempPassword);
        
        return new PasswordResetResponse(
            "Password reset successfully. Temporary password: " + tempPassword, 
            true,
            tempPassword
        );   
        
    }

    // @Override
    // public List<PaawordReset> getActiveTokens(String username) {
  
    //      return tokenRepository.findActiveTokensByUsername(username).stream()
    //     .filter(token -> !token.isUsed() && !token.isExpired())
    //     .collect(Collectors.toList());
    // }

    private String generateSecureToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    private String generateTemporaryPassword() {
        return "Temp" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    
    private void logResetToken(String username, String token) {
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        
        log.info("=== PASSWORD RESET TOKEN ===");
        log.info("Username: {}", username);
        log.info("Reset Token: {}", token);
        log.info("Reset Link: {}", resetLink);
        log.info("Expires: {}", Instant.now().plus(1, java.time.temporal.ChronoUnit.HOURS));
        log.info("=== (Provide this token to the user) ===");
    }

    @Override
    //Clean up expired tokens (called by scheduler)
    public void cleanupExpiredTokens() {
 
         log.info("Cleaning up expired password reset tokens");
        try {
            // FIXED: Use proper method calls
            List<PaawordReset> expiredTokens = tokenRepository.findAll().stream()
                .filter(token -> token.isExpired())  // ✅ Fixed method call
                .filter(token -> !token.isUsed())
                .collect(Collectors.toList());
            
            for (PaawordReset token : expiredTokens) {
                token.setUsed(true);
                tokenRepository.save(token);
                log.debug("Marked expired token as used: {}", token.getToken());
            }
            
            // Then delete all expired tokens
            int deletedCount = tokenRepository.deleteExpiredTokens();
            log.info("Deleted {} expired password reset tokens", deletedCount);
            
        } catch (Exception e) {
            log.error("Error during token cleanup: {}", e.getMessage(), e);
            throw new RuntimeException("Token cleanup failed", e);
        }
        
    }

    @Override
    public int manualCleanup() {
  
         log.info("Manual token cleanup requested");
        return tokenRepository.deleteExpiredTokens();
    }
    

    
}
