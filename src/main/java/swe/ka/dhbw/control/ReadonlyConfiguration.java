package swe.ka.dhbw.control;

import java.awt.*;

public interface ReadonlyConfiguration {
    public enum WindowState {
        WINDOWED,
        MAXIMIZED
    }

    Font getFont();
    int getFontSize();
    String getFontFamily();
    int getWindowX();
    int getWindowY();
    int getWindowWidth();
    int getWindowHeight();
    WindowState getWindowState();
    Color getAccentColor();
}
