package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IPersistable;
import de.dhbwka.swe.utils.util.AppLogger;
import swe.ka.dhbw.model.*;
import swe.ka.dhbw.util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;

public class EntityFactory {
    private static EntityFactory instance;
    private final Map<Class<?>, Map<Object, Set<Consumer<IPersistable>>>> missingReferences = new HashMap<>();
    private EntityManager entityManager;
    private Datenbasis<ICSVPersistable> database;

    private EntityFactory() {
    }

    public static synchronized EntityFactory getInstance() {
        if (instance == null) {
            instance = new EntityFactory();
        }
        return instance;
    }

    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setDatabase(final Datenbasis<ICSVPersistable> database) {
        this.database = database;
    }

    @SuppressWarnings("UnusedReturnValue")
    public IPersistable createElement(final Class<?> c, final String[] csvData) {
        Validator.getInstance().validateNotNull(c);
        Validator.getInstance().validateNotNull(csvData);

        IPersistable persistable;
        if (c == Adresse.class) {
            persistable = this.createAddressFromCSVData(csvData);
        } else if (c == Anlage.class) {
            throw new IllegalArgumentException("EntityFactor::createElement: Anlage is abstract");
        } else if (c == Ausruestung.class || c == Fahrzeug.class) {
            persistable = this.createEquipmentFromCSVData(csvData);
        } else if (c == Bereich.class) {
            persistable = this.createAreaFromCSVData(csvData);
        } else if (c == Buchung.class) {
            persistable = this.createBookingFromCSVData(csvData);
        } else if (c == Chipkarte.class) {
            persistable = this.createChipCardFromCSVData(csvData);
        } else if (c == Einrichtung.class) {
            persistable = this.createFacilityFromCSVData(csvData);
        } else if (c == Foto.class) {
            persistable = this.createPhotoFromCSVData(csvData);
        } else if (c == Fremdfirma.class) {
            persistable = this.createContractorFromCSVData(csvData);
        } else if (c == Gast.class) {
            persistable = this.createGuestFromCSVData(csvData);
        } else if (c == GebuchteLeistung.class) {
            persistable = this.createBookedServiceFromCSVData(csvData);
        } else if (c == Geraetschaft.class) {
            persistable = this.createToolFromCSVData(csvData);
        } else if (c == GPSPosition.class) {
            throw new IllegalArgumentException("EntityFactor::createElement: GPSPosition cannot be created by itself");
        } else if (c == Leistungsbeschreibung.class) {
            throw new IllegalArgumentException("EntityFactor::createElement: Leistungsbeschreibung is abstract");
        } else if (c == Oeffnungstag.class) {
            persistable = this.createOpeningDayFromCSVData(csvData);
        } else if (c == Oeffnungszeit.class) {
            persistable = this.createOpeningHourFromCSVData(csvData);
        } else if (c == Person.class) {
            persistable = this.createPersonFromCSVData(csvData);
        } else if (c == Personal.class) {
            persistable = this.createPersonalFromCSVData(csvData);
        } else if (c == Rechnung.class) {
            persistable = this.createInvoiceFromCSVData(csvData);
        } else if (c == Stellplatz.class) {
            persistable = this.createPitchFromCSVData(csvData);
        } else if (c == Stellplatzfunktion.class) {
            persistable = this.createPitchFunctionFromCSVData(csvData);
        } else if (c == Stoerung.class) {
            persistable = this.createDisturbanceFromCSVData(csvData);
        } else if (c == Wartung.class) {
            persistable = this.createMaintenanceFromCSVData(csvData);
        } else {
            throw new IllegalArgumentException("EntityFactor::createElement: Unknown class: " + c.getName());
        }

        this.entityManager.persist(persistable);
        this.resolveUnresolvedReferences();
        return persistable;
    }

