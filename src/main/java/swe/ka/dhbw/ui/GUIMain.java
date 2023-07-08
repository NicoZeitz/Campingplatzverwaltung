package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.UpdateEvent;

import java.awt.*;

public class GUIMain extends GUIComponent {
    public GUIMain() {
        this.setLayout(new GridLayout(1, 1));

    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {
        // Empty as nothing can change the main gui
    }

    public enum Commands implements EventCommand {
        ;
        public final Class<?> payloadType;
        public final String cmdText;

        private Commands(final String cmdText, final Class<?> payloadType) {
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
}
