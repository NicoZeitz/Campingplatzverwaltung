package swe.ka.dhbw.control;

import java.awt.*;
import java.util.Objects;

public class Configuration implements ReadonlyConfiguration {

    private static final int DEFAULT_FONT_SIZE = 11;
    private static final String DEFAULT_FONT_FAMILY = "Tahoma";
    private static final int DEFAULT_WINDOW_WIDTH = 800;
    private static final int DEFAULT_WINDOW_HEIGHT = 600;
    private static final int DEFAULT_WINDOW_X = 0;
    private static final int DEFAULT_WINDOW_Y = 0;
    private static final WindowState DEFAULT_WINDOW_STATE = WindowState.WINDOWED;
    private static final Color DEFAULT_ACCENT_COLOR = Color.GREEN;

    public static final class Builder {
        private int fontSize = DEFAULT_FONT_SIZE;
        private String fontFamily = DEFAULT_FONT_FAMILY;
        private int windowWidth = DEFAULT_WINDOW_WIDTH;
        private int windowHeight = DEFAULT_WINDOW_HEIGHT;
        private int windowX = DEFAULT_WINDOW_X;
        private int windowY = DEFAULT_WINDOW_Y;
        private ReadonlyConfiguration.WindowState windowState = DEFAULT_WINDOW_STATE;
        private Color accentColor = DEFAULT_ACCENT_COLOR;

        private Builder() {}

        public Configuration build() {
            return new Configuration(
                    this.fontSize,
                    this.fontFamily,
                    this.windowX,
                    this.windowY,
                    this.windowWidth,
                    this.windowHeight,
                    this.windowState,
                    this.accentColor
            );
        }

        public Builder fontSize(final int size) {
            if(size <= 0) {
                throw new IllegalArgumentException("Configuration with a font size of '" + size + "' is not allowed as it is not greater than 0");
            }
            this.fontSize = size;
            return this;
        }

        public Builder fontFamily(final String family) {
            this.fontFamily = family;
            return this;
        }

        public Builder windowPosition(final int x, final int y) {
            if(x <= 0) {
                throw new IllegalArgumentException("Configuration with a window x of '" + x + "' is not allowed as it is not greater than 0");
            }
            if(y <= 0) {
                throw new IllegalArgumentException("Configuration with a window y of '" + y + "' is not allowed as it is not greater than 0");
            }
            this.windowX = x;
            this.windowY = y;
            return this;
        }

        public Builder windowSize(final int width, final int height) {
            if(width <= 0) {
                throw new IllegalArgumentException("Configuration with a window width of '" + width + "' is not allowed as it is not greater than 0");
            }
            if(height <= 0) {
                throw new IllegalArgumentException("Configuration with a window height of '" + height + "' is not allowed as it is not greater than 0");
            }
            this.windowWidth = width;
            this.windowHeight = height;
            return this;
        }

        public Builder windowState(final ReadonlyConfiguration.WindowState state) {
            this.windowState = state;
            return this;
        }

        public Builder accentColor(final Color color) {
            this.accentColor = color;
            return this;
        }
    }

    private int fontSize;
    private String fontFamily;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private ReadonlyConfiguration.WindowState windowState;
    private Color accentColor;

    private Configuration(
            final int fontSize,
            final String fontFamily,
            final int windowX,
            final int windowY,
            final int windowWidth,
            final int windowHeight,
            final ReadonlyConfiguration.WindowState windowState,
            final Color accentColor
    ) {
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
        this.windowX = windowX;
        this.windowY = windowY;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowState = windowState;
        this.accentColor = accentColor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Font getFont() {
        return new Font(fontFamily, Font.PLAIN, fontSize);
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getWindowX() {
        return windowX;
    }

    public void setWindowX(int windowX) {
        this.windowX = windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public void setWindowY(int windowY) {
        this.windowY = windowY;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public ReadonlyConfiguration.WindowState getWindowState() {
        return windowState;
    }

    public void setWindowState(ReadonlyConfiguration.WindowState windowState) {
        this.windowState = windowState;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration that)) return false;
        return getFontSize() == that.getFontSize() && getWindowX() == that.getWindowX() && getWindowY() == that.getWindowY() && getWindowWidth() == that.getWindowWidth() && getWindowHeight() == that.getWindowHeight() && Objects.equals(getFontFamily(), that.getFontFamily()) && getWindowState() == that.getWindowState() && Objects.equals(getAccentColor(), that.getAccentColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFontSize(), getFontFamily(), getWindowX(), getWindowY(), getWindowWidth(), getWindowHeight(), getWindowState(), getAccentColor());
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "fontSize=" + fontSize +
                ", fontFamily='" + fontFamily + '\'' +
                ", windowX=" + windowX +
                ", windowY=" + windowY +
                ", windowWidth=" + windowWidth +
                ", windowHeight=" + windowHeight +
                ", windowState=" + windowState +
                ", accentColor=" + accentColor +
                '}';
    }
}
