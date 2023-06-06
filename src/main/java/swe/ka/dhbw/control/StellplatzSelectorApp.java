package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.util.BaseController;
import de.dhbwka.swe.utils.util.IOUtilities;
import swe.ka.dhbw.model.Stellplatz;
import swe.ka.dhbw.ui.StellplatzSelector;

import java.util.ArrayList;
import java.util.List;

public class StellplatzSelectorApp extends BaseController {   // BaseController beinhaltet Listener-Handling

    private StellplatzSelector spSelectorButton;
    private List<IDepictable> stellplaetze = new ArrayList<>();

    public static void main(String[] args) {
        new StellplatzSelectorApp();
    }

    public StellplatzSelectorApp() {
        try {
            this.stellplaetze.add(new Stellplatz("Platz-1", true));
            this.stellplaetze.add(new Stellplatz("Platz-2", false));
            this.stellplaetze.add(new Stellplatz("Platz-3", true));
            this.stellplaetze.add(new Stellplatz("Platz-4", false));
            this.stellplaetze.add(new Stellplatz("Platz-5", true));
            this.stellplaetze.add(new Stellplatz("Platz-6", false));
            this.stellplaetze.add(new Stellplatz("Platz-7", true));
            this.stellplaetze.add(new Stellplatz("Platz-8", false));
            this.stellplaetze.add(new Stellplatz("Platz-9", true));
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.spSelectorButton = new StellplatzSelector( this.stellplaetze);
        this.spSelectorButton.addObserver(this);
        this.addObserver(spSelectorButton);
        IOUtilities.openInJFrame( spSelectorButton, 600, 600, 0, 0, "StellplatzSelector", null, true);
    }

    @Override
    public void processGUIEvent(GUIEvent ge) {
        System.out.println(((IDepictable)ge.getSource()).getElementID() );
        Attribute[] attArray = ((IDepictable)ge.getSource()).getAttributeArray();
        System.out.println( attArray[1] );
        try {
            attArray[1].setValue( ! (Boolean)attArray[1].getValue() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        IDepictable sPlatz = (IDepictable)ge.getSource();
        sPlatz.setAttributeValues(attArray);
        fireUpdateEvent(new UpdateEvent(this, StellplatzSelector.Commands.UPDATE_PLACES, null));
    }


}
