package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.GUIMain;

public final class GUIMainObserver implements IGUIEventListener {
    @Override
    public void processGUIEvent(final GUIEvent ge) {
        if (ge.getCmd() == GUIMain.Commands.BOOKING_MANAGEMENT) {
            GUIController.getInstance().openWindowBooking();
        } else if (ge.getCmd() == GUIMain.Commands.PITCH_MANAGEMENT) {
            GUIController.getInstance().openWindowPitch();
        } else if (ge.getCmd() == GUIMain.Commands.GUEST_MANAGEMENT) {
            GUIController.getInstance().openWindowGuest();
        } else if (ge.getCmd() == GUIMain.Commands.FACILITY_MANAGEMENT) {
            GUIController.getInstance().openWindowFacility();
        } else if (ge.getCmd() == GUIMain.Commands.PERSONNEL_MANAGEMENT) {
            GUIController.getInstance().openWindowStaff();
        } else if (ge.getCmd() == GUIMain.Commands.CREATE_BOOKING) {
            GUIController.getInstance().handleWindowMainCreateBooking();
        } else if (ge.getCmd() == GUIMain.Commands.CHECK_IN_CHECK_OUT) {
            GUIController.getInstance().openWindowCheckInCheckOut();
        } else {
            LogObserver.logGUIEvent(ge);
        }
    }
}
