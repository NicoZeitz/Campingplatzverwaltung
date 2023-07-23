package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.SimpleListComponent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GuestSelectorComponent extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        GUEST_SELECTED("GuestSelectorComponent::GUEST_SELECTED", IDepictable.class);

        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText, final Class<?> payloadType) {
            this.cmdText = cmdText;
            this.payloadType = payloadType;
        }

        @Override
        public String getCmdText() {
            return this.cmdText;
        }

        @Override
        public Class<?> getPayloadType() {
            return this.payloadType;
        }
    }

    public GuestSelectorComponent(final ReadonlyConfiguration config, List<? extends IDepictable> guests) {
        super("GuestSelectorComponent", config);
        this.initUI(guests);
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {

    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // nothing to process
    }

    private void initUI(final List<? extends IDepictable> guests) {
        final var border = BorderFactory.createTitledBorder("Gast hinzufügen");
        border.setTitleFont(this.config.getLargeFont());
        border.setTitleColor(this.config.getTextColor());
        this.setBorder(border);
        this.setLayout(new GridLayout(2, 1, 10, 10));


        final var inputWrapper = new JPanel();
        inputWrapper.setLayout(new GridBagLayout());


        this.add(inputWrapper);

        final var list = SimpleListComponent.builder(super.generateRandomID())
                .build();

        this.add(list);
    }
}
