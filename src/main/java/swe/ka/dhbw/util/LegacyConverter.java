package swe.ka.dhbw.util;

import swe.ka.dhbw.model.Adresse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public final class LegacyConverter {
    private static LegacyConverter instance;

    private LegacyConverter() {
    }

    public synchronized static LegacyConverter getInstance() {
        if (instance == null) {
            instance = new LegacyConverter();
        }
        return instance;
    }

    /**
     * converts a string with a date in the old date format into a java date object
     *
     * @param data a String that should have the date in the format YYYY-MM-DD
     * @return a date object created from the date in the string
     */
    public LocalDate convertDate(final String data) {
        var parts = data.split("-");
        var year = Integer.parseInt(parts[0]);
        var month = Integer.parseInt(parts[1]) - 1; // date expects a month between 0-11
        var day = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
    }

    public Name convertName(final String data) {
        var parts = data.split(" ");
        return new Name(parts[0], parts[1]);
    }

    public Adresse convertAdress(final String data) {
        throw new RuntimeException("TODO: UNIMPLEMENTED");
    }

    public Optional<String> convertEmail(final String data) {
        try {
            Validator.getInstance().validateEmail(data);
            return Optional.of(data);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<String> convertPhoneNumber(final String data) {
        try {
            Validator.getInstance().validatePhoneNumber(data);
            return Optional.of(data);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public String convertStellplatzNumber(final String data) {
        var character = data.charAt(0);
        var number = Integer.parseInt(data.substring(1));
        return String.format("%c%03d", character, number);
    }

    public BigDecimal convertPrice(final String data) {
        var number = data.substring(0, data.length() - 2);
        return new BigDecimal(number.replace(',', '.'));
    }

    public record Name(String vorname, String nachname) {
    }
}
