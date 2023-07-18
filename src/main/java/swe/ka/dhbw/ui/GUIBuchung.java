package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.components.BookingImportExportComponent;
import swe.ka.dhbw.ui.components.BookingListComponent;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUIBuchung extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        OPEN_TAB("openTab", TabPayload.class),
        CLOSE_TAB("closeTab", ObservableComponent.class);

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

    private final ReadonlyConfiguration config;
    private BookingOverviewComponent bookingOverview;
    private BookingListComponent bookingList;
    private JTabbedPane tabs;

    public GUIBuchung(final ReadonlyConfiguration config,
                      final List<? extends IDepictable> bookings,
                      final Map<LocalDate, List<? extends IDepictable>> appointments,
                      final LocalDate currentWeek) {
        super("GUIBuchung");
        this.config = config;
        this.initUI(bookings, appointments, currentWeek);
    }

    @Override
    public void processGUIEvent(GUIEvent ge) {
        fireGUIEvent(ge);
    }

    @Override
    public void processUpdateEvent(UpdateEvent updateEvent) {
        if (Arrays.stream(BookingOverviewComponent.Commands.values()).anyMatch(cmd -> cmd == updateEvent.getCmd())) {
            this.bookingOverview.processUpdateEvent(updateEvent);
        } else if (updateEvent.getCmd() == Commands.OPEN_TAB) {
            final var payload = (TabPayload) updateEvent.getData();

            // Tab existiert bereits, erneut öffnen
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
        }
    }

    private void initUI(
            final List<? extends IDepictable> bookings,
            final Map<LocalDate, List<? extends IDepictable>> appointments,
            final LocalDate currentWeek) {
        this.bookingOverview = new BookingOverviewComponent(appointments, currentWeek, this.config);
        this.bookingOverview.addObserver(this);

        this.bookingList = new BookingListComponent(this.config, bookings);
        this.bookingList.addObserver(this);

        UIManager.put("TabbedPane.selected", this.config.getAccentColor());
        UIManager.put("TabbedPane.borderColor", this.config.getAccentColor());
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(-1, -1, -1, -1));

        this.tabs = new JTabbedPane();
        this.tabs.setBackground(this.config.getBackgroundColor());
        this.tabs.setForeground(this.config.getTextColor());
        this.tabs.setOpaque(true);

        this.tabs.addTab("Terminübersicht", null, this.bookingOverview, "Zeigt die Buchungen übersichtlich in einem Kalendar an");
        this.tabs.setMnemonicAt(0, KeyEvent.VK_1);
        this.tabs.addTab("Buchungsliste", null, this.bookingList, "Zeigt die Buchungen in einer Liste an");
        this.tabs.setMnemonicAt(0, KeyEvent.VK_2);
        this.tabs.addTab("Buchung anlegen", null, new JPanel(), "Erstellt eine neue Buchung");
        this.tabs.setMnemonicAt(0, KeyEvent.VK_3);
        this.tabs.addTab("Buchung Import/Export", null, new BookingImportExportComponent(this.config), "Importiert/Exportiert Buchungen");
        this.tabs.setMnemonicAt(0, KeyEvent.VK_4);

        for (var i = 0; i < this.tabs.getTabCount(); ++i) {
            this.tabs.setBackgroundAt(i, this.config.getBackgroundColor());
            this.tabs.setForegroundAt(i, this.config.getTextColor());
        }

        this.setLayout(new GridLayout(1, 1));
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());
        this.setOpaque(true);
        this.add(this.tabs);
    }

    public record TabPayload(String tabName, ObservableComponent component, String tooltip, Optional<Integer> index) {
        public TabPayload(String tabName, ObservableComponent component, String tooltip) {
            this(tabName, component, tooltip, Optional.empty());
        }
    }
}
