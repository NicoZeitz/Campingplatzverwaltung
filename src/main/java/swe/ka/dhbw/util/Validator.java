package swe.ka.dhbw.util;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\+(?:[0-9]?){6,14}[0-9]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static Validator instance;


    private Validator() {
    }

    public static synchronized Validator getInstance() {
        if (instance == null) {
            instance = new Validator();
        }
        return instance;
    }

    public void validateNotNull(final Object object, final String objectName) throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(objectName + " cannot be null!");
        }
    }

    public void validatePhoneNumber(final String phoneNumber) {
        var validPhoneNumber = PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
        if (!validPhoneNumber) {
            throw new IllegalArgumentException(phoneNumber + " is not a valid phone number!");
        }
    }

    public void validateEmail(final String email) {
        var validEmail = EMAIL_PATTERN.matcher(email).matches();
        if (!validEmail) {
            throw new IllegalArgumentException(email + " is not a valid email!");
        }
    }
}
