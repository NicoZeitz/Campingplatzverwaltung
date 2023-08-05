package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.util.IPropertyManager;
import de.dhbwka.swe.utils.util.PropertyManager;
import swe.ka.dhbw.util.WindowLocation;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Configuration implements ReadonlyConfiguration {
    public static final class Builder {
        private final Map<String, WindowLocation> windowLocations = new HashMap<>();
        private int fontSize = DEFAULT_FONT_SIZE;
        private String fontFamily = DEFAULT_FONT_FAMILY;
        private Color textColor = DEFAULT_TEXT_COLOR;
        private Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
        private Color secondaryBackgroundColor = DEFAULT_SECONDARY_BACKGROUND_COLOR;
        private Color accentColor = DEFAULT_ACCENT_COLOR;
        private Color successColor = DEFAULT_SUCCESS_COLOR;
        private Color failureColor = DEFAULT_FAILURE_COLOR;
        private IPropertyManager propertyManager = new PropertyManager(System.getProperty("user.home") + File.separator + "configuration.properties",
                Configuration.class,
                "/configuration.properties");

        private Builder() throws Exception {
        }

        public Builder accentColor(final Color color) {
            this.accentColor = color;
            return this;
        }

        public Builder addProperties(Properties properties) {
            if (properties.containsKey("windows")) {
                final var serializedLocations = properties.getProperty("windows");
                for (final var serializedLocation : serializedLocations.split(";")) {
                    if (serializedLocation.isEmpty()) {
                        continue;
                    }

                    final var windowName = serializedLocation.split("\\.")[0];
                    final var windowLocation = WindowLocation.fromSerialized(serializedLocation.split("\\.")[1]);
                    this.windowLocations.put(windowName, windowLocation);
                }
            }
            return this;
        }

        public Builder backgroundColor(final Color color) {
            this.backgroundColor = color;
            return this;
        }

        public Configuration build() {
            return new Configuration(
                    this.fontSize,
                    this.fontFamily,
                    this.windowLocations,
                    this.textColor,
                    this.backgroundColor,
                    this.secondaryBackgroundColor,
                    this.accentColor,
                    this.successColor,
                    this.failureColor,
                    this.propertyManager
            );
        }

        public Builder darkMode() {
            return this
                    .textColor(DARK_DEFAULT_TEXT_COLOR)
                    .secondaryBackgroundColor(DARK_DEFAULT_SECONDARY_BACKGROUND_COLOR)
                    .backgroundColor(DARK_DEFAULT_BACKGROUND_COLOR);
        }

        public Builder failureColor(final Color color) {
            this.failureColor = color;
            return this;
        }

        public Builder font(final Font font) {
            this.fontFamily = font.getFamily();
            this.fontSize = font.getSize();
            return this;
        }

        public Builder fontFamily(final String family) {
            this.fontFamily = family;
            return this;
        }

        public Builder fontSize(final int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("Configuration with a font size of '" + size + "' is not allowed as it is not greater than 0");
            }
            this.fontSize = size;
            return this;
        }

        public Builder lightMode() {
            return this
                    .textColor(DEFAULT_TEXT_COLOR)
                    .secondaryBackgroundColor(DEFAULT_SECONDARY_BACKGROUND_COLOR)
                    .backgroundColor(DEFAULT_BACKGROUND_COLOR);
        }

        public Builder propertyManager(final PropertyManager propertyManager) {
            this.propertyManager = propertyManager;
            this.addProperties(propertyManager.getAllProperties());
            return this;
        }

        public Builder secondaryBackgroundColor(final Color color) {
            this.secondaryBackgroundColor = color;
            return this;
        }

        public Builder successColor(final Color color) {
            this.successColor = color;
            return this;
        }

        public Builder textColor(final Color color) {
            this.textColor = color;
            return this;
        }

        public Builder windowLocation(final String windowName, final WindowLocation windowLocation) {
            if (windowLocation.x() <= 0) {
                throw new IllegalArgumentException("Configuration with a window x of '" + windowLocation.x() + "' is not allowed as it is not greater than 0");
            }
            if (windowLocation.y() <= 0) {
                throw new IllegalArgumentException("Configuration with a window y of '" + windowLocation.y() + "' is not allowed as it is not greater than 0");
            }
            if (windowLocation.width() <= 0) {
                throw new IllegalArgumentException("Configuration with a window width of '" + windowLocation.width() + "' is not allowed as it is not greater than 0");
            }
            if (windowLocation.height() <= 0) {
                throw new IllegalArgumentException("Configuration with a window height of '" + windowLocation.height() + "' is not allowed as it is not greater than 0");
            }

            this.windowLocations.put(windowName, windowLocation);
            return this;
        }
    }

    private final IPropertyManager propertyManager;
    private final Map<String, WindowLocation> windowLocations;
    private int fontSize;
    private String fontFamily;
    private Color textColor;
    private Color backgroundColor;
    private Color secondaryBackgroundColor;
    private Color accentColor;
    private Color successColor;
    private Color failureColor;

    private Configuration(
            final int fontSize,
            final String fontFamily,
            final Map<String, WindowLocation> windowLocations,
            final Color textColor,
            final Color backgroundColor,
            final Color secondaryBackgroundColor,
            final Color accentColor,
            final Color successColor,
            final Color failureColor,
            final IPropertyManager propertyManager
    ) {
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
        this.windowLocations = windowLocations;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.secondaryBackgroundColor = secondaryBackgroundColor;
        this.accentColor = accentColor;
        this.successColor = successColor;
        this.failureColor = failureColor;
        this.propertyManager = propertyManager;
    }

    public HashMap<String, String> getProperties() {
        final var properties = new HashMap<String, String>();
        final var serializedLocations = this.windowLocations.entrySet()
                .stream()
                .map(entry -> {
                    final var windowName = entry.getKey();
                    final var windowLocation = entry.getValue();
                    return "%s.".formatted(windowName) +
                            "x=%d,".formatted(windowLocation.x()) +
                            "y=%d,".formatted(windowLocation.y()) +
                            "w=%d,".formatted(windowLocation.width()) +
                            "h=%d,".formatted(windowLocation.height()) +
                            "s=%s".formatted(windowLocation.state().toString());
                }).collect(Collectors.joining(";"));
        properties.put("windows", serializedLocations);
        return properties;
    }

    public Map<String, WindowLocation> getWindowLocations() {
        return this.windowLocations;
    }

    public static Builder builder() throws Exception {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration that)) return false;
        return this.getFontSize() == that.getFontSize() &&
                Objects.equals(this.getFontFamily(), that.getFontFamily()) &&
                Objects.equals(this.getTextColor(), that.getTextColor()) &&
                Objects.equals(this.getBackgroundColor(), that.getBackgroundColor()) &&
                Objects.equals(this.getSecondaryBackgroundColor(), that.getSecondaryBackgroundColor()) &&
                Objects.equals(this.getAccentColor(), that.getAccentColor()) &&
                Objects.equals(this.getSuccessColor(), that.getSuccessColor()) &&
                Objects.equals(this.getFailureColor(), that.getFailureColor());

    }

    public Color getAccentColor() {
        return this.accentColor;
    }

    public void setAccentColor(final Color accentColor) {
        this.accentColor = accentColor;
    }

    @Override
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public Color getFailureColor() {
        return this.failureColor;
    }

    public void setFailureColor(final Color failureColor) {
        this.failureColor = failureColor;
    }

    @Override
    public Font getFont() {
        return new Font(fontFamily, Font.PLAIN, fontSize);
    }

    @Override
    public String getFontFamily() {
        return this.fontFamily;
    }

    public void setFontFamily(final String fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(final int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public Font getHeaderFont() {
        return new Font(fontFamily, Font.BOLD, Math.round(fontSize * 1.75f));
    }

    @Override
    public Font getLargeFont() {
        return new Font(fontFamily, Font.PLAIN, Math.round(fontSize * 1.5f));
    }

    @Override
    public Color getSecondaryAccentColor() {
        final var hsbVals = new float[3];
        Color.RGBtoHSB(this.accentColor.getRed(), this.accentColor.getGreen(), this.accentColor.getBlue(), hsbVals);
        // hue rotate
        hsbVals[0] = (hsbVals[0] + 0.5f) % 1.0f;
        return new Color(Color.HSBtoRGB(hsbVals[0], hsbVals[1], hsbVals[2]));
    }

    @Override
    public Color getSecondaryBackgroundColor() {
        return this.secondaryBackgroundColor;
    }

    public void setSecondaryBackgroundColor(final Color secondaryBackgroundColor) {
        this.secondaryBackgroundColor = secondaryBackgroundColor;
    }

    @Override
    public Font getSmallFont() {
        return new Font(fontFamily, Font.PLAIN, Math.round(fontSize * 0.8f));
    }

    @Override
    public Color getSuccessColor() {
        return this.successColor;

    }

    public void setSuccessColor(final Color successColor) {
        this.successColor = successColor;
    }

    @Override
    public Color getTextColor() {
        return this.textColor;
    }

    public void setTextColor(final Color textColor) {
        this.textColor = textColor;
    }

    @Override
    public WindowLocation getWindowLocation(final String windowName) {
        return this.windowLocations.computeIfAbsent(windowName, k -> new WindowLocation(
                WindowLocation.DEFAULT_WINDOW_X,
                WindowLocation.DEFAULT_WINDOW_Y,
                WindowLocation.DEFAULT_WINDOW_WIDTH,
                WindowLocation.DEFAULT_WINDOW_HEIGHT,
                WindowLocation.DEFAULT_WINDOW_STATE
        ));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getFontSize(),
                this.getFontFamily(),
                this.getTextColor(),
                this.getBackgroundColor(),
                this.getSecondaryBackgroundColor(),
                this.getAccentColor(),
                this.getSuccessColor(),
                this.getFailureColor());
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "fontSize=" + this.getFontSize() +
                ", fontFamily='" + this.getFontFamily() + '\'' +
                ", textColor=" + this.getTextColor() +
                ", backgroundColor=" + this.getBackgroundColor() +
                ", secondaryBackgroundColor=" + this.getSecondaryBackgroundColor() +
                ", accentColor=" + this.getAccentColor() +
                ", successColor=" + this.getSuccessColor() +
                ", failureColor=" + this.getFailureColor() +
                ", windowLocations=" + this.getWindowLocations() +
                '}';
    }

    public void saveConfiguration() throws IOException {
        this.propertyManager.addProperties(this.getProperties());
        this.propertyManager.saveConfiguration(this.propertyManager.getPropertiesFileName(),
                Optional.of("Campingplatzverwaltung - Wolf & Zeitz Solutions"));
    }

    public void setWindowLocation(final String windowName, final WindowLocation windowLocation) {
        this.windowLocations.put(windowName, windowLocation);
    }
}
