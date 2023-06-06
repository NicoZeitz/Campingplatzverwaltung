package swe.ka.dhbw.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.model.Stellplatz;

public class StellplatzSelector extends ObservableComponent implements IUpdateEventListener{

    /**
     * Command for a button being pressed
     */
    public enum Commands implements EventCommand {

        /**
         * The command text, when a button was pressed
         */
        PLACE_SELECTED( "CampingplatzSelector.placeSelected", IDepictable.class ),
        UPDATE_PLACES( "CampingplatzSelector.updatePlaces", null );

        public final Class<?> payloadType;
        public final String cmdText;

        private Commands( String cmdText, Class<?> payloadType ) {
            this.cmdText = cmdText;
            this.payloadType = payloadType;
        }

        /**
         * {@inheritDoc}
         */
//		@Override
        public String getCmdText() {
            return this.cmdText;
        }

        /**
         * {@inheritDoc}
         */
//		@Override
        public Class<?> getPayloadType() {
            return this.payloadType;
        }
    }

    private int dimXY = 3;
    private List<IDepictable> stellplaetze;
    private final HashMap<String, JButton> stellPlatzButtonMap = new HashMap<String, JButton>();
    private JPanel pnl; // = new JPanel( new GridLayout(xy,xy) );


    public StellplatzSelector( List<IDepictable> stellplaetze ) {
        this.stellplaetze = stellplaetze;
        if( stellplaetze.size() != 9 && stellplaetze.size() != 16 ) {
            throw new IllegalArgumentException("The number of 'Stellplaetze' must be 9 or 16");
        }
        this.dimXY = (int)(Math.sqrt(stellplaetze.size()));
        this.initUI();
    }

    private void initUI() {
        this.pnl = new JPanel( new GridLayout(dimXY,dimXY) );
        this.setLayout(new BorderLayout());

        stellplaetze.forEach(e -> {
            final JButton button = new JButton(e.getElementID());
            stellPlatzButtonMap.put(e.getElementID(), button);
            button.addMouseListener( new MouseAdapter() {
                public void mouseClicked(MouseEvent ev) {
                    fireGUIEvent( new GUIEvent( getStellplatz( e.getElementID() ), Commands.PLACE_SELECTED));
                }
            });
            button.setBorder(BorderFactory.createEtchedBorder());
            pnl.add(button);
        });
        this.add(pnl, BorderLayout.CENTER);
        this.setReservedColors();
    }

    private IDepictable getStellplatz( String elementId ) {
        for (IDepictable iDepictable : stellplaetze) {
            if( iDepictable.getElementID().equals(elementId) ) return iDepictable;
        }
        return null;
    }

    private void setReservedColors() {
        stellplaetze.forEach(e -> {
            Attribute[] attArray = e.getAttributeArray();

            if( attArray != null ) {
                // get the button
                JButton button = stellPlatzButtonMap.get(attArray[0].getValue() );
                if( ((Boolean)attArray[Stellplatz.RESERVIERT].getValue()) == true ) {
                    button.setBackground(Color.red);
                }
                else {
                    button.setBackground(Color.green);
                }
            }
        });
    }


// NIcht erforderlich, da sich die Stelllätze als solche nie aendern (muessen)
//	public void setStellplaetze( List<IDepictable> stellplaetze ) {
//		this.stellplaetze = stellplaetze;
////		if( stellplaetze.size() != 9 && stellplaetze.size() != 16 ) {
////			throw new IllegalArgumentException("The number of 'Stellplaetze' must be 9 or 16");
////		}
//		this.initUI();
//	}

    @Override
    public void processUpdateEvent(UpdateEvent ue) {
        if( ue.getCmd() == Commands.UPDATE_PLACES ) {
            setReservedColors();
        }
    }

}
