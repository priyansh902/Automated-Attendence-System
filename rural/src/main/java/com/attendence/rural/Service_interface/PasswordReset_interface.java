package com.attendence.rural.Service_interface;

import com.attendence.rural.RespDtos.PasswordResetResponse;

public interface PasswordReset_interface {

    PasswordResetResponse initiatePasswordReset(String username);

    PasswordResetResponse resetPassword(String token, String newPassword);

    PasswordResetResponse adminResetUserPassword(String adminUsername, String targetUsername);

    //   List<PaawordReset> getActiveTokens(String username);

     void cleanupExpiredTokens();

     int manualCleanup();

}
