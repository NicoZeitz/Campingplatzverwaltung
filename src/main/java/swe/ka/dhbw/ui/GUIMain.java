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
        PERSONNEL_MANAGEMENT("Personal verwalten");

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

    private final ReadonlyConfiguration config;

    public GUIMain(final ReadonlyConfiguration config) {
        this.config = config;
        this.initUI();
    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {
        // Empty as nothing can change the main gui
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Titel
        final var title = new JPanel();
        title.setLayout(new GridLayout(2, 1, 0, 10));
        title.setBackground(Color.WHITE);
        title.setOpaque(true);

        final var titleHeader = new JLabel("Campingplatzverwaltung");
        titleHeader.setHorizontalAlignment(SwingConstants.CENTER);
        titleHeader.setFont(this.config.getHeaderFont());
        titleHeader.setBorder(new EmptyBorder(10, 10, 10, 10));
        title.add(titleHeader);

        final var subtitle = new JLabel("Hauptanwendungen");
        subtitle.setHorizontalAlignment(SwingConstants.LEFT);
        subtitle.setFont(this.config.getLargeFont());
        subtitle.setBorder(new EmptyBorder(10, 10, 10, 10));
        title.add(subtitle);

        this.add(title, BorderLayout.NORTH);

        // Hauptanwendungen
        final var mainApps = new JPanel();
        mainApps.setLayout(new GridLayout(2, 3, 10, 10));
        mainApps.setBackground(Color.WHITE);
        mainApps.setOpaque(true);
        mainApps.setBorder(new EmptyBorder(10, 10, 10, 10));

        final var buchungen = new JButton("Buchungen verwalten");
        final var stellplaetze = new JButton("Stellplätze verwalten");
        final var gaeste = new JButton("Gästedaten verwalten");
        final var einrichtungen = new JButton("Einrichtungen verwalten");
        final var personal = new JButton("Personaldaten verwalten");

        for (final var btn : new JButton[] {buchungen, stellplaetze, gaeste, einrichtungen, personal}) {
            btn.setFont(this.config.getLargeFont());
            btn.setBackground(this.config.getAccentColor());
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            mainApps.add(btn);
        }

        buchungen.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.BOOKING_MANAGEMENT)));
        stellplaetze.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.PITCH_MANAGEMENT)));
        gaeste.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.GUEST_MANAGEMENT)));
        einrichtungen.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.FACILITY_MANAGEMENT)));
        personal.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.PERSONNEL_MANAGEMENT)));

        this.add(mainApps, BorderLayout.CENTER);
    }
}