    public void loadAllEntities() throws IOException {
        final var entityClasses = new Class<?>[] {
                Adresse.class,
                Ausruestung.class,
                Bereich.class,
                Buchung.class,
                Chipkarte.class,
                Einrichtung.class,
                Foto.class,
                Fremdfirma.class,
                Gast.class,
                GebuchteLeistung.class,
                Geraetschaft.class,
                Leistungsbeschreibung.class,
                Oeffnungstag.class,
                Oeffnungszeit.class,
                Person.class,
                Personal.class,
                Rechnung.class,
                Stellplatz.class,
                Stellplatzfunktion.class,
                Stoerung.class,
                Wartung.class
        };
        for (final var entityClass : entityClasses) {
            for (final var entity : database.read(entityClass)) {
                this.createElement(entityClass, entity.getCSVData());
            }
        }

        this.resolveUnresolvedReferences();
        // @formatter:off
        this.missingReferences.keySet().forEach(clazz ->
                this.missingReferences.get(clazz).keySet().forEach(primaryKey ->
                        this.missingReferences.get(clazz).get(primaryKey).forEach(consumer ->
                            AppLogger.getInstance().warning("EntityFactory::loadAllEntities: Could not resolve reference for " + clazz.getSimpleName() + " with primary key " + primaryKey)
                        )
                )
        );
        // @formatter:on
    }

    public void resolveUnresolvedReferences() {
        for (final var entity : this.entityManager.find()) {
            for (Class<?> clazz = entity.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
                final var keyToEntity = this.missingReferences.get(clazz);
                if (keyToEntity == null) {
                    continue;
                }

                final var callbacks = keyToEntity.get(entity.getPrimaryKey());
                if (callbacks == null) {
                    continue;
                }

                callbacks.forEach((cb) -> cb.accept(entity));
                callbacks.clear();
                keyToEntity.remove(entity.getPrimaryKey());
            }
        }
    }

    private IPersistable createAddressFromCSVData(final String[] csvData) {
        final var addressSupplement = csvData[Adresse.CSVPosition.ZUSATZ.ordinal()];
        return new Adresse(
                Integer.parseInt(csvData[Adresse.CSVPosition.ADRESSE_ID.ordinal()]),
                csvData[Adresse.CSVPosition.STRASSE.ordinal()],
                Integer.parseInt(csvData[Adresse.CSVPosition.HAUSNUMMER.ordinal()]),
                addressSupplement.equals("") ? Optional.empty() : Optional.of(addressSupplement),
                csvData[Adresse.CSVPosition.ORT.ordinal()],
                csvData[Adresse.CSVPosition.PLZ.ordinal()],
                Adresse.Land.valueOf(csvData[Adresse.CSVPosition.LAND.ordinal()])
        );
    }

    private IPersistable createAreaFromCSVData(final String[] csvData) {
        final var area = new Bereich(
                Integer.parseInt(csvData[Bereich.CSVPosition.ANLAGEID.ordinal()]),
                new GPSPosition(
                        Double.parseDouble(csvData[Bereich.CSVPosition.LAGE_LATITUDE.ordinal()]),
                        Double.parseDouble(csvData[Bereich.CSVPosition.LAGE_LONGITUDE.ordinal()])
                ),
                csvData[Bereich.CSVPosition.KENNZEICHEN.ordinal()].charAt(0),
                csvData[Bereich.CSVPosition.BESCHREIBUNG.ordinal()]
        );

        for (final var complexId : this.getListValues(csvData[Bereich.CSVPosition.ANLAGEN_IDS.ordinal()])) {
            this.onReferenceFound(Anlage.class, Integer.parseInt(complexId), area::addAnlage);
        }

        final var areaId = csvData[Bereich.CSVPosition.BEREICH_ID.ordinal()];
        if (!areaId.isEmpty()) {
            this.onReferenceFound(Bereich.class, Integer.parseInt(areaId), area::setBereich);
        }

        for (final var photoId : this.getListValues(csvData[Bereich.CSVPosition.FOTO_IDS.ordinal()])) {
            this.onReferenceFound(Foto.class, Integer.parseInt(photoId), area::addFoto);
        }

        return area;
    }

    private IPersistable createBookedServiceFromCSVData(final String[] csvData) {
        final var bookedService = new GebuchteLeistung(
                Integer.parseInt(csvData[GebuchteLeistung.CSVPosition.GEBUCHTE_LEISTUNG_ID.ordinal()]),
                LocalDate.parse(csvData[GebuchteLeistung.CSVPosition.BUCHUNG_START.ordinal()]),
                LocalDate.parse(csvData[GebuchteLeistung.CSVPosition.BUCHUNGS_ENDE.ordinal()])
        );

        final var serviceDescriptionId = Integer.parseInt(csvData[GebuchteLeistung.CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()]);
        this.onReferenceFound(Leistungsbeschreibung.class, serviceDescriptionId, bookedService::setLeistungsbeschreibung);

        return bookedService;
    }

