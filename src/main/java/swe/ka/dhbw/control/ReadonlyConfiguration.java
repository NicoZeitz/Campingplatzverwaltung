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

    Color getAccentColor();

    Color getBackgroundColor();

    Color getSecondaryBackgroundColor();

    Color getTextColor();

    WindowLocation getWindowLocation(final String windowName);

    record WindowLocation(int x, int y, int width, int height, WindowState state) {
        public static final int DEFAULT_WINDOW_WIDTH = 800;
        public static final int DEFAULT_WINDOW_HEIGHT = 600;
        public static final int DEFAULT_WINDOW_X = getDefaultWindowX();
        public static final int DEFAULT_WINDOW_Y = getDefaultWindowY();
        public static final WindowState DEFAULT_WINDOW_STATE = WindowState.WINDOWED;

        public WindowLocation(final int x, final int y, final int width, final int height) {
            this(x, y, width, height, WindowState.WINDOWED);
        }

        private static int getDefaultWindowX() {
            final var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final var centerPoint = ge.getCenterPoint();
            return centerPoint.x - DEFAULT_WINDOW_WIDTH / 2;
        }

        private static int getDefaultWindowY() {
            final var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final var centerPoint = ge.getCenterPoint();
            return centerPoint.y - DEFAULT_WINDOW_HEIGHT / 2;
        }

        public static WindowLocation fromSerialized(final String serialization) {
            int x = DEFAULT_WINDOW_X;
            int y = DEFAULT_WINDOW_Y;
            int width = DEFAULT_WINDOW_WIDTH;
            int height = DEFAULT_WINDOW_HEIGHT;
            WindowState state = DEFAULT_WINDOW_STATE;

            final var entries = serialization.split(",");
            for (final var entry : entries) {
                final var keyValuePair = entry.split("=");
                if (keyValuePair.length != 2) {
                    continue;
                }

                final var key = keyValuePair[0];
                final var value = keyValuePair[1];

                if (key.equals("x")) {
                    x = Integer.parseInt(value);
                } else if (key.equals("y")) {
                    y = Integer.parseInt(value);
                } else if (key.equals("w")) {
                    width = Integer.parseInt(value);
                } else if (key.equals("h")) {
                    height = Integer.parseInt(value);
                } else if (key.equals("s")) {
                    state = WindowState.valueOf(value);
                }
            }

            return new WindowLocation(x, y, width, height, state);
        }
    }
}
