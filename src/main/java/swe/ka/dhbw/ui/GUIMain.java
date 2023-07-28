package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUIMain extends GUIComponent {
    // Commands
    public enum Commands implements EventCommand {
        // outgoing gui events
        BOOKING_MANAGEMENT("GUIMain::BOOKING_MANAGEMENT"),
        PITCH_MANAGEMENT("GUIMain::PITCH_MANAGEMENT"),
        GUEST_MANAGEMENT("GUIMain::GUEST_MANAGEMENT"),
        FACILITY_MANAGEMENT("GUIMain::FACILITY_MANAGEMENT"),
        PERSONNEL_MANAGEMENT("GUIMain::PERSONNEL_MANAGEMENT"),
        CREATE_BOOKING("GUIMain::CREATE_BOOKING"),
        CHECK_IN_CHECK_OUT("GUIMain::CHECK_IN_CHECK_OUT");

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
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // Empty as nothing can change the main gui
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(this.config.getBackgroundColor());

        // Title & Heading
        final var topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1, 0, 10));
        topPanel.setBackground(this.config.getBackgroundColor());
        topPanel.setOpaque(true);

        final var title = new JLabel("Campingplatzverwaltung");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(this.config.getHeaderFont());
        title.setForeground(this.config.getTextColor());
        title.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.add(title);

        final var header = new JLabel("Hauptanwendungen");
        header.setHorizontalAlignment(SwingConstants.LEFT);
        header.setFont(this.config.getLargeFont());
        header.setForeground(this.config.getTextColor());
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.add(header);

        this.add(topPanel, BorderLayout.NORTH);

        // Main Apps
        final var mainApps = new JPanel();
        mainApps.setLayout(new GridLayout(2, 3, 10, 10));
        mainApps.setBackground(this.config.getBackgroundColor());
        mainApps.setOpaque(true);
        mainApps.setBorder(new EmptyBorder(10, 10, 10, 10));

        final var bookingManagement = new JButton("Buchungen verwalten");
        final var pitchManagement = new JButton("Stellplätze verwalten");
        final var guestManagement = new JButton("Gästedaten verwalten");
        final var facilityManagement = new JButton("Einrichtungen verwalten");
        final var staffManagement = new JButton("Personaldaten verwalten");

        for (final var btn : new JButton[] {bookingManagement, pitchManagement, guestManagement, facilityManagement, staffManagement}) {
            btn.setFont(this.config.getLargeFont());
            btn.setForeground(this.config.getTextColor());
            btn.setBackground(this.config.getAccentColor());
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            btn.setBorder(BorderFactory.createEmptyBorder());
            mainApps.add(btn);
        }

        // Quick Functions
        final var quickApps = new JPanel();
        quickApps.setLayout(new GridLayout(2, 1, 10, 10));
        quickApps.setBackground(this.config.getBackgroundColor());
        quickApps.setOpaque(true);
        quickApps.setBorder(BorderFactory.createEmptyBorder());

        final var createBooking = new JButton("Buchung erstellen");
        final var checkInCheckOut = new JButton("Check-In / Check-Out");

        for (final var btn : new JButton[] {createBooking, checkInCheckOut}) {
            btn.setFont(this.config.getLargeFont());
            btn.setForeground(this.config.getTextColor());
            btn.setBackground(this.config.getSecondaryAccentColor());
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            btn.setBorder(BorderFactory.createEmptyBorder());
            quickApps.add(btn);
        }

        mainApps.add(quickApps);

        bookingManagement.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.BOOKING_MANAGEMENT)));
        pitchManagement.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.PITCH_MANAGEMENT)));
        guestManagement.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.GUEST_MANAGEMENT)));
        facilityManagement.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.FACILITY_MANAGEMENT)));
        staffManagement.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.PERSONNEL_MANAGEMENT)));
        createBooking.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.CREATE_BOOKING)));
        checkInCheckOut.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.CHECK_IN_CHECK_OUT)));

        this.add(mainApps, BorderLayout.CENTER);
    }
}
