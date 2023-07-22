package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.gui.ButtonComponent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.GUIConstants;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public abstract class GUIComponent extends ObservableComponent implements IUpdateEventListener {
    protected ReadonlyConfiguration config;

    public GUIComponent(final String id, final ReadonlyConfiguration config) {
        super(id);
        this.config = config;
    }

    public GUIComponent(final ReadonlyConfiguration config) {
        super();
        this.config = config;
    }

    protected ButtonElement createAddButton(final String id, final String tooltip) {
        final var addButton = ButtonElement.builder(id)
                .buttonText("+")
                .toolTip(tooltip)
                .font(this.config.getFont())
                .backgroundColor(this.config.getAccentColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(
                        GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue(),
                        GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue())
                )
                .build();
        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.addObserver(this);
        return addButton;
    }

    protected ButtonComponent createAddableWrapper(final String title,
                                                   final String wrapperId,
                                                   final String createButtonId,
                                                   final String createButtonTooltip,
                                                   final JComponent embeddedComponent) {
        final var wrapper = ButtonComponent.builder(wrapperId)
                .embeddedComponent(embeddedComponent)
                .title(title)
                .buttonElements(new ButtonElement[] {this.createAddButton(createButtonId, createButtonTooltip)})
                .position(ButtonComponent.Position.NORTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();
        final var border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getLargeFont());
        wrapper.setForeground(this.config.getTextColor());
        wrapper.setBackground(this.config.getBackgroundColor());
        wrapper.getComponents()[1].setBackground(this.config.getBackgroundColor());
        wrapper.getComponents()[1].setForeground(this.config.getTextColor());
        wrapper.setBorder(border);
        return wrapper;
    }

    protected JComponent createFillComponent() {
        final var fillComponent = new JPanel();
        fillComponent.setOpaque(true);
        fillComponent.setBackground(this.config.getBackgroundColor());
        fillComponent.setForeground(this.config.getTextColor());
        return fillComponent;
    }

    protected JComponent createWrapper(
            final String title,
            final JComponent embeddedComponent
    ) {
        final var wrapper = new JPanel();
        final var border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getLargeFont());
        wrapper.setBorder(border);
        wrapper.setBackground(this.config.getBackgroundColor());
        wrapper.setForeground(this.config.getTextColor());
        return wrapper;
    }

    protected String generateRandomID() {
        return "%s::%s".formatted(this.getClass().getSimpleName(), UUID.randomUUID().toString());
    }
}
