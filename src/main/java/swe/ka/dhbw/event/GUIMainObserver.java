package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.GUIMain;

public final class GUIMainObserver implements IGUIEventListener {
    @Override
    public void processGUIEvent(final GUIEvent ge) {
        if (ge.getCmd() == GUIMain.Commands.BOOKING_MANAGEMENT) {
            GUIController.getInstance().openGUIBuchung();
        } else if (ge.getCmd() == GUIMain.Commands.PITCH_MANAGEMENT) {
            GUIController.getInstance().openGUIStellplatz();
        } else if (ge.getCmd() == GUIMain.Commands.GUEST_MANAGEMENT) {
            GUIController.getInstance().openGUIGast();
        } else if (ge.getCmd() == GUIMain.Commands.FACILITY_MANAGEMENT) {
            GUIController.getInstance().openGUIEinrichtung();
        } else if (ge.getCmd() == GUIMain.Commands.PERSONNEL_MANAGEMENT) {
            GUIController.getInstance().openGUIPersonal();
        } else {
            LogObserver.logGUIEvent(ge);
        }
    }
}
