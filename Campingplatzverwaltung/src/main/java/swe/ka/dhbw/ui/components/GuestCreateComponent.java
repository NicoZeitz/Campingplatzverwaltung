package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

public class GuestCreateComponent extends GUIComponent {
    public GuestCreateComponent(final ReadonlyConfiguration config) {
        super("GuestCreateComponent", config);
        super.createEmptyMessage("Hier finden zurzeit noch Bauarbeiten statt. In der Zukunft können Sie hier einen neuen Gast anlegen.");
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {

    }

}
