package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.gui.*;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import javax.swing.table.JTableHeader;
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

    public JTable createTable(String[] columnNames) {
        // we need to create our own table as the SimpleTableComponent does not allow us to listen to events that happen inside the table
        // and we want to grow the table dynamically instead of using a scrollpane
        final var table = new JTable(new Object[0][0], columnNames);
        table.setFont(this.config.getFont());
        table.setForeground(this.config.getTextColor());
        table.setBackground(this.config.getBackgroundColor());
        table.setRowSelectionAllowed(false);
        final var tableHeader = new JTableHeader();
        tableHeader.setColumnModel(table.getColumnModel());
        tableHeader.setFont(this.config.getFont());
        tableHeader.setForeground(this.config.getTextColor());
        tableHeader.setBackground(this.config.getSecondaryBackgroundColor());
        table.setTableHeader(tableHeader);
        return table;
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
}
