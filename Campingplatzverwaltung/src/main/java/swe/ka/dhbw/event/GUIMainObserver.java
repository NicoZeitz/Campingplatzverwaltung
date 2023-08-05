package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.GUIMain;

public final class GUIMainObserver implements IGUIEventListener {
    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        final var controller = GUIController.getInstance();
        if (guiEvent.getCmd() instanceof GUIMain.Commands command) {
            switch (command) {
                case BOOKING_MANAGEMENT -> controller.handleWindowMainOpenBookingManagement();
                case PITCH_MANAGEMENT -> controller.openWindowPitch();
                case GUEST_MANAGEMENT -> controller.openWindowGuest();
                case FACILITY_MANAGEMENT -> controller.openWindowFacility();
                case PERSONNEL_MANAGEMENT -> controller.openWindowStaff();
                case CREATE_BOOKING -> controller.handleWindowMainCreateBooking();
                case CHECK_IN_CHECK_OUT -> controller.openWindowCheckInCheckOut();
            }
        }
    }
}
