package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.components.BookingCreateComponent;
import swe.ka.dhbw.ui.components.BookingImportExportComponent;
import swe.ka.dhbw.ui.components.BookingListComponent;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

public class GUIBuchung extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        OPEN_TAB("GUIBuchung::OPEN_TAB", TabPayload.class),
        CLOSE_TAB("GUIBuchung::CLOSE_TAB", ObservableComponent.class),
        SWITCH_TAB("GUIBuchung::SWITCH_TAB", Tabs.class);

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

    public enum Tabs {
        APPOINTMENT_OVERVIEW("Terminübersicht", "Zeigt die Buchungen übersichtlich in einem Kalendar an"),
        BOOKING_LIST("Buchungsliste", "Zeigt die Buchungen in einer Liste an"),
        BOOKING_CREATE("Buchung anlegen", "Erstellt eine neue Buchung"),
        BOOKING_IMPORT_EXPORT("Buchung Import/Export", "Importiert/Exportiert Buchungen");

        private final String name;
        private final String tooltip;

        Tabs(final String name, final String tooltip) {
            this.name = name;
            this.tooltip = tooltip;
        }

        public String getName() {
            return this.name;
        }

        public String getTooltip() {
            return this.tooltip;
        }
    }

    private BookingOverviewComponent bookingOverview;
    private BookingListComponent bookingList;
    private BookingCreateComponent bookingCreate;
    private JTabbedPane tabs;

    public GUIBuchung(final ReadonlyConfiguration config) {
        super("GUIBuchung", config);
        this.initUI();
    }

    public BookingOverviewComponent getBookingOverview() {
        return bookingOverview;
    }

    public BookingListComponent getBookingList() {
        return bookingList;
    }

    public BookingCreateComponent getBookingCreate() {
        return bookingCreate;
    }

    private GUIComponent getSelectedTab() {
        final var selectedTab = (JComponent) this.tabs.getSelectedComponent();
        if (selectedTab instanceof GUIComponent component) {
            return component;
        }
        return (GUIComponent) selectedTab.getComponent(0);
    }

    @Override
    public void processGUIEvent(GUIEvent guiEvent) {
        // delegate all events from child tabs
        fireGUIEvent(guiEvent);
    }

    @Override
    public void processUpdateEvent(UpdateEvent updateEvent) {
        if (updateEvent.getCmd() == Commands.OPEN_TAB) {
            final var payload = (TabPayload) updateEvent.getData();

            // Tab already exists, focus it
            final var index = this.tabs.indexOfTabComponent(payload.component());
            if (index != -1) {
                this.tabs.setSelectedIndex(index);
                this.tabs.setTitleAt(index, payload.tabName());
                this.tabs.setToolTipTextAt(index, payload.tooltip());
                return;
            }

            if (payload.index().isPresent()) {
                this.tabs.insertTab(payload.tabName(), null, payload.component(), payload.tooltip(), payload.index().get());
            } else {
                this.tabs.addTab(payload.tabName(), null, payload.component(), payload.tooltip());
            }
            this.tabs.setSelectedIndex(payload.index().orElse(this.tabs.getTabCount() - 1));
        } else if (updateEvent.getCmd() == Commands.CLOSE_TAB) {
            // TODO: Alternativ falls es einfacher ist mit Titel: this.tabs.indexOfTab("Title")
            final var index = this.tabs.indexOfTabComponent((ObservableComponent) updateEvent.getData());
            if (index != -1) {
                this.tabs.removeTabAt(index);
            }
        } else if (updateEvent.getCmd() == Commands.SWITCH_TAB) {
            final var index = this.tabs.indexOfTab(((Tabs) updateEvent.getData()).getName());
            if (index != -1) {
                this.tabs.setSelectedIndex(index);
            }
        }
        // send commands to specific tab
        else if (Arrays.stream(BookingOverviewComponent.Commands.values()).anyMatch(cmd -> cmd == updateEvent.getCmd())) {
            this.bookingOverview.processUpdateEvent(updateEvent);
        } else if (Arrays.stream(BookingCreateComponent.Commands.values()).anyMatch(cmd -> cmd == updateEvent.getCmd())) {
            this.bookingCreate.processUpdateEvent(updateEvent);
        } else if (Arrays.stream(BookingListComponent.Commands.values()).anyMatch(cmd -> cmd == updateEvent.getCmd())) {
            this.bookingList.processUpdateEvent(updateEvent);
        }
        // send unknown commands to all tabs
        else {
            this.bookingOverview.processUpdateEvent(updateEvent);
            this.bookingList.processUpdateEvent(updateEvent);
            this.bookingCreate.processUpdateEvent(updateEvent);
        }
    }

    private void initUI() {
        // create tab components
        this.bookingOverview = new BookingOverviewComponent(this.config);
        this.bookingOverview.addObserver(this);

        this.bookingList = new BookingListComponent(this.config);
        this.bookingList.addObserver(this);

        this.bookingCreate = new BookingCreateComponent(this.config);
        this.bookingCreate.addObserver(this);

        final var bookingImportExport = new BookingImportExportComponent(this.config);

        this.setLayout(new GridLayout(1, 1));
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());
        this.setOpaque(true);

        // add tabs
        UIManager.put("TabbedPane.selected", this.config.getAccentColor());
        UIManager.put("TabbedPane.borderColor", this.config.getAccentColor());
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(-1, -1, -1, -1));

        this.tabs = new JTabbedPane();
        this.tabs.setBackground(this.config.getBackgroundColor());
        this.tabs.setForeground(this.config.getTextColor());
        this.tabs.setOpaque(true);

        this.tabs.addTab(
                Tabs.APPOINTMENT_OVERVIEW.getName(),
                null,
                this.bookingOverview,
                Tabs.APPOINTMENT_OVERVIEW.getTooltip()
        );
        this.tabs.setMnemonicAt(0, KeyEvent.VK_1);
        this.tabs.addTab(
                Tabs.BOOKING_LIST.getName(),
                null,
                this.bookingList,
                Tabs.BOOKING_LIST.getTooltip()
        );
        this.tabs.setMnemonicAt(0, KeyEvent.VK_2);
        this.tabs.addTab(
                Tabs.BOOKING_CREATE.getName(),
                null,
                this.wrapInWrapper(this.bookingCreate),
                Tabs.BOOKING_CREATE.getTooltip()
        );
        this.tabs.setMnemonicAt(0, KeyEvent.VK_3);
        this.tabs.addTab(
                Tabs.BOOKING_IMPORT_EXPORT.getName(),
                null,
                this.wrapInWrapper(bookingImportExport),
                Tabs.BOOKING_IMPORT_EXPORT.getTooltip()
        );
        this.tabs.setMnemonicAt(0, KeyEvent.VK_4);

        for (var i = 0; i < this.tabs.getTabCount(); ++i) {
            this.tabs.setBackgroundAt(i, this.config.getBackgroundColor());
            this.tabs.setForegroundAt(i, this.config.getTextColor());
        }
        this.add(this.tabs);
    }

    private JPanel wrapInWrapper(final ObservableComponent component) {
        final var wrapper = new JPanel();
        wrapper.setLayout(new GridBagLayout());
        wrapper.setBackground(this.config.getBackgroundColor());
        wrapper.setForeground(this.config.getTextColor());
        wrapper.setOpaque(true);

        final var panel = new JPanel();
        panel.setMaximumSize(new Dimension(0, 0));
        panel.setSize(new Dimension(0, 0));
        panel.setBackground(null);
        panel.setOpaque(true);

        // @formatter:off
        wrapper.add(component, new GridBagConstraints(1, 1, 1, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        wrapper.add(panel, new GridBagConstraints(1, 0, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        // @formatter:on
        return wrapper;
    }

    public record TabPayload(String tabName, ObservableComponent component, String tooltip, Optional<Integer> index) {
        public TabPayload(String tabName, ObservableComponent component, String tooltip) {
            this(tabName, component, tooltip, Optional.empty());
        }
    }
}