    private IPersistable createBookingFromCSVData(final String[] csvData) {
        final var booking = new Buchung(
                Integer.parseInt(csvData[Buchung.CSVPosition.BUCHUNGSNUMMER.ordinal()]),
                LocalDateTime.parse(csvData[Buchung.CSVPosition.ANREISE.ordinal()]),
                LocalDateTime.parse(csvData[Buchung.CSVPosition.ABREISE.ordinal()])
        );

        final var pitchId = Integer.parseInt(csvData[Buchung.CSVPosition.GEBUCHTER_STELLPLATZ_ID.ordinal()]);
        this.onReferenceFound(Stellplatz.class, pitchId, booking::setGebuchterStellplatz);

        for (final var chipCardId : this.getListValues(csvData[Buchung.CSVPosition.AUSGEHAENDIGTE_CHIPKARTEN_IDS.ordinal()])) {
            this.onReferenceFound(Chipkarte.class,
                    Integer.parseInt(chipCardId),
                    booking::addAusgehaendigteChipkarte);
        }

        final var invoiceId = csvData[Buchung.CSVPosition.RECHNUNG_ID.ordinal()];
        if (!invoiceId.isEmpty()) {
            this.onReferenceFound(Rechnung.class, Integer.parseInt(invoiceId), booking::setRechnung);
        }

        for (final var associatedGuestId : this.getListValues(csvData[Buchung.CSVPosition.ZUGEHOERIGE_GAESTE_IDS.ordinal()])) {
            this.onReferenceFound(Gast.class, Integer.parseInt(associatedGuestId), booking::addZugehoerigerGast);
        }

        final var responsibleGuestId = Integer.parseInt(csvData[Buchung.CSVPosition.VERANTWORTLICHER_GAST_ID.ordinal()]);
        this.onReferenceFound(Gast.class, responsibleGuestId, booking::setVerantwortlicherGast);

        for (final var bookedServiceId : this.getListValues(csvData[Buchung.CSVPosition.GEBUCHTE_LEISTUNGEN_IDS.ordinal()])) {
            this.onReferenceFound(GebuchteLeistung.class, Integer.parseInt(bookedServiceId), booking::addGebuchteLeistung);
        }

        // TODO: equipment

        return booking;
    }

    private IPersistable createChipCardFromCSVData(final String[] csvData) {
        return new Chipkarte(
                Integer.parseInt(csvData[Chipkarte.CSVPosition.NUMMER.ordinal()]),
                Chipkarte.Status.valueOf(csvData[Chipkarte.CSVPosition.STATUS.ordinal()])
        );
    }

    private IPersistable createContractorFromCSVData(final String[] csvData) {
        final var contractor = new Fremdfirma(
                Integer.parseInt(csvData[Fremdfirma.CSVPosition.FREMDFIRMA_ID.ordinal()]),
                csvData[Fremdfirma.CSVPosition.NAME.ordinal()]
        );

        for (final var maintenanceId : this.getListValues(csvData[Fremdfirma.CSVPosition.WARTUNG_IDS.ordinal()])) {
            this.onReferenceFound(Wartung.class, Integer.parseInt(maintenanceId), contractor::addWartung);
        }

        for (final var facilityId : this.getListValues(csvData[Fremdfirma.CSVPosition.EINRICHTUNG_IDS.ordinal()])) {
            this.onReferenceFound(Einrichtung.class, Integer.parseInt(facilityId), contractor::addEinrichtung);
        }

        final var addressId = Integer.parseInt(csvData[Fremdfirma.CSVPosition.ANSCHRIFT_ID.ordinal()]);
        this.onReferenceFound(Adresse.class, addressId, contractor::setAnschrift);

        final var contactPersonId = Integer.parseInt(csvData[Fremdfirma.CSVPosition.ANSPRECHPERSON_ID.ordinal()]);
        this.onReferenceFound(Person.class, contactPersonId, contractor::setAnsprechperson);

        return contractor;
    }

