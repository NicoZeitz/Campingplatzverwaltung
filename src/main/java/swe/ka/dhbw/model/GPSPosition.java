package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IAttributed;
import swe.ka.dhbw.util.Validator;

import java.util.Objects;

public final class GPSPosition implements IAttributed {
    public enum Attributes {
        LATITUDE,
        LONGITUDE
    }

    private double latitude;
    private double longitude;

    public GPSPosition(final double latitude, final double longitude) {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(final double latitude) {
        Validator.getInstance().validateInRange(latitude, -90d, 90d);
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(final double longitude) {
        Validator.getInstance().validateInRange(longitude, -180d, 180d);
        this.longitude = longitude;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof GPSPosition that)) return false;
        return Double.compare(that.getLatitude(), this.getLatitude()) == 0 &&
                Double.compare(that.getLongitude(), this.getLongitude()) == 0;
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.LATITUDE.name(), this, Double.class, this.getLatitude(), this.getLatitude(), true),
                new Attribute(Attributes.LONGITUDE.name(), this, Double.class, this.getLongitude(), this.getLongitude(), true)
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getLatitude(), this.getLongitude());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();
        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.LATITUDE.name()) && !value.equals(this.getLatitude())) {
                this.setLatitude((double) value);
            } else if (name.equals(Attributes.LONGITUDE.name()) && !value.equals(this.getLongitude())) {
                this.setLongitude((double) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "GPSPosition{" +
                "latitude=" + this.getLatitude() +
                ", longitude=" + this.getLongitude() +
                '}';
    }
}
