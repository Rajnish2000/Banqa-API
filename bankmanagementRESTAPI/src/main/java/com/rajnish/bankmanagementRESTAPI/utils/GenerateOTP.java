package com.rajnish.bankmanagementRESTAPI.utils;

import java.security.SecureRandom;
import java.util.Random;

public class GenerateOTP {
    public static String generateOtp(){
        Random rand = new Random();
        int otp = 1000 + rand.nextInt(9000);
        return Integer.toString(otp);
    }
}