    private IPersistable createDisturbanceFromCSVData(final String[] csvData) {
        final var disturbance = new Stoerung(
                Integer.parseInt(csvData[Stoerung.CSVPosition.STOERUNGSNUMMER.ordinal()]),
                csvData[Stoerung.CSVPosition.TITEL.ordinal()],
                csvData[Stoerung.CSVPosition.BESCHREIBUNG.ordinal()],
                LocalDateTime.parse(csvData[Stoerung.CSVPosition.ERSTELLUNGSDATUM.ordinal()]),
                LocalDateTime.parse(csvData[Stoerung.CSVPosition.BEHEBUNGSDATUM.ordinal()]),
                Stoerung.Status.valueOf(csvData[Stoerung.CSVPosition.STATUS.ordinal()])
        );

        final var pitchFunctionId = csvData[Stoerung.CSVPosition.STELLPLATZFUNKTION_ID.ordinal()];
        if (!pitchFunctionId.isEmpty()) {
            this.onReferenceFound(Stellplatzfunktion.class,
                    Integer.parseInt(pitchFunctionId),
                    disturbance::setStellplatzfunktion);
        }

        return disturbance;
    }

    private IPersistable createEquipmentFromCSVData(final String[] csvData) {
        // Equipment and Subclasses are implemented as Single Table Inheritance
        final var discriminator = csvData[Ausruestung.CSVPosition.DISCRIMINATOR.ordinal()];

        if (discriminator.equals(Fahrzeug.class.getSimpleName())) {
            return new Fahrzeug(
                    Integer.parseInt(csvData[Ausruestung.CSVPosition.AUSRUESTUNG_ID.ordinal()]),
                    csvData[Ausruestung.CSVPosition.BEZEICHNUNG.ordinal()],
                    Integer.parseInt(csvData[Ausruestung.CSVPosition.ANZAHL.ordinal()]),
                    Double.parseDouble(csvData[Ausruestung.CSVPosition.BREITE.ordinal()]),
                    Double.parseDouble(csvData[Ausruestung.CSVPosition.HOEHE.ordinal()]),
                    csvData[Ausruestung.CSVPosition.KENNZEICHEN.ordinal()],
                    Fahrzeug.Typ.valueOf(csvData[Ausruestung.CSVPosition.FAHRZEUGTYP.ordinal()])
            );
        }

        if (discriminator.equals(Ausruestung.class.getSimpleName())) {
            return new Ausruestung(
                    Integer.parseInt(csvData[Ausruestung.CSVPosition.AUSRUESTUNG_ID.ordinal()]),
                    csvData[Ausruestung.CSVPosition.BEZEICHNUNG.ordinal()],
                    Integer.parseInt(csvData[Ausruestung.CSVPosition.ANZAHL.ordinal()]),
                    Double.parseDouble(csvData[Ausruestung.CSVPosition.BREITE.ordinal()]),
                    Double.parseDouble(csvData[Ausruestung.CSVPosition.HOEHE.ordinal()])
            );
        }

        throw new IllegalArgumentException("EntityFactor::createElement: Unknown Ausruestungs discriminator: " + discriminator);
    }

    private IPersistable createFacilityFromCSVData(final String[] csvData) {
        final var facility = new Einrichtung(
                Integer.parseInt(csvData[Einrichtung.CSVPosition.ANLAGE_ID.ordinal()]),
                new GPSPosition(
                        Double.parseDouble(csvData[Einrichtung.CSVPosition.LAGE_LATITUDE.ordinal()]),
                        Double.parseDouble(csvData[Einrichtung.CSVPosition.LAGE_LONGITUDE.ordinal()])
                ),
                csvData[Einrichtung.CSVPosition.NAME.ordinal()],
                csvData[Einrichtung.CSVPosition.BESCHREIBUNG.ordinal()],
                LocalDateTime.parse(csvData[Einrichtung.CSVPosition.LETZTE_WARTUNG.ordinal()])
        );

        for (final var openingDayId : this.getListValues(csvData[Einrichtung.CSVPosition.OEFFNUNGSTAGE_IDS.ordinal()])) {
            this.onReferenceFound(Oeffnungstag.class, Integer.parseInt(openingDayId), facility::addOeffnungstag);
        }

        final var contractorId = csvData[Einrichtung.CSVPosition.ZUSTAENDIGE_FIRMA_ID.ordinal()];
        if (!contractorId.isEmpty()) {
            this.onReferenceFound(Fremdfirma.class, Integer.parseInt(contractorId), facility::setZustaendigeFirma);
        }

        final var areaId = csvData[Einrichtung.CSVPosition.BEREICH_ID.ordinal()];
        if (!areaId.isEmpty()) {
            this.onReferenceFound(Bereich.class, Integer.parseInt(areaId), facility::setBereich);
        }

        for (final var photoId : this.getListValues(csvData[Einrichtung.CSVPosition.FOTO_IDS.ordinal()])) {
            this.onReferenceFound(Foto.class, Integer.parseInt(photoId), facility::addFoto);
        }

        return facility;
    }

