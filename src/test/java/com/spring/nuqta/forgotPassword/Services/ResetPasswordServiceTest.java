package com.spring.nuqta.forgotPassword.Services;

import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.forgotPassword.Repo.ResetPasswordRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResetPasswordServiceTest {

    private ResetPasswordRepo resetPasswordRepo;
    private ResetPasswordService resetPasswordService;

    @BeforeEach
    void setUp() {
        resetPasswordRepo = mock(ResetPasswordRepo.class); // Mock the repo
        resetPasswordService = new ResetPasswordService(resetPasswordRepo); // Inject mock repo
    }

    @Test
    void testGenerateOtp_ShouldReturnSixDigitString() {
        // Act
        String otp = resetPasswordService.generateOtp();

        // Assert
        assertNotNull(otp, "OTP should not be null");
        assertEquals(6, otp.length(), "OTP should be exactly 6 digits");
        assertTrue(otp.matches("\\d{6}"), "OTP should contain only digits");
    }

    @Test
    void testDeleteOtp_ShouldCallRepoDelete() {
        // Arrange
        ResetPasswordEntity otpEntity = new ResetPasswordEntity();

        // Act
        resetPasswordService.deleteOtp(otpEntity);

        // Assert
        verify(resetPasswordRepo, times(1)).delete(otpEntity);
    }

    @Test
    void testGenerateOtp_ShouldPadWithLeadingZeros() {
        // Act
        String otp = resetPasswordService.generateOtp();

        // Assert
        assertEquals(6, otp.length(), "OTP should be exactly 6 characters long");
        assertTrue(otp.matches("\\d{6}"), "OTP should contain only digits");
        assertTrue(Integer.parseInt(otp) >= 0 && Integer.parseInt(otp) <= 999999,
                "OTP should be a valid 6-digit number");
    }

}
