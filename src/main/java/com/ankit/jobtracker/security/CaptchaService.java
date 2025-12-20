package com.ankit.jobtracker.security;

import org.springframework.stereotype.Component;

@Component
public class CaptchaService {

    // Simulated CAPTCHA validation
    // Later replace with Google reCAPTCHA API call
    public boolean validate(String captchaToken) {

        // For demo:
        // token "VALID_CAPTCHA" is considered correct
        return captchaToken != null && captchaToken.equals("VALID_CAPTCHA");
    }
}