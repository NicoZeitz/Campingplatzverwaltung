package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;

public class LogObserver implements IGUIEventListener {
    public static void logGUIEvent(final GUIEvent ge) {
        System.out.println("============================================ GUIEvent ============================================");
        System.out.println("EVENT: " + ge.toString().replaceAll("\n", " ").replaceAll("\r", " "));
        System.out.println("CMD: " + ge.getCmd());
        System.out.println("DATA: " + ge.getData());
    }

    @Override
    public void processGUIEvent(final GUIEvent ge) {
        LogObserver.logGUIEvent(ge);
    }
}
