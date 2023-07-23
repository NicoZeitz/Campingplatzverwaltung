package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.*;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

public class BookingCreateComponent extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        ADD_GUEST("BookingCreateComponent::ADD_GUEST"),
        ADD_SERVICE("BookingCreateComponent::ADD_SERVICE"),
        ADD_EQUIPMENT("BookingCreateComponent::ADD_EQUIPMENT"),
        SELECT_START_DATE("BookingCreateComponent::SELECT_START_DATE", Optional.class),
        SELECT_END_DATE("BookingCreateComponent::SELECT_END_DATE", Optional.class),
        SELECT_CHIPCARD("BookingCreateComponent::SELECT_CHIPCARD", SelectChipkartePayload.class),
        DELETE_CHIPCARD("BookingCreateComponent::DELETE_CHIPCARD", DeleteChipkartePayload.class),
        RESET("BookingCreateComponent::RESET"),
        CREATE_BOOKING("BookingCreateComponent::CREATE_BOOKING");

        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText) {
            this(cmdText, null);
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

    private static final String BUTTON_COMPONENT_ID = "BookingCreateComponent::BUTTON_COMPONENT_ID";
    private static final String CREATE_BOOKING_BUTTON_ELEMENT_ID = "BookingCreateComponent::CREATE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID = "BookingCreateComponent::CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String ADD_GAST_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_GAST_BUTTON_ELEMENT_ID";
    private static final String ADD_LEISTUNG_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_LEISTUNG_BUTTON_ELEMENT_ID";
    private static final String ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID";
    private static final String BUCHUNGSZEITRAUM_VON_ATTRIBUTE_ELEMENT_ID = "BookingCreateComponent::BUCHUNGSZEITRAUM_VON_ATTRIBUTE_ELEMENT_ID";
    private static final String BUCHUNGSZEITRAUM_BIS_ATTRIBUTE_ELEMENT_ID = "BookingCreateComponent::BUCHUNGSZEITRAUM_BIS_ATTRIBUTE_ELEMENT_ID";
    private static final String CHIPKARTE_ATTRIBUTE_ELEMENT_ID = "BookingCreateComponent::CHIPKARTE_ATTRIBUTE_ELEMENT_ID";
    private static final String CHIPKARTE_SIMPLE_TABLE_COMPONENT_ID = "BookingCreateComponent::CHIPKARTE_SIMPLE_TABLE_COMPONENT_ID";
    private final DateTimeFormatter dateTimeFormatter;
    private AttributeElement anreisedatum;
    private AttributeElement abreisedatum;
    private AttributeElement chipkartenSelector;
    private SimpleTableComponent chipkartenTable;
    private List<? extends IDepictable> availableChipkarten;
    private List<? extends IDepictable> selectedChipkarten = new ArrayList<>();

    public BookingCreateComponent(final ReadonlyConfiguration config, final List<? extends IDepictable> availableChipkarten) {
        super("BookingCreateComponent", config);
        this.dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm").toFormatter(Locale.GERMANY);
        this.availableChipkarten = availableChipkarten;
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent ge) {
        if (ge.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case ADD_GAST_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_GUEST));
                case ADD_LEISTUNG_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_SERVICE));
                case ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_EQUIPMENT));
                case BUCHUNGSZEITRAUM_VON_ATTRIBUTE_ELEMENT_ID -> {
                    Optional<LocalDateTime> value = Optional.empty();
                    try {
                        value = Optional.of(LocalDateTime.parse(((AttributeElement) ge.getData()).getValueAsString(), dateTimeFormatter));
                    } catch (DateTimeParseException e) {
                        // Fehler ignorieren
                    }
                    this.fireGUIEvent(new GUIEvent(this, Commands.SELECT_START_DATE, value));

                }
                case BUCHUNGSZEITRAUM_BIS_ATTRIBUTE_ELEMENT_ID -> {
                    Optional<LocalDateTime> value = Optional.empty();
                    try {
                        value = Optional.of(LocalDateTime.parse(((AttributeElement) ge.getData()).getValueAsString(), dateTimeFormatter));
                    } catch (DateTimeParseException e) {
                        // Fehler ignorieren
                    }
                    this.fireGUIEvent(new GUIEvent(this, Commands.SELECT_END_DATE, value));
                }
                case CHIPKARTE_ATTRIBUTE_ELEMENT_ID -> {
                    final var value = ((AttributeElement) ge.getData()).getValue();
                    if (value instanceof String str && str.isEmpty()) {
                        return;
                    }

                    this.fireGUIEvent(new GUIEvent(this, Commands.SELECT_CHIPCARD, new SelectChipkartePayload(
                            this.availableChipkarten,
                            this.selectedChipkarten,
                            value
                    )));
                }
                case CHIPKARTE_SIMPLE_TABLE_COMPONENT_ID -> {
                    final var scrollPane = (JScrollPane) this.chipkartenTable.getComponent(0);
                    final var viewport = scrollPane.getViewport();
                    final var tableComponent = (JTable) viewport.getComponent(0);
                    if (tableComponent.getSelectedColumn() != 2 || ge.getCmd() != SimpleTableComponent.Commands.ROW_SELECTED) {
                        return;
                    }
                    final var chipkarte = this.selectedChipkarten.stream()
                            .filter(c -> c.getElementID().equals(((IDepictable) ge.getData()).getElementID()))
                            .findFirst();
                    if (chipkarte.isPresent()) {
                        this.fireGUIEvent(new GUIEvent(this, Commands.DELETE_CHIPCARD, new DeleteChipkartePayload(
                                this.availableChipkarten,
                                this.selectedChipkarten,
                                chipkarte.get()
                        )));
                    }
                }
                case CREATE_BOOKING_BUTTON_ELEMENT_ID -> {
                    this.fireGUIEvent(new GUIEvent(this, Commands.CREATE_BOOKING, BookingCreatePayload.create(this)));
                }
                case CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID -> {
                    this.fireGUIEvent(new GUIEvent(this, Commands.RESET));
                }
            }
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {
        if (ue.getCmdText().equals(Commands.SELECT_START_DATE.getCmdText())) {
            final var date = LocalDateTime.of((LocalDate) ue.getData(), LocalTime.of(0, 0));
            this.anreisedatum.setValue(date.format(this.dateTimeFormatter));
        } else if (ue.getCmdText().equals(Commands.SELECT_END_DATE.getCmdText())) {
            final var date = LocalDateTime.of((LocalDate) ue.getData(), LocalTime.of(23, 59));
            this.abreisedatum.setValue(date.format(this.dateTimeFormatter));
        } else if (ue.getCmd() == Commands.RESET) {
            this.resetInput();
        } else if (ue.getCmd() == Commands.SELECT_CHIPCARD) {
            final var payload = (SelectChipkartePayload) ue.getData();
            this.availableChipkarten = payload.availableChipkarten();
            this.selectedChipkarten = payload.selectedChipkarten();

            this.chipkartenSelector.setData(Stream.concat(Stream.of(""), this.availableChipkarten.stream()).toArray(Object[]::new));
            this.chipkartenSelector.setValue(payload.selectedChipkarte());
            if (this.availableChipkarten.size() == 0) this.chipkartenSelector.setEnabled(false);
            this.reloadChipkarten();
        }
    }

    @SuppressWarnings("unchecked")
    public void resetInput() {
        this.anreisedatum.setValue("");
        this.abreisedatum.setValue("");
        this.availableChipkarten = Stream.concat(this.availableChipkarten.stream(), this.selectedChipkarten.stream()).sorted().toList();
        this.selectedChipkarten.clear();
        this.reloadChipkarten();
        this.repaint();
    }

    private JComponent createAusruestungTable() {
        return new JPanel();
    }

    private JComponent createBuchungszeitraum() {
        this.anreisedatum = AttributeElement
                .builder(BUCHUNGSZEITRAUM_VON_ATTRIBUTE_ELEMENT_ID)
                .labelName("Anreisedatum")
                .toolTip("Angabe des Anreisedatums der Buchung (Format: dd.MM.yyyy HH:mm)")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(dateTimeFormatter)
                .allowedChars(AttributeElement.FormatType.DATETIME.getAllowedCharacterSet())
                // action button
                .data("Datum auswählen")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.anreisedatum.addObserver(this);

        this.abreisedatum = AttributeElement
                .builder(BUCHUNGSZEITRAUM_BIS_ATTRIBUTE_ELEMENT_ID)
                .labelName("Abreisedatum")
                .toolTip("Angabe des Abreisedatums der Buchung (Format: dd.MM.yyyy HH:mm)")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(dateTimeFormatter)
                .allowedChars(AttributeElement.FormatType.DATETIME.getAllowedCharacterSet())
                // action button
                .data("Datum auswählen")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.abreisedatum.addObserver(this);

        final var panel = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {this.anreisedatum, this.abreisedatum})
                .build();
        panel.setBackground(this.config.getBackgroundColor());
        panel.setForeground(this.config.getTextColor());
        panel.setFont(this.config.getFont());
        panel.getComponent(0).setBackground(this.config.getBackgroundColor());
        panel.getComponent(0).setForeground(this.config.getTextColor());
        panel.getComponent(0).setFont(this.config.getFont());
        return panel;
    }

    private JComponent createChipkartenAuswahl() {
        final var panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.getInsets().set(10, 10, 10, 10);
        panel.setOpaque(true);
        panel.setBackground(this.config.getBackgroundColor());
        panel.setForeground(this.config.getTextColor());

        this.chipkartenSelector = AttributeElement.builder(CHIPKARTE_ATTRIBUTE_ELEMENT_ID)
                .labelName("Neue Karte")
                .toolTip("Auswahl der Chipkarten")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .formatType(AttributeElement.FormatType.TEXT)
                .autoformat()
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                // action button
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.COMBOBOX)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.chipkartenSelector.addObserver(this);

        final var selectorComponent = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {this.chipkartenSelector})
                .build();

        selectorComponent.setFont(this.config.getFont());
        selectorComponent.setForeground(this.config.getTextColor());
        selectorComponent.setBackground(this.config.getBackgroundColor());
        selectorComponent.getComponent(0).setBackground(this.config.getBackgroundColor());
        selectorComponent.getComponent(0).setForeground(this.config.getTextColor());
        selectorComponent.getComponent(0).setFont(this.config.getFont());
        panel.add(selectorComponent,
                new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        this.chipkartenTable = SimpleTableComponent.builder(CHIPKARTE_SIMPLE_TABLE_COMPONENT_ID)
                .columnNames(new String[] {"Nummer", "Status", ""})
                .preferredScrollableViewportSize(new Dimension(500, 100))
                .cellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                    if (column == 2) {
                        final var button = new JButton();
                        button.setText("Löschen");
                        button.setFont(this.config.getFont());
                        button.setForeground(this.config.getTextColor());
                        button.setBackground(this.config.getFailureColor());
                        button.setOpaque(true);
                        return button;
                    }

                    return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }, Object.class)
                .build();
        this.chipkartenTable.addObserver(this);
        super.colorizeTable(this.chipkartenTable);
        panel.add(this.chipkartenTable,
                new GridBagConstraints(0, 1, 1, 1, 1d, 1d, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        final var table = super.createTable(new String[] {"Nummer", "Status", ""});
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        final var scrollPane = new JScrollPane(table);
        panel.add(scrollPane,
                new GridBagConstraints(0, 1, 1, 1, 1d, 1d, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        this.resetInput();
        return panel;
    }

    private JComponent createGastTable() {
        return new JPanel();
    }

    private JComponent createLeistungTable() {
        return new JPanel();
    }

    private JComponent createStellplatzauswahl() {
        return new JPanel();
    }

    private void initUI() {
        this.setLayout(new GridLayout(1, 1));
        this.setOpaque(true);
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());

        final var mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.setOpaque(true);
        mainPanel.setBackground(this.config.getBackgroundColor());
        mainPanel.setForeground(this.config.getTextColor());
        final var leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setOpaque(true);
        leftPanel.setBackground(this.config.getBackgroundColor());
        leftPanel.setForeground(this.config.getTextColor());
        mainPanel.add(leftPanel);
        final var rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(this.config.getBackgroundColor());
        rightPanel.setForeground(this.config.getTextColor());
        mainPanel.add(rightPanel);

        // @formatter:off
        leftPanel.add(super.createAddableWrapper(
                "Gäste auswählen",
                super.generateRandomID(),
                ADD_GAST_BUTTON_ELEMENT_ID,
                "Fügt einen neuen Gast hinzu",
                this.createGastTable()
        ), new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createAddableWrapper(
                "Leistungen auswählen",
                super.generateRandomID(),
                ADD_LEISTUNG_BUTTON_ELEMENT_ID,
                "Fügt eine neue gebuchte Leistung hinzu",
                this.createLeistungTable()
        ), new GridBagConstraints(1, 2, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createAddableWrapper(
                "Mitgebrachte Ausrüstung auswählen",
                super.generateRandomID(),
                ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID,
                "Fügt eine neue mitgebrachte Ausrüstung hinzu",
                this.createAusruestungTable()
        ), new GridBagConstraints(1, 3, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createFillComponent(), new GridBagConstraints(1, 4, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        rightPanel.add(super.createWrapper("Buchungszeitraum", this.createBuchungszeitraum()),   new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createWrapper("Stellplatzauswahl", this.createStellplatzauswahl()), new GridBagConstraints(1, 2, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createWrapper("Chipkartenauswahl", this.createChipkartenAuswahl()), new GridBagConstraints(1, 3, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createFillComponent(),                                              new GridBagConstraints(1, 4, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        // @formatter:on

        final var cancelButton = ButtonElement.builder(CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID)
                .buttonText("Erstellen abbrechen")
                .font(this.config.getFont())
                .backgroundColor(this.config.getBackgroundColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Bricht den Vorgang ab")
                .build();
        cancelButton.addObserver(this);

        final var createButton = ButtonElement.builder(CREATE_BOOKING_BUTTON_ELEMENT_ID)
                .buttonText("Buchung erstellen")
                .font(this.config.getFont())
                .backgroundColor(this.config.getAccentColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Erstellt die neue Buchung mit den eingegebenen Daten")
                .build();
        createButton.addObserver(this);

        final var buttonComponent = ButtonComponent.builder(BUTTON_COMPONENT_ID)
                .embeddedComponent(mainPanel)
                .buttonElements(new ButtonElement[] {cancelButton, createButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();

        // TODO: why is the border background blue???
        final var border = BorderFactory.createTitledBorder("Neue Buchung anlegen");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        buttonComponent.setForeground(this.config.getTextColor());
        buttonComponent.setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponent(0).setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponent(0).setForeground(this.config.getTextColor());
        buttonComponent.getComponent(1).setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponent(1).setForeground(this.config.getTextColor());
        buttonComponent.setBorder(border);

        this.add(buttonComponent);
    }

    private void reloadChipkarten() {
        this.chipkartenSelector.setData(Stream.concat(
                Stream.of(""),
                this.availableChipkarten.stream()
        ).toArray(Object[]::new));
        this.chipkartenSelector.setValue("");
        this.chipkartenTable.setMinimumSize(new Dimension(0, 100));
        this.chipkartenTable.setData(this.selectedChipkarten.stream().map(c ->
                new IDepictable() {
                    @Override
                    public Attribute[] getAttributeArray() {
                        final var superAttributes = c.getAttributeArray();
                        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 1);
                        attributes[superAttributes.length] = new Attribute("",
                                c,
                                Object.class,
                                null,
                                null,
                                true,
                                false,
                                false,
                                true
                        );
                        return attributes;
                    }

                    @Override
                    public String getElementID() {
                        return c.getElementID();
                    }
                }).toArray(IDepictable[]::new), new String[] {"Nummer", "Status", ""});
        this.revalidate();
    }

    public record SelectChipkartePayload(List<? extends IDepictable> availableChipkarten, List<? extends IDepictable> selectedChipkarten,
                                         Object selectedChipkarte) {
    }

    public record DeleteChipkartePayload(List<? extends IDepictable> availableChipkarten, List<? extends IDepictable> selectedChipkarten,
                                         IDepictable deletedChipkarte) {
    }

    public record BookingCreatePayload(
            Optional<LocalDateTime> anreisedatum,
            Optional<LocalDateTime> abreisedatum,
            List<? extends IDepictable> chipkarten
    ) {
        public static BookingCreatePayload create(final BookingCreateComponent component) {
            Optional<LocalDateTime> anreisedatum = Optional.empty();
            try {
                anreisedatum = Optional.of(LocalDateTime.parse(component.anreisedatum.getValueAsString(), component.dateTimeFormatter));
            } catch (DateTimeParseException e) {
                // Fehler ignorieren
            }

            Optional<LocalDateTime> abreisedatum = Optional.empty();
            try {
                abreisedatum = Optional.of(LocalDateTime.parse(component.abreisedatum.getValueAsString(), component.dateTimeFormatter));
            } catch (DateTimeParseException e) {
                // Fehler ignorieren
            }

            return new BookingCreatePayload(anreisedatum, abreisedatum, component.selectedChipkarten);
        }
    }
}
