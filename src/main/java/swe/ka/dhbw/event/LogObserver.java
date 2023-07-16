package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;

public class LogObserver extends GUIObserver {
    @Override
    public void processGUIEvent(final GUIEvent ge) {
        System.out.println("============================================ GUIEvent ============================================");
        System.out.println("EVENT: " + ge.toString().replaceAll("\n", " "));
        System.out.println("CMD: " + ge.getCmd());
        System.out.println("DATA: " + ge.getData());
    }
}
