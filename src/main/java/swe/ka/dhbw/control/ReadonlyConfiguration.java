package swe.ka.dhbw.control;

import java.awt.*;

public interface ReadonlyConfiguration {
    Font getFont();

    int getFontSize();

    String getFontFamily();

    int getWindowX();

    int getWindowY();

    int getWindowWidth();

    int getWindowHeight();

    WindowState getWindowState();

    Color getAccentColor();

    public enum WindowState {
        WINDOWED,
        MAXIMIZED
    }
}
