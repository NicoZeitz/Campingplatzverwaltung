package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;

public class LogObserver implements IGUIEventListener {
    public static void logGUIEvent(final GUIEvent guiEvent) {
        System.out.println("============================================ GUIEvent ============================================");
        System.out.println("EVENT: " + guiEvent.toString().replaceAll("\n", " ").replaceAll("\r", " "));
        System.out.println("CMD: " + guiEvent.getCmd());
        System.out.println("DATA: " + guiEvent.getData());
    }

    public static void logUpdateEvent(final UpdateEvent updateEvent) {
        System.out.println("============================================ UpdateEvent ============================================");
        System.out.println("EVENT: " + updateEvent.toString().replaceAll("\n", " ").replaceAll("\r", " "));
        System.out.println("CMD: " + updateEvent.getCmd());
        System.out.println("DATA: " + updateEvent.getData());
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        LogObserver.logGUIEvent(guiEvent);
    }
}
