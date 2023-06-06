package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;

public class Stellplatz implements IDepictable {

    public final static int ID = 0;
    public final static int RESERVIERT = 1;

    private Attribute[] attributes = new Attribute[] {
            new Attribute("iD", this, String.class, "", "unknown", true),
            new Attribute("reserviert", this, Boolean.class, false, false, true)
    };

    private String iD;
    private Boolean reserviert;

    private Stellplatz() {}

    public Stellplatz(String iD, Boolean reserviert) throws Exception {
        super();
        this.attributes[ID].setValue(iD);
        this.attributes[RESERVIERT].setValue(reserviert);
    }

    public void setReserviert(Boolean reserviert) throws Exception {
        this.attributes[RESERVIERT].setValue(reserviert);
    }

    public Boolean isReserviert() {
        return (Boolean) this.attributes[RESERVIERT].getValue();
    }

    public Attribute[] getAttributeArray() {
        return this.attributes;
    }

    public String getElementID() {
        return (String) this.attributes[ID].getValue();
    }

    @Override
    public String toString() {
        return (String) this.attributes[ID].getValue();
    }
}
