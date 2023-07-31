package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.SimpleTableComponent;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.ImageElement;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class BookingListComponent extends GUIComponent implements IGUIEventListener, TableCellRenderer {
    // Commands
    public enum Commands implements EventCommand {
        // outgoing gui events
        BOOKING_SELECTED("BookingListComponent::BOOKING_SELECTED", IDepictable.class),
        // incoming update events
        UPDATE_BOOKINGS("BookingListComponent::UPDATE_BOOKINGS", List.class);

        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText, final Class<?> payloadType) {
            this.cmdText = cmdText;
            this.payloadType = payloadType;
        }

        @Override
        public String getCmdText() {
            return this.cmdText;
        }

        @Override
        public Class<?> getPayloadType() {
            return this.payloadType;
        }
    }

    // UI IDs
    private static final String BOOKING_SIMPLE_TABLE_COMPONENT_ID = "BookingListComponent::BOOKING_SIMPLE_TABLE_COMPONENT_ID";

    // Data
    private final Map<ImageElement, ImageIcon> cachedImages = new HashMap<>();
    private List<? extends IDepictable> bookings = new ArrayList<>();

    // Components
    private SimpleTableComponent tableComponent;

    public BookingListComponent(final ReadonlyConfiguration config) {
        super("BookingListComponent", config);
        this.initUI();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row,
                                                   final int column) {
        if (value instanceof Collection<?> collection && collection.size() > 0 && collection.stream()
                .findAny()
                .get() instanceof ImageElement) {
            final var cell = new JPanel();
            cell.setLayout(new FlowLayout());
            cell.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            cell.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            cell.setFont(table.getFont());
            cell.setOpaque(true);
            for (final var image : collection) {
                // cache resized images to avoid resizing them on every render
                final var img = this.cachedImages.computeIfAbsent((ImageElement) image,
                        i -> new ImageIcon(i.getBaseImage()
                                .getScaledInstance(table.getRowHeight(), table.getRowHeight(), Image.SCALE_DEFAULT)));
                cell.add(new JLabel(img));
            }
            return cell;
        }

        var text = "";
        if (value != null) {
            text = value.toString();

            if (value instanceof IDepictable depictable) {
                text = depictable.getVisibleText();
            } else if (value instanceof Optional<?> optional) {
                if (optional.isPresent() && optional.get() instanceof IDepictable depictable) {
                    text = depictable.getVisibleText();
                } else if (optional.isPresent()) {
                    text = optional.toString();
                } else {
                    text = "-";
                }
            } else if (value instanceof Collection<?> collection) {
                if (collection.size() == 0) {
                    text = "";
                } else if (collection.stream()
                        .findAny()
                        .get() instanceof IDepictable) {
                    text = ((Collection<IDepictable>) collection).stream().map(IDepictable::getVisibleText).collect(Collectors.joining(", "));
                } else {
                    text = collection.stream().map(Object::toString).collect(Collectors.joining(", "));
                }
            }
        }

        final var cell = new JPanel();
        cell.setLayout(new GridLayout(1, 1));
        cell.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        cell.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        cell.setFont(table.getFont());
        cell.setOpaque(true);


        final var textComponent = new JTextPane();
        textComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        textComponent.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        textComponent.setFont(table.getFont());
        textComponent.setOpaque(true);
        textComponent.setEditable(false);
        textComponent.setFocusable(false);
        textComponent.setText(text);
        var doc = textComponent.getStyledDocument();
        var styles = new SimpleAttributeSet();
        StyleConstants.setAlignment(styles, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(styles, this.config.getFontFamily());
        StyleConstants.setFontSize(styles, this.config.getFontSize());
        doc.setParagraphAttributes(0, doc.getLength(), styles, false);
        cell.add(textComponent);
        return cell;
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getCmd() == SimpleTableComponent.Commands.ROW_SELECTED) {
            final var scrollPane = (JScrollPane) this.tableComponent.getComponent(0);
            final var viewport = (JViewport) scrollPane.getComponent(0);
            final var table = (JTable) viewport.getComponent(0);
            table.getSelectionModel().clearSelection();
            this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BOOKING_SELECTED,
                    guiEvent.getData()
            ));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmd() == Commands.UPDATE_BOOKINGS) {
            this.bookings = (List<? extends IDepictable>) updateEvent.getData();
            if (this.bookings.isEmpty()) {
                this.tableComponent.setData(new IDepictable[0], new String[0]);
                return;
            }

            final var columnNames = Arrays.stream(this.bookings.get(0).getAttributeArray())
                    .map(Attribute::getName)
                    .toList()
                    .toArray(new String[0]);
            this.tableComponent.setData(this.bookings.toArray(new IDepictable[0]), columnNames);

            // BUG:SWE-UTILS: The SimpleTableComponent adds a new listener every time the data is set. This results in +1 events per update
            // This happens because the SimpleTableComponent::setData method calls SimpleTableComponent::initTable which adds a new listener
            // The following code removes the listener and adds it again to avoid this
            final var scrollPane = (JScrollPane) this.tableComponent.getComponent(0);
            final var viewport = (JViewport) scrollPane.getComponent(0);
            final var table = (JTable) viewport.getComponent(0);
            final var selectionMode = (DefaultListSelectionModel) table.getSelectionModel();
            final var listeners = selectionMode.getListSelectionListeners();
            var listenerToAddAgain = (ListSelectionListener) null;
            for (final var listener : listeners) {
                if (listener.getClass().getEnclosingClass() == SimpleTableComponent.class) {
                    selectionMode.removeListSelectionListener(listener);
                    listenerToAddAgain = listener;
                }
            }
            selectionMode.addListSelectionListener(listenerToAddAgain);
        }
    }

    private void initUI() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(this.config.getBackgroundColor());
        this.setOpaque(true);

        this.tableComponent = SimpleTableComponent
                .builder(BOOKING_SIMPLE_TABLE_COMPONENT_ID)
                .columnNames(new String[0])
                .cellRenderer(this, List.class, String.class, IDepictable.class, Optional.class)
                .build();
        super.colorizeTable(this.tableComponent);
        this.tableComponent.setData(this.bookings.toArray(new IDepictable[0]), new String[0]);
        this.tableComponent.addObserver(this);
        this.add(this.tableComponent, BorderLayout.CENTER);
    }
}
