package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;

import java.time.LocalDate;

public class GebuchteLeistung implements IDepictable, IPersistable, ICSVPersistable {

    private LocalDate buchungStart;
    private LocalDate buchungsEnde;
    private Leistungsbeschreibung leistungsbeschreibung;
    private Buchung buchung;

    public LocalDate getBuchungStart() {
        return buchungStart;
    }

    public void setBuchungStart(LocalDate buchungStart) {
        this.buchungStart = buchungStart;
    }

    public LocalDate getBuchungsEnde() {
        return buchungsEnde;
    }

    public void setBuchungsEnde(LocalDate buchungsEnde) {
        this.buchungsEnde = buchungsEnde;
    }

    public Leistungsbeschreibung getLeistungsbeschreibung() {
        return leistungsbeschreibung;
    }

    public void setLeistungsbeschreibung(Leistungsbeschreibung leistungsbeschreibung) {
        this.leistungsbeschreibung = leistungsbeschreibung;
    }

    public Buchung getBuchung() {
        return buchung;
    }

    public void setBuchung(Buchung buchung) {
        this.buchung = buchung;
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
}
