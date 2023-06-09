package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonComponent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.util.AppLogger;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class GUIBuchung extends GUIComponent implements IGUIEventListener {
    private final String GAST_SELECTOR_BUTTON_ELEMENT_ID = this.getClass().getName() + ".gastSelectorButtonElementID";
    private final String EXIT_BUTTON_COMPONENT_ID = this.getClass().getName() + ".exitButtonComponentID";
    private final String DISCARD_BUTTON_ELEMENT_ID = this.getClass().getName() + ".discardButtonElementID";
    private final String SAVE_BUTTON_ELEMENT_ID = this.getClass().getName() + ".saveButtonElementID";


    public enum Commands implements EventCommand {
        DISCARD("discard", String.class),
        SAVE("save", String.class),
        ;
        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText, final Class<?> payloadType ) {
            this.cmdText = cmdText;
            this.payloadType = payloadType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getCmdText() {
            return this.cmdText;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public Class<?> getPayloadType() {
            return this.payloadType;
        }
    }

    public GUIBuchung(ReadonlyConfiguration config) {
        super();
        final var main = new JPanel();
        main.setLayout(new GridLayout(1, 2));
        final var leftSide = this.createLeftSide(config);
        final var rightSide = this.createRightSide(config);
        main.add(leftSide);
        main.add(rightSide);

        final var buttonComponent = ButtonComponent.builder(EXIT_BUTTON_COMPONENT_ID)
                .embeddedComponent(main)
                .title("Buchung anlegen")
                .buttonElements(new ButtonElement[] {
                        ButtonElement.builder(DISCARD_BUTTON_ELEMENT_ID)
                                .buttonText("Abbrechen")
                                .font(config.getFont())
                                .observer(this)
                                .toolTip("Bricht das Erstellen der Buchung ab")
                                .build(),
                        ButtonElement.builder(SAVE_BUTTON_ELEMENT_ID)
                                .buttonText("Bestätigen")
                                .font(config.getFont())
                                .observer(this)
                                .toolTip("Speichert die Buchung")
                                .build()
                })
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();

        this.setLayout(new GridLayout(1, 1));
        this.add(buttonComponent);
    }

    private JComponent createLeftSide(ReadonlyConfiguration config) {
        var panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.add(this.createGastSelector(config));

        return panel;
    }

    private JComponent createGastSelector(ReadonlyConfiguration config) {
        return this.createSelector(
                config,
                GAST_SELECTOR_BUTTON_ELEMENT_ID,
                "Öffnet den Gast-Selektor",
                Optional.of("Gäste auswählen"),
                Optional.of(this)
        );
    }

    private JComponent createRightSide(ReadonlyConfiguration config) {
        var panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        return panel;
    }

    @Override
    public void processUpdateEvent(UpdateEvent ue) {    }

    @Override
    public void processGUIEvent(GUIEvent ge) {
        // TODO: implement and fire own events
        fireGUIEvent(ge);
    }
}
