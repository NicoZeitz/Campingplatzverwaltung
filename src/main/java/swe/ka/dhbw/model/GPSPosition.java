package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IAttributed;

import java.util.Objects;

public class GPSPosition implements IAttributed {
    public static final int LATITUDE = 0;
    public static final int LONGITUDE = 1;

    private Attribute[] attributes;
    
    public GPSPosition(double latitude, double longitude) {
        this.attributes = new Attribute[] {
                new Attribute("latitude", this, Double.class, latitude, latitude, true),
                new Attribute("longitude", this, Double.class, longitude, longitude, true)
        };
    }

    public double getLatitude() {
        return (Double) this.attributes[LATITUDE].getValue();
    }

    public void setLatitude(double latitude) throws Exception {
        this.attributes[LATITUDE].setValue(latitude);
    }

    public double getLongitude() {
        return (Double) this.attributes[LONGITUDE].getValue();
    }

    public void setLongitude(double longitude) throws Exception {
        this.attributes[LONGITUDE].setValue(longitude);
    }

    @Override
    public Attribute[] getAttributeArray() {
        return this.attributes;
    }

    @Override
    public String toString() {
        return "GPSPosition{" +
                "latitude=" + this.getLatitude() +
                ", longitude=" + this.getLongitude() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GPSPosition that)) return false;
        return Double.compare(that.getLatitude(), this.getLatitude()) == 0 && Double.compare(that.getLongitude(), this.getLongitude()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getLatitude(), this.getLongitude());
    }
}
