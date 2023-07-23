package swe.ka.dhbw.control;

import swe.ka.dhbw.util.WindowLocation;

import java.awt.*;

public interface ReadonlyConfiguration {
    int DEFAULT_FONT_SIZE = 11;
    String DEFAULT_FONT_FAMILY = "Tahoma";
    Color DEFAULT_ACCENT_COLOR = new Color(117, 201, 252);
    Color DEFAULT_SUCCESS_COLOR = new Color(172, 208, 146);
    Color DEFAULT_FAILURE_COLOR = new Color(195, 88, 87);
    /* LIGHT THEME */
    Color DEFAULT_TEXT_COLOR = new Color(0, 0, 0);
    Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255);
    Color DEFAULT_SECONDARY_BACKGROUND_COLOR = new Color(238, 238, 238);
    /* DARK THEME */
    Color DARK_DEFAULT_SECONDARY_BACKGROUND_COLOR = new Color(57, 57, 57);
    Color DARK_DEFAULT_BACKGROUND_COLOR = new Color(0, 0, 0);
    Color DARK_DEFAULT_TEXT_COLOR = new Color(255, 255, 255);

    Font getFont();

    Font getLargeFont();

    Font getSmallFont();

    Font getHeaderFont();

    int getFontSize();

    String getFontFamily();

    Color getAccentColor();

    Color getSecondaryAccentColor();

    Color getBackgroundColor();

    Color getSecondaryBackgroundColor();

    Color getTextColor();

    Color getSuccessColor();

    Color getFailureColor();

    WindowLocation getWindowLocation(final String windowName);
}
