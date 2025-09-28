package com.attendence.rural.Service_interface;

import java.util.List;

import com.attendence.rural.Model.PaawordReset;
import com.attendence.rural.RespDtos.PasswordResetResponse;

public interface PasswordReset_interface {

    PasswordResetResponse initiatePasswordReset(String username);

    PasswordResetResponse resetPassword(String token, String newPassword);

    PasswordResetResponse adminResetUserPassword(String adminUsername, String targetUsername);

    //   List<PaawordReset> getActiveTokens(String username);

     void cleanupExpiredTokens();

     int manualCleanup();

}
