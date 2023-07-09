package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;

public class Chipkarte implements IDepictable, IPersistable, ICSVPersistable {

    private int nummer;
    private Status status;

    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getElementID() {
        return null;
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[0];
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        return IDepictable.super.setAttributeValues(attributeArray);
    }

    @Override
    public String[] getCSVHeader() {
        return new String[0];
    }

    @Override
    public String[] getCSVData() {
        return new String[0];
    }

    @Override
    public Object getPrimaryKey() {
        return null;
    }

    public enum Status {
        VERFUEGBAR, IN_VERWENDUNG
    }
}
