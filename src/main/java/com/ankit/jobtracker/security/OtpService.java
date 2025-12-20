package com.ankit.jobtracker.security;

import com.ankit.jobtracker.entity.OtpToken;
import com.ankit.jobtracker.repository.OtpTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;

    public OtpService(OtpTokenRepository otpTokenRepository) {
        this.otpTokenRepository = otpTokenRepository;
    }

    public void generateOtp(String username) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpToken token = new OtpToken();
        token.setUsername(username);
        token.setOtp(otp);
        token.setExpiryTime(Instant.now().plusSeconds(300)); // 5 minutes

        otpTokenRepository.save(token);

        // Simulate sending OTP
        System.out.println("OTP for " + username + ": " + otp);
    }

    public boolean verifyOtp(String username, String otp) {

        OtpToken token = otpTokenRepository
                .findTopByUsernameOrderByExpiryTimeDesc(username)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (token.isUsed()) return false;
        if (token.getExpiryTime().isBefore(Instant.now())) return false;

        if (!token.getOtp().equals(otp)) return false;

        token.setUsed(true);
        otpTokenRepository.save(token);

        return true;
    }
}