    private IPersistable createGuestFromCSVData(final String[] csvData) {
        final var guest = new Gast(
                csvData[Gast.CSVPosition.VORNAME.ordinal()],
                csvData[Gast.CSVPosition.NACHNAME.ordinal()],
                Gast.Geschlecht.valueOf(csvData[Gast.CSVPosition.GESCHLECHT.ordinal()]),
                csvData[Gast.CSVPosition.EMAIL.ordinal()],
                csvData[Gast.CSVPosition.TELEFONNUMMER.ordinal()],
                Integer.parseInt(csvData[Gast.CSVPosition.KUNDENNUMMER.ordinal()]),
                csvData[Gast.CSVPosition.AUSWEISNUMMER.ordinal()]
        );

        final var addressId = Integer.parseInt(csvData[Gast.CSVPosition.ANSCHRIFT.ordinal()]);
        this.onReferenceFound(Adresse.class, addressId, guest::setAnschrift);

        for (final var bookingId : this.getListValues(csvData[Gast.CSVPosition.BUCHUNG_IDS.ordinal()])) {
            this.onReferenceFound(Buchung.class, Integer.parseInt(bookingId), guest::addBuchung);
        }

        return guest;
    }

    private IPersistable createInvoiceFromCSVData(final String[] csvData) {
        final var invoice = new Rechnung(
                Integer.parseInt(csvData[Rechnung.CSVPosition.RECHNUNGSNUMMER.ordinal()]),
                LocalDate.parse(csvData[Rechnung.CSVPosition.RECHNUNGSDATUM.ordinal()]),
                new BigDecimal(csvData[Rechnung.CSVPosition.BETRAG_NETTO.ordinal()]),
                csvData[Rechnung.CSVPosition.ZAHLUNGSANWEISUNG.ordinal()],
                csvData[Rechnung.CSVPosition.BANKVERBINDUNG.ordinal()],
                csvData[Rechnung.CSVPosition.ZAHLUNGSZWECK.ordinal()],
                LocalDate.parse(csvData[Rechnung.CSVPosition.ZAHLUNGSZIEL.ordinal()])
        );

        final var guestId = Integer.parseInt(csvData[Rechnung.CSVPosition.ADRESSAT_ID.ordinal()]);
        this.onReferenceFound(Gast.class, guestId, invoice::setAdressat);

        return invoice;
    }

    private IPersistable createMaintenanceFromCSVData(final String[] csvData) {
        final var maintenance = new Wartung(
                Integer.parseInt(csvData[Wartung.CSVPosition.WARTUNGSNUMMER.ordinal()]),
                LocalDate.parse(csvData[Wartung.CSVPosition.DUERCHFUEHRUNGSDATUM.ordinal()]),
                LocalDate.parse(csvData[Wartung.CSVPosition.RECHNUNGSDATUM.ordinal()]),
                csvData[Wartung.CSVPosition.AUFTRAGSNUMMER.ordinal()],
                csvData[Wartung.CSVPosition.RECHNUNGSNUMMER.ordinal()],
                new BigDecimal(csvData[Wartung.CSVPosition.KOSTEN.ordinal()])
        );

        final var contractorId = csvData[Wartung.CSVPosition.ZUSTAENDIGE_FIRMA_ID.ordinal()];
        if (!contractorId.isEmpty()) {
            this.onReferenceFound(Fremdfirma.class, Integer.parseInt(contractorId), maintenance::setZustaendigeFirma);
        }

        final var complexId = Integer.parseInt(csvData[Wartung.CSVPosition.ANLAGE_ID.ordinal()]);
        this.onReferenceFound(Anlage.class, complexId, maintenance::setAnlage);

        return maintenance;
    }

