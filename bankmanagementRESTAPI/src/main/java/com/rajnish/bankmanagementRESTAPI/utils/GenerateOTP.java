package com.rajnish.bankmanagementRESTAPI.utils;

import java.security.SecureRandom;
import java.util.Locale;

public final class GenerateOTP {

    private static final SecureRandom RANDOM = new SecureRandom();

    private GenerateOTP() {
    }

    public static String generateOtp() {
        return String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
    }
}
