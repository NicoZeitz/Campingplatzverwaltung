package swe.ka.dhbw.util;

import java.awt.*;

public record WindowLocation(int x, int y, int width, int height, State state) {
    public enum State {
        WINDOWED,
        MAXIMIZED
    }

    public static final int DEFAULT_WINDOW_WIDTH = 800;
    public static final int DEFAULT_WINDOW_HEIGHT = 600;
    public static final int DEFAULT_WINDOW_X = getDefaultWindowX();
    public static final int DEFAULT_WINDOW_Y = getDefaultWindowY();
    public static final State DEFAULT_WINDOW_STATE = State.WINDOWED;

    @SuppressWarnings("unused")
    public WindowLocation(final int x, final int y, final int width, final int height) {
        this(x, y, width, height, State.WINDOWED);
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
        State state = DEFAULT_WINDOW_STATE;

        final var entries = serialization.split(",");
        for (final var entry : entries) {
            final var keyValuePair = entry.split("=");
            if (keyValuePair.length != 2) {
                continue;
            }

            final var key = keyValuePair[0];
            final var value = keyValuePair[1];

            switch (key) {
                case "x" -> x = Integer.parseInt(value);
                case "y" -> y = Integer.parseInt(value);
                case "w" -> width = Integer.parseInt(value);
                case "h" -> height = Integer.parseInt(value);
                case "s" -> state = State.valueOf(value);
            }
        }

        return new WindowLocation(x, y, width, height, state);
    }

    public static WindowLocation from(final Window window) {
        return new WindowLocation(
                window.getX(),
                window.getY(),
                window.getWidth(),
                window.getHeight(),
                State.WINDOWED
        );
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public WindowLocation clone() {
        return new WindowLocation(this.x, this.y, this.width, this.height, this.state);
    }

    public WindowLocation withHeight(final int height) {
        return new WindowLocation(this.x, this.y, this.width, height, this.state);
    }

    @SuppressWarnings("unused")
    public WindowLocation withState(final State state) {
        return new WindowLocation(this.x, this.y, this.width, this.height, state);
    }

    public WindowLocation withWidth(final int width) {
        return new WindowLocation(this.x, this.y, width, this.height, this.state);
    }

    @SuppressWarnings("unused")
    public WindowLocation withX(final int x) {
        return new WindowLocation(x, this.y, this.width, this.height, this.state);
    }

    @SuppressWarnings("unused")
    public WindowLocation withY(final int y) {
        return new WindowLocation(this.x, y, this.width, this.height, this.state);
    }
}
