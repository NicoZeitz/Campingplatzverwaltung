package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

public class GUIGast extends GUIComponent {
    public GUIGast(final ReadonlyConfiguration config) {
        super("GUIGast", config);
        super.createEmptyMessage("Stellen Sie sich hier eine wunderbare Gästedatenverwaltung vor!");
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // placeholder
    }
}
