package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;

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
        DISCARD("discard", String.class),
        SAVE("save", String.class),
        ;
        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText, final Class<?> payloadType) {
            this.cmdText = cmdText;
            this.payloadType = payloadType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getCmdText() {
            return this.cmdText;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public Class<?> getPayloadType() {
            return this.payloadType;
        }
    }

    private final String GAST_SELECTOR_BUTTON_ELEMENT_ID = this.getClass().getName() + ".gastSelectorButtonElementID";
    private final String EXIT_BUTTON_COMPONENT_ID = this.getClass().getName() + ".exitButtonComponentID";
    private final String DISCARD_BUTTON_ELEMENT_ID = this.getClass().getName() + ".discardButtonElementID";
    private final String SAVE_BUTTON_ELEMENT_ID = this.getClass().getName() + ".saveButtonElementID";

    private BookingOverviewComponent bookingOverview;

    public GUIBuchung(final ReadonlyConfiguration config,
                      final Map<LocalDate, List<? extends IDepictable>> appointments,
                      final LocalDate currentWeek) {
        super();

        this.initUI(config, appointments, currentWeek);
        return;

        /*
        final var main = new JPanel();
        main.setLayout(new GridLayout(1, 2));
        final var leftSide = this.createLeftSide(config);
        final var rightSide = this.createRightSide(config);
        main.add(leftSide);
        main.add(rightSide);

        final var buttonComponent = ButtonComponent.builder(EXIT_BUTTON_COMPONENT_ID)
                .embeddedComponent(main)
                .title("Buchung anlegen")
                .buttonElements(new ButtonElement[] {
                        ButtonElement.builder(DISCARD_BUTTON_ELEMENT_ID)
                                .buttonText("Abbrechen")
                                .font(config.getFont())
                                .observer(this)
                                .toolTip("Bricht das Erstellen der Buchung ab")
                                .build(),
                        ButtonElement.builder(SAVE_BUTTON_ELEMENT_ID)
                                .buttonText("Bestätigen")
                                .font(config.getFont())
                                .observer(this)
                                .toolTip("Speichert die Buchung")
                                .build()
                })
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();

        this.setLayout(new GridLayout(1, 1));
        this.add(buttonComponent);

         */
    }

    @Override
    public void processGUIEvent(GUIEvent ge) {
        fireGUIEvent(ge);
    }

    @Override
    public void processUpdateEvent(UpdateEvent updateEvent) {
        if (Arrays.stream(BookingOverviewComponent.Commands.values()).anyMatch(cmd -> cmd == updateEvent.getCmd())) {
            this.bookingOverview.processUpdateEvent(updateEvent);
        }
    }

    private JComponent createGastSelector(ReadonlyConfiguration config) {
        return this.createSelector(
                config,
                GAST_SELECTOR_BUTTON_ELEMENT_ID,
                "Öffnet den Gast-Selektor",
                Optional.of("Gäste auswählen"),
                Optional.of(this)
        );
    }

    private JComponent createLeftSide(ReadonlyConfiguration config) {
        var panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.add(this.createGastSelector(config));

        return panel;
    }

    private JComponent createRightSide(ReadonlyConfiguration config) {
        var panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        return panel;
    }

    private void initUI(final ReadonlyConfiguration config,
                        final Map<LocalDate, List<? extends IDepictable>> appointments,
                        final LocalDate currentWeek) {
        this.bookingOverview = new BookingOverviewComponent(appointments, currentWeek, config);
        this.bookingOverview.addObserver(this);

        final var tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Terminübersicht",
                null,
                this.bookingOverview,
                "Zeigt die Buchungen übersichtlich in einem Kalendar an");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.addTab("Buchungsliste", null, new JPanel(), "Zeigt die Buchungen in einer Liste an");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);
        tabbedPane.addTab("Buchung anlegen", null, new JPanel(), "Erstellt eine neue Buchung");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_3);

        this.setLayout(new GridLayout(1, 1));
        this.add(tabbedPane);
    }
}
