package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.gui.*;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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

    protected void colorizeAttributeComponent(final AttributeComponent attributeComponent) {
        final var attributeElements = ((JComponent) attributeComponent.getComponent(0)).getComponents();
        for (final var element : attributeElements) {
            final var component = ((AttributeElement) element).getComponent(1);
            if (component instanceof JTextField textField) {
                textField.setBackground(this.config.getSecondaryBackgroundColor());
                textField.setForeground(this.config.getTextColor());
                textField.setFont(this.config.getFont());
                textField.setOpaque(true);
            }
        }
    }

    protected void colorizeTable(final SimpleTableComponent table) {
        table.setBackground(this.config.getBackgroundColor());
        table.setForeground(this.config.getTextColor());
        table.setFont(this.config.getFont());

        final var scrollPane = (JScrollPane) table.getComponent(0);
        scrollPane.setBackground(this.config.getBackgroundColor());
        scrollPane.setForeground(this.config.getTextColor());
        scrollPane.setFont(this.config.getFont());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        final var viewport = scrollPane.getViewport();
        viewport.setBackground(this.config.getBackgroundColor());
        viewport.setForeground(this.config.getTextColor());
        viewport.setFont(this.config.getFont());
        final var tableComponent = (JTable) viewport.getComponent(0);
        tableComponent.setRowHeight(75);
        tableComponent.setBackground(this.config.getBackgroundColor());
        tableComponent.setForeground(this.config.getTextColor());
        tableComponent.setFont(this.config.getFont());
        tableComponent.setSelectionBackground(this.config.getAccentColor());
        tableComponent.setBorder(BorderFactory.createEmptyBorder());
        final var tableHeader = tableComponent.getTableHeader();
        tableHeader.setBackground(this.config.getSecondaryBackgroundColor());
        tableHeader.setForeground(this.config.getTextColor());
        tableHeader.setFont(this.config.getFont());
        tableHeader.setBorder(BorderFactory.createEmptyBorder());
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
        wrapper.getComponent(1).setBackground(this.config.getBackgroundColor());
        wrapper.getComponent(1).setForeground(this.config.getTextColor());
        wrapper.setBorder(border);
        return wrapper;
    }

    protected void createEmptyMessage(final String message) {
        this.setLayout(new GridBagLayout());
        final var text = new JTextPane();
        text.setBackground(this.config.getBackgroundColor());
        text.setForeground(this.config.getTextColor());
        text.setEditable(false);
        text.setFocusable(false);
        text.setOpaque(true);
        text.setText(message);
        var doc = text.getStyledDocument();
        var styles = new SimpleAttributeSet();
        StyleConstants.setAlignment(styles, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(styles, this.config.getFontFamily());
        StyleConstants.setFontSize(styles, this.config.getHeaderFont().getSize());
        StyleConstants.setBold(styles, true);
        StyleConstants.setBackground(styles, this.config.getBackgroundColor());
        StyleConstants.setForeground(styles, this.config.getTextColor());
        doc.setParagraphAttributes(0, doc.getLength(), styles, false);
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());
        this.setOpaque(true);
        this.add(text);
    }

    protected JLabel createErrorLabel() {
        final var label = new JLabel();
        final var attributes = new HashMap<TextAttribute, Object>(this.config.getFont().getAttributes());
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(this.config.getFont());
        label.setFont(new Font(attributes));

        label.setForeground(this.config.getFailureColor());
        label.setBackground(this.config.getBackgroundColor());
        label.setOpaque(true);
        return label;
    }

    protected JPanel createErrorWrapper(final Component... errorLabels) {
        final var panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(this.config.getBackgroundColor());
        panel.setForeground(this.config.getTextColor());
        panel.setFont(this.config.getFont());
        panel.setOpaque(true);

        final var messagePanel = new JPanel();
        messagePanel.setBackground(this.config.getBackgroundColor());
        messagePanel.setForeground(this.config.getTextColor());
        messagePanel.setFont(this.config.getFont());
        messagePanel.setOpaque(true);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        messagePanel.setLayout(new GridLayout(errorLabels.length, 1, 0, 5));
        panel.add(messagePanel, BorderLayout.SOUTH);

        for (final var label : errorLabels) {
            messagePanel.add(label);
        }

        return panel;
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
        wrapper.setLayout(new BorderLayout());
        wrapper.add(embeddedComponent, BorderLayout.CENTER);
        return wrapper;
    }

    protected String generateRandomID() {
        return "%s::%s".formatted(this.getClass().getSimpleName(), UUID.randomUUID().toString());
    }

    protected <T> Optional<T> tryOptional(final Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (final Exception e) {
            // ignore exception
            return Optional.empty();
        }
    }
}