    private IPersistable createOpeningDayFromCSVData(final String[] csvData) {
        final var openingDay = new Oeffnungstag(
                Integer.parseInt(csvData[Oeffnungstag.CSVPosition.OEFFNUNGSTAG_ID.ordinal()]),
                Oeffnungstag.Wochentag.valueOf(csvData[Oeffnungstag.CSVPosition.WOCHENTAG.ordinal()])
        );

        for (final var openingHourId : this.getListValues(csvData[Oeffnungstag.CSVPosition.OEFFNUNGSZEITEN_IDS.ordinal()])) {
            this.onReferenceFound(Oeffnungszeit.class, Integer.parseInt(openingHourId), openingDay::addOeffnungszeit);
        }

        return openingDay;
    }

    private IPersistable createOpeningHourFromCSVData(final String[] csvData) {
        return new Oeffnungszeit(
                Integer.parseInt(csvData[Oeffnungszeit.CSVPosition.OEFFNUNGSZEIT_ID.ordinal()]),
                LocalTime.parse(csvData[Oeffnungszeit.CSVPosition.START.ordinal()]),
                LocalTime.parse(csvData[Oeffnungszeit.CSVPosition.ENDE.ordinal()])
        );
    }

    private IPersistable createPersonFromCSVData(final String[] csvData) {
        return new Person(
                Integer.parseInt(csvData[Person.CSVPosition.PERSON_ID.ordinal()]),
                csvData[Person.CSVPosition.VORNAME.ordinal()],
                csvData[Person.CSVPosition.NACHNAME.ordinal()],
                Person.Geschlecht.valueOf(csvData[Person.CSVPosition.GESCHLECHT.ordinal()]),
                csvData[Person.CSVPosition.EMAIL.ordinal()],
                csvData[Person.CSVPosition.TELEFONNUMMER.ordinal()]
        );
    }

    private IPersistable createPersonalFromCSVData(final String[] csvData) {
        final var personal = new Personal(
                Integer.parseInt(csvData[Personal.CSVPosition.PERSONALNUMMER.ordinal()]),
                csvData[Personal.CSVPosition.VORNAME.ordinal()],
                csvData[Personal.CSVPosition.NACHNAME.ordinal()],
                Person.Geschlecht.valueOf(csvData[Personal.CSVPosition.GESCHLECHT.ordinal()]),
                csvData[Personal.CSVPosition.EMAIL.ordinal()],
                csvData[Personal.CSVPosition.TELEFONNUMMER.ordinal()],
                LocalDate.parse(csvData[Personal.CSVPosition.GEBURTSTAG.ordinal()]),
                Personal.Rolle.valueOf(csvData[Personal.CSVPosition.ROLLE.ordinal()])
        );

        for (final var disturbanceId : this.getListValues(csvData[Personal.CSVPosition.STOERUNGEN_IDS.ordinal()])) {
            this.onReferenceFound(Stoerung.class, Integer.parseInt(disturbanceId), personal::addStoerung);
        }

        return personal;
    }

    private IPersistable createPhotoFromCSVData(final String[] csvData) {
        return new Foto(
                Integer.parseInt(csvData[Foto.CSVPosition.FOTO_ID.ordinal()]),
                Path.of(csvData[Foto.CSVPosition.DATEIPFAD.ordinal()]),
                csvData[Foto.CSVPosition.TITEL.ordinal()],
                csvData[Foto.CSVPosition.BESCHREIBUNG.ordinal()]
        );
    }

