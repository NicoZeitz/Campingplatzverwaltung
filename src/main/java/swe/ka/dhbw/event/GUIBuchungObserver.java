package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.components.BookingListComponent;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;

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
            GUIController.getInstance().bookingOpenEditTab(((IDepictable) ge.getData()).getElementID());
        }

        // BookingListComponent
        else if (ge.getCmd() == BookingListComponent.Commands.BUCHUNG_SELECTED) {
            GUIController.getInstance().bookingOpenEditTab(((IDepictable) ge.getData()).getElementID());
        } else {
            LogObserver.logGUIEvent(ge);
        }
    }
}
