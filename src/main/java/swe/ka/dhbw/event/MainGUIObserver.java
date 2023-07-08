package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import swe.ka.dhbw.control.GUIController;

public final class MainGUIObserver extends GUIObserver {

    @Override
    public void processGUIEvent(GUIEvent ge) {
        GUIController.getInstance();
        // UNIMPLEMENTED:
        System.out.println(ge);
    }
}
