package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.AttributeElement;
import de.dhbwka.swe.utils.gui.GUIConstants;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class BookingListComponent extends GUIComponent implements IGUIEventListener, TableCellRenderer {
    public record SearchInputChangedPayload(
            List<? extends IDepictable> allBookings,
            Optional<LocalDate> startDate,
            Optional<LocalDate> endDate
    ) {
    }

    public enum Commands implements EventCommand {
        // outgoing gui events
        BOOKING_SELECTED("BookingListComponent::BOOKING_SELECTED", IDepictable.class),
        BUTTON_PRESSED_SELECT_START_DATE("BookingListComponent::BUTTON_PRESSED_SELECT_START_DATE", Optional.class),
        BUTTON_PRESSED_SELECT_END_DATE("BookingListComponent::BUTTON_PRESSED_SELECT_END_DATE", Optional.class),
        SEARCH_INPUT_CHANGED("BookingListComponent::SEARCH_INPUT_CHANGED", SearchInputChangedPayload.class),
        // incoming update events
        SET_START_DATE("BookingListComponent::SET_START_DATE", LocalDate.class),
        SET_END_DATE("BookingListComponent::SET_END_DATE", LocalDate.class),
        RESET_SEARCH_INPUT("BookingListComponent::RESET_SEARCH_INPUT"),
        UPDATE_FILTERED_BOOKINGS("BookingListComponent::UPDATE_FILTERED_BOOKINGS", List.class),
        UPDATE_BOOKINGS("BookingListComponent::UPDATE_BOOKINGS", List.class);

        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText) {
            this(cmdText, Void.class);
        }

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
    private static final String SEARCH_START_DATE_ELEMENT_ID = "BookingListComponent::SEARCH_START_DATE_ELEMENT_ID";
    private static final String SEARCH_END_DATE_ELEMENT_ID = "BookingListComponent::SEARCH_END_DATE_ELEMENT_ID";

    // Data
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    private final Map<ImageElement, ImageIcon> cachedImages = new HashMap<>();
    private List<? extends IDepictable> allBookings = new ArrayList<>();
    private List<? extends IDepictable> filteredBookings = new ArrayList<>();
    private String[] columnNames = new String[0];

    // Components
    private SimpleTableComponent tableComponent;
    private AttributeElement startDateInput;
    private AttributeElement endDateInput;
    private JLabel emptyMessage;

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
        } else if (guiEvent.getSource() instanceof AttributeElement component &&
                component.getID().equals(SEARCH_START_DATE_ELEMENT_ID) &&
                guiEvent.getCmd() == AttributeElement.Commands.BUTTON_PRESSED) {
            final var startDate = tryOptional(() -> LocalDate.parse(component.getValueAsString(), this.dateTimeFormatter));
            this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_START_DATE, startDate));
        } else if (guiEvent.getSource() instanceof AttributeElement component &&
                component.getID().equals(SEARCH_END_DATE_ELEMENT_ID) &&
                guiEvent.getCmd() == AttributeElement.Commands.BUTTON_PRESSED) {
            final var endDate = tryOptional(() -> LocalDate.parse(component.getValueAsString(), this.dateTimeFormatter));
            this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_END_DATE, endDate));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmd() instanceof Commands command) {
            switch (command) {
                case RESET_SEARCH_INPUT -> {
                    this.startDateInput.setValue("");
                    this.endDateInput.setValue("");
                    this.fireSearchInputChanged();
                }
                case SET_START_DATE -> {
                    final var date = (LocalDate) updateEvent.getData();
                    this.startDateInput.setValue(date.format(this.dateTimeFormatter));
                    this.fireSearchInputChanged();
                }
                case SET_END_DATE -> {
                    final var date = (LocalDate) updateEvent.getData();
                    this.endDateInput.setValue(date.format(this.dateTimeFormatter));
                    this.fireSearchInputChanged();
                }
                case UPDATE_BOOKINGS -> {
                    this.allBookings = (List<? extends IDepictable>) updateEvent.getData();
                    if (this.columnNames.length == 0 && this.allBookings.size() > 0) {
                        this.columnNames = Arrays.stream(this.allBookings.get(0).getAttributeArray())
                                .map(Attribute::getName)
                                .toList()
                                .toArray(new String[0]);
                    }
                    this.fireSearchInputChanged();
                }
                case UPDATE_FILTERED_BOOKINGS -> {
                    this.filteredBookings = (List<? extends IDepictable>) updateEvent.getData();
                    if (this.filteredBookings.isEmpty()) {
                        this.tableComponent.setData(new IDepictable[0], this.columnNames);
                        this.emptyMessage.setText("Keine Buchungen mit den angegebenen Suchkriterien gefunden.");
                    } else {
                        this.tableComponent.setData(this.filteredBookings.toArray(new IDepictable[0]), this.columnNames);
                        this.emptyMessage.setText("");
                    }

                    this.removeListenersFromTable();
                    this.revalidate();
                }
            }
        }
    }

    private void fireSearchInputChanged() {
        final var startDate = tryOptional(() -> LocalDate.parse(
                this.startDateInput.getValueAsString(),
                this.dateTimeFormatter
        ));
        final var endDate = tryOptional(() -> LocalDate.parse(
                this.endDateInput.getValueAsString(),
                this.dateTimeFormatter
        ));
        this.fireGUIEvent(new GUIEvent(this, Commands.SEARCH_INPUT_CHANGED, new SearchInputChangedPayload(
                this.allBookings,
                startDate,
                endDate
        )));
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(this.config.getBackgroundColor());
        this.setOpaque(true);

        final var topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 3, 5, 5));
        topPanel.setBackground(this.config.getBackgroundColor());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setOpaque(true);

        this.startDateInput = AttributeElement.builder(SEARCH_START_DATE_ELEMENT_ID)
                .labelName("Buchungen anzeigen von")
                .toolTip("Angabe des Startdatums der Suche (Format: dd.MM.yyyy)")
                .textFieldFont(this.config.getFont())
                // label
                .labelSize(new Dimension(140, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(this.dateTimeFormatter)
                .allowedChars(AttributeElement.FormatType.DATE.getAllowedCharacterSet())
                // action button
                .data("Kalenderauswahl")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getAccentColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.startDateInput.addObserver(this);
        super.colorizeAttributeElement(this.startDateInput);
        ((JTextField) this.startDateInput.getComponent(1)).addActionListener(e -> this.fireSearchInputChanged());

        this.endDateInput = AttributeElement.builder(SEARCH_END_DATE_ELEMENT_ID)
                .labelName("bis")
                .toolTip("Angabe des Enddatums der Suche (Format: dd.MM.yyyy)")
                .textFieldFont(this.config.getFont())
                // label
                .labelSize(new Dimension(140, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(this.dateTimeFormatter)
                .allowedChars(AttributeElement.FormatType.DATE.getAllowedCharacterSet())
                // action button
                .data("Kalenderauswahl")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getAccentColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.endDateInput.addObserver(this);
        super.colorizeAttributeElement(this.endDateInput);
        ((JTextField) this.endDateInput.getComponent(1)).addActionListener(e -> this.fireSearchInputChanged());

        topPanel.add(super.createFillComponent());
        topPanel.add(this.startDateInput);
        topPanel.add(super.createFillComponent());
        topPanel.add(super.createFillComponent());
        topPanel.add(this.endDateInput);
        topPanel.add(super.createFillComponent());
        this.add(topPanel, BorderLayout.NORTH);

        final var panel = super.createFillComponent();
        panel.setLayout(new BorderLayout(10, 10));

        this.tableComponent = SimpleTableComponent
                .builder(BOOKING_SIMPLE_TABLE_COMPONENT_ID)
                .columnNames(this.columnNames)
                .cellRenderer(this, List.class, String.class, IDepictable.class, Optional.class)
                .build();
        super.colorizeTable(this.tableComponent);
        this.tableComponent.setData(this.filteredBookings.toArray(new IDepictable[0]), new String[0]);
        this.tableComponent.addObserver(this);
        panel.add(this.tableComponent, BorderLayout.CENTER);

        this.emptyMessage = new JLabel("");
        this.emptyMessage.setFont(this.config.getLargeFont());
        this.emptyMessage.setForeground(this.config.getTextColor());
        this.emptyMessage.setBackground(this.config.getBackgroundColor());
        this.emptyMessage.setOpaque(true);
        this.emptyMessage.setHorizontalAlignment(SwingConstants.CENTER);
        this.emptyMessage.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(this.emptyMessage, BorderLayout.NORTH);

        this.add(panel, BorderLayout.CENTER);
    }

    private void removeListenersFromTable() {
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
