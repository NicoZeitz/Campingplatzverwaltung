package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

public class GUICheckInCheckOut extends GUIComponent {
    public GUICheckInCheckOut(final ReadonlyConfiguration config) {
        super("GUICheckInCheckOut", config);
        super.createEmptyMessage("Stellen Sie sich hier einen wunderbaren Check-In / Check-Out Prozess vor!");
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // placeholder
    }
}