package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

public class PitchSelectorComponent extends GUIComponent {
    public PitchSelectorComponent(final ReadonlyConfiguration config) {
        super("PitchSelectorComponent", config);
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {

    }
}
