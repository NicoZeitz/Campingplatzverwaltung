package swe.ka.dhbw.control;

import java.awt.*;

public interface ReadonlyConfiguration {
    enum WindowState {
        WINDOWED,
        MAXIMIZED
    }

    Font getFont();

    Font getLargeFont();

    Font getSmallFont();

    Font getHeaderFont();

    int getFontSize();

    String getFontFamily();

    int getWindowX();

    int getWindowY();

    int getWindowWidth();

    int getWindowHeight();

    WindowState getWindowState();

    Color getAccentColor();
}
