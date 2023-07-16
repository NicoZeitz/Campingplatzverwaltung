package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.BookingOverviewComponent;

import java.time.LocalDate;

public class GUIBuchungObserver extends GUIObserver {
    private final GUIController controller;

    public GUIBuchungObserver(GUIController controller) {
        this.controller = controller;
    }

    @Override
    public void processGUIEvent(final GUIEvent ge) {
        // BookingOverviewComponent
        if (ge.getCmd() == BookingOverviewComponent.Commands.PREVIOUS_WEEK) {
            this.controller.bookingOverviewPreviousWeek((LocalDate) ge.getData());
        } else if (ge.getCmd() == BookingOverviewComponent.Commands.NEXT_WEEK) {
            this.controller.bookingOverviewNextWeek((LocalDate) ge.getData());
        } else if (ge.getCmd() == BookingOverviewComponent.Commands.BUCHUNG_SELECTED) {
            // ignore
        }
    }
}
