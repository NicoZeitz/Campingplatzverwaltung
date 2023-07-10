package swe.ka.dhbw.util;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\+(?:[0-9]?){6,14}[0-9]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);
    private static Validator instance;


    private Validator() {
    }

    public static synchronized Validator getInstance() {
        if (instance == null) {
            instance = new Validator();
        }
        return instance;
    }

    public void validateNotNull(final Object object) throws NullPointerException {
        if (object == null) {
            throw new IllegalArgumentException("Validator::validateNotNull: Argument cannot be null!");
        }
    }

    public void validateNotEmpty(final String string) throws IllegalArgumentException {
        this.validateNotNull(string);
        if (string.isEmpty()) {
            throw new IllegalArgumentException("Validator::validateNotEmpty: String cannot be empty!");
        }
    }

    public void validatePhoneNumber(final String phoneNumber) {
        this.validateNotNull(phoneNumber);
        var validPhoneNumber = PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
        if (!validPhoneNumber) {
            throw new IllegalArgumentException("Validator::validatePhoneNumber: " + phoneNumber + " is not a valid phone number!");
        }
    }

    public void validateEmail(final String email) {
        this.validateNotEmpty(email);
        var validEmail = EMAIL_PATTERN.matcher(email).matches();
        if (!validEmail) {
            throw new IllegalArgumentException("Validator::validateEmail: " + email + " is not a valid email!");
        }
    }

    public void validateGreaterThan(final long value, final long min) {
        if (value < min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateGreaterThanEqual(final long value, final long min) {
        if (value <= min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateGreaterThanEqual(final double value, final double min) {
        if (value <= min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateGreaterThan(final double value, final double min) {
        if (value < min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateLessThan(final long value, final long min) {
        if (value < min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateLessThan(final double value, final double min) {
        if (value < min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateLessThanEqual(final long value, final long min) {
        if (value <= min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateLessThanEqual(final double value, final double min) {
        if (value <= min) {
            throw new IllegalArgumentException("Validator::validateGreaterThan: " + value + " is not greater than " + min + "!");
        }
    }

    public void validateInRange(final long value, final long min, final long max) {
        if (value <= min || value >= max) {
            throw new IllegalArgumentException("Validator::validateInRange: " + value + " is not in range of " + min + " and " + max + "!");
        }
    }

    public void validateInRange(final double value, final double min, final double max) {
        if (value <= min || value >= max) {
            throw new IllegalArgumentException("Validator::validateInRange: " + value + " is not in range of " + min + " and " + max + "!");
        }
    }
}