    private IPersistable createPitchFromCSVData(final String[] csvData) {
        final var pitch = new Stellplatz(
                Integer.parseInt(csvData[Stellplatz.CSVPosition.ANLAGE_ID.ordinal()]),
                new GPSPosition(
                        Double.parseDouble(csvData[Stellplatz.CSVPosition.LAGE_LATITUDE.ordinal()]),
                        Double.parseDouble(csvData[Stellplatz.CSVPosition.LAGE_LONGITUDE.ordinal()])
                ),
                csvData[Stellplatz.CSVPosition.STELLPLATZ.ordinal()],
                new BigDecimal(csvData[Stellplatz.CSVPosition.GEBUEHR.ordinal()]),
                Double.parseDouble(csvData[Stellplatz.CSVPosition.GROESSE.ordinal()]),
                Boolean.parseBoolean(csvData[Stellplatz.CSVPosition.BARRIEREFREI.ordinal()]),
                Integer.parseInt(csvData[Stellplatz.CSVPosition.ANZAHL_WOHNWAGEN.ordinal()]),
                Integer.parseInt(csvData[Stellplatz.CSVPosition.ANZAHL_PKW.ordinal()]),
                Integer.parseInt(csvData[Stellplatz.CSVPosition.ANZAHL_ZELTE.ordinal()])
        );

        for (final var pitchFunctionId : this.getListValues(csvData[Stellplatz.CSVPosition.VERFUEGBARE_FUNKTIONEN_IDS.ordinal()])) {
            this.onReferenceFound(Stellplatzfunktion.class,
                    Integer.parseInt(pitchFunctionId),
                    pitch::addVerfuegbareFunktion);
        }

        final var areaId = csvData[Stellplatz.CSVPosition.BEREICH_ID.ordinal()];
        if (!areaId.isEmpty()) {
            this.onReferenceFound(Bereich.class, Integer.parseInt(areaId), pitch::setBereich);
        }

        for (final var photoId : this.getListValues(csvData[Stellplatz.CSVPosition.FOTO_IDS.ordinal()])) {
            this.onReferenceFound(Foto.class, Integer.parseInt(photoId), pitch::addFoto);
        }

        return pitch;
    }

    private IPersistable createPitchFunctionFromCSVData(final String[] csvData) {
        final var pitchFunction = new Stellplatzfunktion(
                Integer.parseInt(csvData[Stellplatzfunktion.CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()]),
                new BigDecimal(csvData[Stellplatzfunktion.CSVPosition.GEBUEHR.ordinal()]),
                Integer.parseInt(csvData[Stellplatzfunktion.CSVPosition.MAXIMAL_ANZAHL.ordinal()]),
                csvData[Stellplatzfunktion.CSVPosition.BESCHREIBUNG.ordinal()],
                Stellplatzfunktion.Status.valueOf(csvData[Stellplatzfunktion.CSVPosition.STATUS.ordinal()])
        );

        for (final var pitchId : this.getListValues(csvData[Stellplatzfunktion.CSVPosition.STELLPLATZ_IDS.ordinal()])) {
            this.onReferenceFound(Stellplatz.class, Integer.parseInt(pitchId), pitchFunction::addStellplatz);
        }

        return pitchFunction;
    }

    private IPersistable createToolFromCSVData(final String[] csvData) {
        return new Geraetschaft(
                Integer.parseInt(csvData[Geraetschaft.CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()]),
                new BigDecimal(csvData[Geraetschaft.CSVPosition.GEBUEHR.ordinal()]),
                Integer.parseInt(csvData[Geraetschaft.CSVPosition.MAXIMAL_ANZAHL.ordinal()]),
                csvData[Geraetschaft.CSVPosition.BESCHREIBUNG.ordinal()],
                LocalDate.parse(csvData[Geraetschaft.CSVPosition.ANSCHAFFUNGSDATUM.ordinal()]),
                csvData[Geraetschaft.CSVPosition.ZUSTAND.ordinal()]
        );
    }

    private Iterable<String> getListValues(final String csvData) {
        return Arrays.stream(csvData.trim().split(","))
                .filter(s -> s.length() > 0) // remove empty string (occurs when no entries are in the list)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T extends IPersistable> void onReferenceFound(final Class<T> c, final Object id, final Consumer<T> callback) {
        var keyToEntity = this.missingReferences.computeIfAbsent(c, k -> new HashMap<>());
        var callbacks = keyToEntity.computeIfAbsent(id, k -> new HashSet<>());

        callbacks.add((Consumer<IPersistable>) callback);

        final var entity = this.entityManager.findOne(c, id);
        if (entity.isPresent()) {
            callbacks.forEach((cb) -> cb.accept(entity.get()));
            callbacks.clear();
            keyToEntity.remove(entity.get().getPrimaryKey());
        }
    }
}
