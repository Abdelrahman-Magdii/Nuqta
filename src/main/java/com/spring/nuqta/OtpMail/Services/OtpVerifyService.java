package com.spring.nuqta.OtpMail.Services;

import com.spring.nuqta.OtpMail.Entity.OtpVerifyEntity;
import com.spring.nuqta.OtpMail.Repo.OtpVerifyRepo;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpVerifyService {

    private final OtpVerifyRepo otpVerifyRepo;

    public OtpVerifyService(OtpVerifyRepo otpVerifyRepo) {
        this.otpVerifyRepo = otpVerifyRepo;
    }


    public void deleteOtp(OtpVerifyEntity otp) {
        otpVerifyRepo.delete(otp);
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
