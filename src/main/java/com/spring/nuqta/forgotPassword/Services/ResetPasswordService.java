package com.spring.nuqta.forgotPassword.Services;

import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.forgotPassword.Repo.ResetPasswordRepo;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ResetPasswordService {

    private final ResetPasswordRepo resetPasswordRepo;

    public ResetPasswordService(ResetPasswordRepo otpVerifyRepo) {
        this.resetPasswordRepo = otpVerifyRepo;
    }


    public void deleteOtp(ResetPasswordEntity otp) {
        resetPasswordRepo.delete(otp);
    }

    public String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        String output = Integer.toString(randomNumber);

        while (output.length() < 6) {
            output = "0" + output;
        }
        return output;
    }


}
