package com.venkatesh.it.usermanagementservice.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidMobileNumber(String mobileNumber) {
        return mobileNumber != null && MOBILE_PATTERN.matcher(mobileNumber).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return PASSWORD_UPPERCASE.matcher(password).matches() &&
               PASSWORD_LOWERCASE.matcher(password).matches() &&
               PASSWORD_DIGIT.matcher(password).matches() &&
               PASSWORD_SPECIAL.matcher(password).matches();
    }

    public static String getPasswordValidationMessage() {
        return "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.";
    }

    public static String getEmailValidationMessage() {
        return "Email must be a valid email address.";
    }

    public static String getMobileValidationMessage() {
        return "Mobile number must be 10 digits starting with 6-9.";
    }
}
