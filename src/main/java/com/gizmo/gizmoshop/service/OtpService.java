package com.gizmo.gizmoshop.service;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new HashMap<>();
    private final long OTP_EXPIRATION_TIME = 300000; // Thời gian hết hạn OTP (5 phút)

    // Tạo OTP
    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);
        System.out.println("Generated OTP for " + email + ": " + otp);  // Debugging log
        return otp;
    }

    // Xác thực OTP
    public boolean validateOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp == null) {
            System.out.println("No OTP found for " + email);
            return false;
        }

        // Kiểm tra OTP có hợp lệ
        if (otp.equals(storedOtp)) {
            System.out.println("OTP for " + email + " is valid.");
            return true;
        } else {
            System.out.println("Invalid OTP for " + email);
            return false;
        }
    }

    // Xóa OTP
    public void invalidateOtp(String email) {
        otpStorage.remove(email);
        System.out.println("OTP invalidated for " + email);
    }
}
