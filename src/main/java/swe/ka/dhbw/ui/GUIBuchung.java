package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.*;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.components.BookingChangeComponent;
import swe.ka.dhbw.ui.components.BookingImportExportComponent;
import swe.ka.dhbw.ui.components.BookingListComponent;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Optional;

public class GUIBuchung extends GUIComponent implements IGUIEventListener {
    public record TabPayload(String tabName, ObservableComponent component, String tooltip, Optional<Integer> index) {
        public TabPayload(String tabName, ObservableComponent component, String tooltip) {
            this(tabName, component, tooltip, Optional.empty());
        }
    }

    public record SendEventToTabPayload(Object tab, UpdateEvent event) {
    }

    public enum Commands implements EventCommand {
        OPEN_TAB("GUIBuchung::OPEN_TAB", TabPayload.class),
        CLOSE_TAB("GUIBuchung::CLOSE_TAB", Object.class),
        SWITCH_TAB("GUIBuchung::SWITCH_TAB", Object.class),
        BUTTON_PRESSED_TAB_CLOSING("GUIBuchung::BUTTON_PRESSED_TAB_CLOSING"),
        SEND_EVENT_TO_TAB("GUIBuchung::SEND_EVENT_TO_TAB", SendEventToTabPayload.class);

        public final Class<?> payloadType;
        public final String cmdText;

        @SuppressWarnings("SameParameterValue")
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
    private BookingChangeComponent bookingCreate;
    private JTabbedPane tabs;

    public GUIBuchung(final ReadonlyConfiguration config) {
        super("GUIBuchung", config);
        this.initUI();
    }

    @SuppressWarnings("unused")
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
        if (updateEvent.getCmd() instanceof Commands command) {
            switch (command) {
                case OPEN_TAB -> {
                    final var payload = (TabPayload) updateEvent.getData();

                    // Tab already exists, focus it
                    final var tabIndex = this.getTabIndex(payload.component);
                    if (tabIndex.isPresent()) {
                        this.tabs.setSelectedIndex(tabIndex.get());
                        this.tabs.setTitleAt(tabIndex.get(), payload.tabName());
                        this.tabs.setToolTipTextAt(tabIndex.get(), payload.tooltip());
                        return;
                    }

                    final var index = (int) payload.index().orElse(this.tabs.getTabCount());

                    this.tabs.insertTab(payload.tabName(), null, this.wrapInWrapper(payload.component()), payload.tooltip(), index);
                    this.tabs.setTabComponentAt(index, this.createClosableTab(e -> {
                        if (payload.component() instanceof IGUIEventListener listener) {
                            listener.processGUIEvent(new GUIEvent(
                                    this,
                                    Commands.BUTTON_PRESSED_TAB_CLOSING
                            ));
                        }
                    }));
                    this.tabs.setSelectedIndex(index);
                }
                case CLOSE_TAB -> this.getTabIndex(updateEvent.getData()).ifPresent(this.tabs::removeTabAt);
                case SWITCH_TAB -> this.getTabIndex(updateEvent.getData()).ifPresent(index -> {
                    this.tabs.setSelectedIndex(index);
                    this.revalidate();
                    this.repaint();
                });
                case SEND_EVENT_TO_TAB -> {
                    final var payload = (SendEventToTabPayload) updateEvent.getData();
                    final var tabIndex = this.getTabIndex(payload.tab());
                    if (tabIndex.isPresent()) {
                        final var tab = (JComponent) this.tabs.getComponentAt(tabIndex.get());
                        if (tab instanceof IUpdateEventListener component) {
                            component.processUpdateEvent(payload.event());
                        } else if (tab.getComponent(0) instanceof IUpdateEventListener component) {
                            component.processUpdateEvent(payload.event());
                        }
                    }
                }
            }
        }
        // send commands to specific tab
        else if (Arrays.stream(BookingOverviewComponent.Commands.values()).anyMatch(cmd -> cmd == updateEvent.getCmd())) {
            this.bookingOverview.processUpdateEvent(updateEvent);
        } else if (Arrays.stream(BookingChangeComponent.Commands.values()).anyMatch(cmd -> cmd == updateEvent.getCmd())) {
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

    private JComponent createClosableTab(ActionListener onCloseButtonClicked) {
        final var panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        final var label = new JLabel() {
            @Override
            public String getText() {
                var i = GUIBuchung.this.tabs.indexOfTabComponent(panel);
                if (i != -1) {
                    return GUIBuchung.this.tabs.getTitleAt(i);
                }
                return null;
            }

            @Override
            public String getToolTipText() {
                var i = GUIBuchung.this.tabs.indexOfTabComponent(panel);
                if (i != -1) {
                    return GUIBuchung.this.tabs.getToolTipTextAt(i);
                }
                return null;
            }
        };
        label.setFont(this.config.getFont());
        label.setForeground(this.config.getTextColor());
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                var i = GUIBuchung.this.tabs.indexOfTabComponent(panel);
                if (i != -1) {
                    GUIBuchung.this.tabs.setSelectedIndex(i);
                }
            }
        });

        final var button = new JButton("x");
        button.setFont(this.config.getFont());
        button.setForeground(this.config.getFailureColor());
        button.setBackground(null);
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(true);
        button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        button.addActionListener(onCloseButtonClicked);
        button.setToolTipText("Tab schließen");

        panel.add(label);
        panel.add(button);

        return panel;
    }

    private Optional<Integer> getTabIndex(final Object data) {
        if (data instanceof Tabs tab) {
            return Optional.of(this.tabs.indexOfTab(tab.getName())).filter(index -> index != -1);
        } else if (data instanceof Component component) {
            // remove wrappers
            while (component.getParent() != null && !(component.getParent() instanceof JTabbedPane)) {
                component = component.getParent();
            }
            return Optional.of(this.tabs.indexOfComponent(component)).filter(index -> index != -1);
        } else if (data instanceof String tabName) {
            return Optional.of(this.tabs.indexOfTab(tabName)).filter(index -> index != -1);
        } else if (data instanceof Integer index) {
            return Optional.of(index);
        }

        return Optional.empty();
    }

    private void initUI() {
        // create tab components
        this.bookingOverview = new BookingOverviewComponent(this.config);
        this.bookingOverview.addObserver(this);

        this.bookingList = new BookingListComponent(this.config);
        this.bookingList.addObserver(this);

        this.bookingCreate = new BookingChangeComponent(this.config);
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
        this.tabs.setFont(this.config.getFont());
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

    private JComponent wrapInWrapper(final ObservableComponent component) {
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
}
