package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.IGUIEventSender;
import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.gui.ButtonComponent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public abstract class GUIComponent extends ObservableComponent implements IUpdateEventListener {
    protected JComponent createSelector(
            ReadonlyConfiguration config,
            String plusButtonID,
            String plusButtonToolTip,
            Optional<String> title,
            Optional<IGUIEventListener> observer
    ) {
        var buttonElementBuilder = ButtonElement.builder(plusButtonID)
                .buttonText("+")
                .componentSize(new Dimension(45, 45))
                .font(config.getFont())
                .toolTip(plusButtonToolTip);

        if(observer.isPresent()) {
            buttonElementBuilder = buttonElementBuilder.observer(observer.get());
        }


        var wrapperBuilder = ButtonComponent.builder(this.generateRandomID(Optional.of(ButtonComponent.class)))
                .buttonElements(new ButtonElement[] {
                    buttonElementBuilder.build()
                })
                .position(ButtonComponent.Position.NORTH)
                .orientation(ButtonComponent.Orientation.RIGHT);

        if(title.isPresent()) {
            wrapperBuilder = wrapperBuilder.title(title.get());
        }

        return wrapperBuilder.build();
    }

    protected String generateRandomID(Optional<Class<? extends JComponent>> targetComponent) {
        var randomPart = UUID.randomUUID().toString();
        var targetComponentID = targetComponent
                .map(Class::toString)
                .orElse("JComponent");

        return String.format("%s_%s", targetComponentID, randomPart);
    }

}
