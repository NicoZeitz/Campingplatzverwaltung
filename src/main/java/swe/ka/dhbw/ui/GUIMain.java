package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUIMain extends GUIComponent {
    public enum Commands implements EventCommand {
        BOOKING_MANAGEMENT("Buchungen verwalten"),
        PITCH_MANAGEMENT("Stellplätze verwalten"),
        GUEST_MANAGEMENT("Gästedaten verwalten"),
        FACILITY_MANAGEMENT("Einrichtungen verwalten"),
        PERSONNEL_MANAGEMENT("Personal verwalten"),
        CREATE_BOOKING("Buchung erstellen"),
        CHECK_IN_CHECK_OUT("Check-In / Check-Out");

        public final String cmdText;

        Commands(final String cmdText) {
            this.cmdText = cmdText;
        }

        @Override
        public String getCmdText() {
            return this.cmdText;
        }

        @Override
        public Class<?> getPayloadType() {
            return null;
        }
    }

    public GUIMain(final ReadonlyConfiguration config) {
        super("GUIMain", config);
        this.initUI();
    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {
        // Empty as nothing can change the main gui
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(this.config.getBackgroundColor());

        // Titel
        final var title = new JPanel();
        title.setLayout(new GridLayout(2, 1, 0, 10));
        title.setBackground(this.config.getBackgroundColor());
        title.setOpaque(true);

        final var titleHeader = new JLabel("Campingplatzverwaltung");
        titleHeader.setHorizontalAlignment(SwingConstants.CENTER);
        titleHeader.setFont(this.config.getHeaderFont());
        titleHeader.setForeground(this.config.getTextColor());
        titleHeader.setBorder(new EmptyBorder(10, 10, 10, 10));
        title.add(titleHeader);

        final var subtitle = new JLabel("Hauptanwendungen");
        subtitle.setHorizontalAlignment(SwingConstants.LEFT);
        subtitle.setFont(this.config.getLargeFont());
        subtitle.setForeground(this.config.getTextColor());
        subtitle.setBorder(new EmptyBorder(10, 10, 10, 10));
        title.add(subtitle);

        this.add(title, BorderLayout.NORTH);

        // Hauptanwendungen
        final var mainApps = new JPanel();
        mainApps.setLayout(new GridLayout(2, 3, 10, 10));
        mainApps.setBackground(this.config.getBackgroundColor());
        mainApps.setOpaque(true);
        mainApps.setBorder(new EmptyBorder(10, 10, 10, 10));

        final var buchungen = new JButton("Buchungen verwalten");
        final var stellplaetze = new JButton("Stellplätze verwalten");
        final var gaeste = new JButton("Gästedaten verwalten");
        final var einrichtungen = new JButton("Einrichtungen verwalten");
        final var personal = new JButton("Personaldaten verwalten");

        for (final var btn : new JButton[] {buchungen, stellplaetze, gaeste, einrichtungen, personal}) {
            btn.setFont(this.config.getLargeFont());
            btn.setForeground(this.config.getTextColor());
            btn.setBackground(this.config.getAccentColor());
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            btn.setBorder(BorderFactory.createEmptyBorder());
            mainApps.add(btn);
        }

        // Schnellfunktionen
        final var quickApps = new JPanel();
        quickApps.setLayout(new GridLayout(2, 1, 10, 10));
        quickApps.setBackground(this.config.getBackgroundColor());
        quickApps.setOpaque(true);
        quickApps.setBorder(BorderFactory.createEmptyBorder());

        final var buchungErstellen = new JButton("Buchung erstellen");
        final var checkInCheckOut = new JButton("Check-In / Check-Out");

        for (final var btn : new JButton[] {buchungErstellen, checkInCheckOut}) {
            btn.setFont(this.config.getLargeFont());
            btn.setForeground(this.config.getTextColor());
            btn.setBackground(this.config.getSecondaryAccentColor());
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            btn.setBorder(BorderFactory.createEmptyBorder());
            quickApps.add(btn);
        }

        mainApps.add(quickApps);

        buchungen.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.BOOKING_MANAGEMENT)));
        stellplaetze.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.PITCH_MANAGEMENT)));
        gaeste.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.GUEST_MANAGEMENT)));
        einrichtungen.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.FACILITY_MANAGEMENT)));
        personal.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.PERSONNEL_MANAGEMENT)));
        buchungErstellen.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.CREATE_BOOKING)));
        checkInCheckOut.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.CHECK_IN_CHECK_OUT)));

        this.add(mainApps, BorderLayout.CENTER);
    }
}
