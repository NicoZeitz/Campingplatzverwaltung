package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.BookingListComponent;
import swe.ka.dhbw.ui.BookingOverviewComponent;

import java.time.LocalDate;

public class GUIBuchungObserver implements IGUIEventListener {

    @Override
    public void processGUIEvent(final GUIEvent ge) {
        // BookingOverviewComponent
        if (ge.getCmd() == BookingOverviewComponent.Commands.PREVIOUS_WEEK) {
            GUIController.getInstance().bookingOverviewPreviousWeek((LocalDate) ge.getData());
        } else if (ge.getCmd() == BookingOverviewComponent.Commands.NEXT_WEEK) {
            GUIController.getInstance().bookingOverviewNextWeek((LocalDate) ge.getData());
        } else if (ge.getCmd() == BookingOverviewComponent.Commands.BUCHUNG_SELECTED) {
            // TODO: Buchung bearbeiten
        }

        // BookingListComponent
        else if (ge.getCmd() == BookingListComponent.Commands.BUCHUNG_SELECTED) {
            // TODO: Buchung bearbeiten
        } else {
            LogObserver.logGUIEvent(ge);
        }
    }
}
