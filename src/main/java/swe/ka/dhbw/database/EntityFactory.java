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

    public IPersistable createElement(final Class<?> c, final String[] csvData) {
        Validator.getInstance().validateNotNull(c);
        Validator.getInstance().validateNotNull(csvData);

        IPersistable persistable;
        if (c == Adresse.class) {
            persistable = this.createAdresseFromCSVData(csvData);
        } else if (c == Anlage.class) {
            throw new IllegalArgumentException("EntityFactor::createElement: Anlage is abstract");
        } else if (c == Ausruestung.class || c == Fahrzeug.class) {
            persistable = this.createAusruestungFromCSVData(csvData);
        } else if (c == Bereich.class) {
            persistable = this.createBereichFromCSVData(csvData);
        } else if (c == Buchung.class) {
            persistable = this.createBuchungFromCSVData(csvData);
        } else if (c == Chipkarte.class) {
            persistable = this.createChipkarteFromCSVData(csvData);
        } else if (c == Einrichtung.class) {
            persistable = this.createEinrichtungFromCSVData(csvData);
        } else if (c == Foto.class) {
            persistable = this.createFotoFromCSVData(csvData);
        } else if (c == Fremdfirma.class) {
            persistable = this.createFremdfirmaFromCSVData(csvData);
        } else if (c == Gast.class) {
            persistable = this.createGastFromCSVData(csvData);
        } else if (c == GebuchteLeistung.class) {
            persistable = this.createGebuchteLeistungFromCSVData(csvData);
        } else if (c == Geraetschaft.class) {
            persistable = this.createGeraetschaftFromCSVData(csvData);
        } else if (c == GPSPosition.class) {
            throw new IllegalArgumentException("EntityFactor::createElement: GPSPosition cannot be created by itself");
        } else if (c == Leistungsbeschreibung.class) {
            throw new IllegalArgumentException("EntityFactor::createElement: Leistungsbeschreibung is abstract");
        } else if (c == Oeffnungstag.class) {
            persistable = this.createOeffnungstagFromCSVData(csvData);
        } else if (c == Oeffnungszeit.class) {
            persistable = this.createOeffnungszeitFromCSVData(csvData);
        } else if (c == Person.class) {
            persistable = this.createPersonFromCSVData(csvData);
        } else if (c == Personal.class) {
            persistable = this.createPersonalFromCSVData(csvData);
        } else if (c == Rechnung.class) {
            persistable = this.createRechnungFromCSVData(csvData);
        } else if (c == Stellplatz.class) {
            persistable = this.createStellplatzFromCSVData(csvData);
        } else if (c == Stellplatzfunktion.class) {
            persistable = this.createStellplatzfunktionFromCSVData(csvData);
        } else if (c == Stoerung.class) {
            persistable = this.createStoerungFromCSVData(csvData);
        } else if (c == Wartung.class) {
            persistable = this.createWartungFromCSVData(csvData);
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
                        this.missingReferences.get(clazz).get(primaryKey).forEach(consumer -> {
                            AppLogger.getInstance().warning("EntityFactory::loadAllEntities: Could not resolve reference for " + clazz.getSimpleName() + " with primary key " + primaryKey);
                        })
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

    private IPersistable createAdresseFromCSVData(final String[] csvData) {
        final var zusatz = csvData[Adresse.CSVPosition.ZUSATZ.ordinal()];
        return new Adresse(
                Integer.parseInt(csvData[Adresse.CSVPosition.ADRESSE_ID.ordinal()]),
                csvData[Adresse.CSVPosition.STRASSE.ordinal()],
                Integer.parseInt(csvData[Adresse.CSVPosition.HAUSNUMMER.ordinal()]),
                zusatz.equals("") ? Optional.empty() : Optional.of(zusatz),
                csvData[Adresse.CSVPosition.ORT.ordinal()],
                csvData[Adresse.CSVPosition.PLZ.ordinal()],
                Adresse.Land.valueOf(csvData[Adresse.CSVPosition.LAND.ordinal()])
        );
    }

    private IPersistable createAusruestungFromCSVData(final String[] csvData) {
        // Ausruestung und Unterklassen sind als Single Table Inheritance implementiert
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

    private IPersistable createBereichFromCSVData(final String[] csvData) {
        final var bereich = new Bereich(
                Integer.parseInt(csvData[Bereich.CSVPosition.ANLAGEID.ordinal()]),
                new GPSPosition(
                        Double.parseDouble(csvData[Bereich.CSVPosition.LAGE_LATITUDE.ordinal()]),
                        Double.parseDouble(csvData[Bereich.CSVPosition.LAGE_LONGITUDE.ordinal()])
                ),
                csvData[Bereich.CSVPosition.KENNZEICHEN.ordinal()].charAt(0),
                csvData[Bereich.CSVPosition.BESCHREIBUNG.ordinal()]
        );

        for (final var anlageId : this.getListValues(csvData[Bereich.CSVPosition.ANLAGEN_IDS.ordinal()])) {
            this.onReferenceFound(Anlage.class, Integer.parseInt(anlageId), bereich::addAnlage);
        }

        final var bereichId = csvData[Bereich.CSVPosition.BEREICH_ID.ordinal()];
        if (!bereichId.isEmpty()) {
            this.onReferenceFound(Bereich.class, Integer.parseInt(bereichId), bereich::setBereich);
        }

        for (final var fotoId : this.getListValues(csvData[Bereich.CSVPosition.FOTO_IDS.ordinal()])) {
            this.onReferenceFound(Foto.class, Integer.parseInt(fotoId), bereich::addFoto);
        }

        return bereich;
    }

    private IPersistable createBuchungFromCSVData(final String[] csvData) {
        final var buchung = new Buchung(
                Integer.parseInt(csvData[Buchung.CSVPosition.BUCHUNGSNUMMER.ordinal()]),
                LocalDateTime.parse(csvData[Buchung.CSVPosition.ANREISE.ordinal()]),
                LocalDateTime.parse(csvData[Buchung.CSVPosition.ABREISE.ordinal()])
        );

        final var stellplatzId = Integer.parseInt(csvData[Buchung.CSVPosition.GEBUCHTER_STELLPLATZ_ID.ordinal()]);
        this.onReferenceFound(Stellplatz.class, stellplatzId, buchung::setGebuchterStellplatz);

        for (final var ausgehaendigteChipkartenId : csvData[Buchung.CSVPosition.AUSGEHAENDIGTE_CHIPKARTEN_IDS.ordinal()].trim()
                .split(",")) {
            this.onReferenceFound(Chipkarte.class,
                    Integer.parseInt(ausgehaendigteChipkartenId),
                    buchung::addAusgehaendigteChipkarte);
        }

        final var rechnungId = csvData[Buchung.CSVPosition.RECHNUNG_ID.ordinal()];
        if (!rechnungId.isEmpty()) {
            this.onReferenceFound(Rechnung.class, Integer.parseInt(rechnungId), buchung::setRechnung);
        }

        for (final var zugehoerigerGastId : this.getListValues(csvData[Buchung.CSVPosition.ZUGEHOERIGE_GAESTE_IDS.ordinal()])) {
            this.onReferenceFound(Gast.class, Integer.parseInt(zugehoerigerGastId), buchung::addZugehoerigerGast);
        }

        final var verantwortlicherGastId = Integer.parseInt(csvData[Buchung.CSVPosition.VERANTWORTLICHER_GAST_ID.ordinal()]);
        this.onReferenceFound(Gast.class, verantwortlicherGastId, buchung::setVerantwortlicherGast);

        for (final var gebuchteLeistungId : this.getListValues(csvData[Buchung.CSVPosition.GEBUCHTE_LEISTUNGEN_IDS.ordinal()])) {
            this.onReferenceFound(GebuchteLeistung.class, Integer.parseInt(gebuchteLeistungId), buchung::addGebuchteLeistung);
        }

        return buchung;
    }

    private IPersistable createChipkarteFromCSVData(final String[] csvData) {
        return new Chipkarte(
                Integer.parseInt(csvData[Chipkarte.CSVPosition.NUMMER.ordinal()]),
                Chipkarte.Status.valueOf(csvData[Chipkarte.CSVPosition.STATUS.ordinal()])
        );
    }

    private IPersistable createEinrichtungFromCSVData(final String[] csvData) {
        final var einrichtung = new Einrichtung(
                Integer.parseInt(csvData[Einrichtung.CSVPosition.ANLAGE_ID.ordinal()]),
                new GPSPosition(
                        Double.parseDouble(csvData[Einrichtung.CSVPosition.LAGE_LATITUDE.ordinal()]),
                        Double.parseDouble(csvData[Einrichtung.CSVPosition.LAGE_LONGITUDE.ordinal()])
                ),
                csvData[Einrichtung.CSVPosition.NAME.ordinal()],
                csvData[Einrichtung.CSVPosition.BESCHREIBUNG.ordinal()],
                LocalDateTime.parse(csvData[Einrichtung.CSVPosition.LETZTE_WARTUNG.ordinal()])
        );

        for (final var oeffnungstagId : this.getListValues(csvData[Einrichtung.CSVPosition.OEFFNUNGSTAGE_IDS.ordinal()])) {
            this.onReferenceFound(Oeffnungstag.class, Integer.parseInt(oeffnungstagId), einrichtung::addOeffnungstag);
        }

        final var fremdfirmaId = csvData[Einrichtung.CSVPosition.ZUSTAENDIGE_FIRMA_ID.ordinal()];
        if (!fremdfirmaId.isEmpty()) {
            this.onReferenceFound(Fremdfirma.class, Integer.parseInt(fremdfirmaId), einrichtung::setZustaendigeFirma);
        }

        final var bereichId = csvData[Einrichtung.CSVPosition.BEREICH_ID.ordinal()];
        if (!bereichId.isEmpty()) {
            this.onReferenceFound(Bereich.class, Integer.parseInt(bereichId), einrichtung::setBereich);
        }

        for (final var fotoId : this.getListValues(csvData[Einrichtung.CSVPosition.FOTO_IDS.ordinal()])) {
            this.onReferenceFound(Foto.class, Integer.parseInt(fotoId), einrichtung::addFoto);
        }

        return einrichtung;
    }

    private IPersistable createFotoFromCSVData(final String[] csvData) {
        return new Foto(
                Integer.parseInt(csvData[Foto.CSVPosition.FOTO_ID.ordinal()]),
                Path.of(csvData[Foto.CSVPosition.DATEIPFAD.ordinal()]),
                csvData[Foto.CSVPosition.TITEL.ordinal()],
                csvData[Foto.CSVPosition.BESCHREIBUNG.ordinal()]
        );
    }

    private IPersistable createFremdfirmaFromCSVData(final String[] csvData) {
        final var fremdfirma = new Fremdfirma(
                Integer.parseInt(csvData[Fremdfirma.CSVPosition.FREMDFIRMA_ID.ordinal()]),
                csvData[Fremdfirma.CSVPosition.NAME.ordinal()]
        );

        for (final var wartungsId : this.getListValues(csvData[Fremdfirma.CSVPosition.WARTUNG_IDS.ordinal()])) {
            this.onReferenceFound(Wartung.class, Integer.parseInt(wartungsId), fremdfirma::addWartung);
        }

        for (final var einrichtungsId : this.getListValues(csvData[Fremdfirma.CSVPosition.EINRICHTUNG_IDS.ordinal()])) {
            this.onReferenceFound(Einrichtung.class, Integer.parseInt(einrichtungsId), fremdfirma::addEinrichtung);
        }

        final var anschriftId = Integer.parseInt(csvData[Fremdfirma.CSVPosition.ANSCHRIFT_ID.ordinal()]);
        this.onReferenceFound(Adresse.class, anschriftId, fremdfirma::setAnschrift);

        final var ansprechpersonId = Integer.parseInt(csvData[Fremdfirma.CSVPosition.ANSPRECHPERSON_ID.ordinal()]);
        this.onReferenceFound(Person.class, ansprechpersonId, fremdfirma::setAnsprechperson);

        return fremdfirma;
    }

    private IPersistable createGastFromCSVData(final String[] csvData) {
        final var gast = new Gast(
                csvData[Gast.CSVPosition.VORNAME.ordinal()],
                csvData[Gast.CSVPosition.NACHNAME.ordinal()],
                Gast.Geschlecht.valueOf(csvData[Gast.CSVPosition.GESCHLECHT.ordinal()]),
                csvData[Gast.CSVPosition.EMAIL.ordinal()],
                csvData[Gast.CSVPosition.TELEFONNUMMER.ordinal()],
                Integer.parseInt(csvData[Gast.CSVPosition.KUNDENNUMMER.ordinal()]),
                csvData[Gast.CSVPosition.AUSWEISNUMMER.ordinal()]
        );

        final var anschriftId = Integer.parseInt(csvData[Gast.CSVPosition.ANSCHRIFT.ordinal()]);
        this.onReferenceFound(Adresse.class, anschriftId, gast::setAnschrift);

        for (final var buchungId : this.getListValues(csvData[Gast.CSVPosition.BUCHUNG_IDS.ordinal()])) {
            this.onReferenceFound(Buchung.class, Integer.parseInt(buchungId), gast::addBuchung);
        }

        return gast;
    }

    private IPersistable createGebuchteLeistungFromCSVData(final String[] csvData) {
        final var gebuchteLeistung = new GebuchteLeistung(
                Integer.parseInt(csvData[GebuchteLeistung.CSVPosition.GEBUCHTE_LEISTUNG_ID.ordinal()]),
                LocalDate.parse(csvData[GebuchteLeistung.CSVPosition.BUCHUNG_START.ordinal()]),
                LocalDate.parse(csvData[GebuchteLeistung.CSVPosition.BUCHUNGS_ENDE.ordinal()])
        );

        final var leistungsBeschreibungId = Integer.parseInt(csvData[GebuchteLeistung.CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()]);
        this.onReferenceFound(Leistungsbeschreibung.class, leistungsBeschreibungId, gebuchteLeistung::setLeistungsbeschreibung);

        return gebuchteLeistung;
    }

    private IPersistable createGeraetschaftFromCSVData(final String[] csvData) {
        return new Geraetschaft(
                Integer.parseInt(csvData[Geraetschaft.CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()]),
                new BigDecimal(csvData[Geraetschaft.CSVPosition.GEBUEHR.ordinal()]),
                Integer.parseInt(csvData[Geraetschaft.CSVPosition.MAXIMAL_ANZAHL.ordinal()]),
                csvData[Geraetschaft.CSVPosition.BESCHREIBUNG.ordinal()],
                LocalDate.parse(csvData[Geraetschaft.CSVPosition.ANSCHAFFUNGSDATUM.ordinal()]),
                csvData[Geraetschaft.CSVPosition.ZUSTAND.ordinal()]
        );
    }

    private IPersistable createOeffnungstagFromCSVData(final String[] csvData) {
        final var oeffnungstag = new Oeffnungstag(
                Integer.parseInt(csvData[Oeffnungstag.CSVPosition.OEFFNUNGSTAG_ID.ordinal()]),
                Oeffnungstag.Wochentag.valueOf(csvData[Oeffnungstag.CSVPosition.WOCHENTAG.ordinal()])
        );

        for (final var oeffnungszeitId : this.getListValues(csvData[Oeffnungstag.CSVPosition.OEFFNUNGSZEITEN_IDS.ordinal()])) {
            this.onReferenceFound(Oeffnungszeit.class, Integer.parseInt(oeffnungszeitId), oeffnungstag::addOeffnungszeit);
        }

        return oeffnungstag;
    }

    private IPersistable createOeffnungszeitFromCSVData(final String[] csvData) {
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

        for (final var stoerungsId : this.getListValues(csvData[Personal.CSVPosition.STOERUNGEN_IDS.ordinal()])) {
            this.onReferenceFound(Stoerung.class, Integer.parseInt(stoerungsId), personal::addStoerung);
        }

        return personal;
    }

    private IPersistable createRechnungFromCSVData(final String[] csvData) {
        final var rechnung = new Rechnung(
                Integer.parseInt(csvData[Rechnung.CSVPosition.RECHNUNGSNUMMER.ordinal()]),
                LocalDate.parse(csvData[Rechnung.CSVPosition.RECHNUNGSDATUM.ordinal()]),
                new BigDecimal(csvData[Rechnung.CSVPosition.BETRAG_NETTO.ordinal()]),
                csvData[Rechnung.CSVPosition.ZAHLUNGSANWEISUNG.ordinal()],
                csvData[Rechnung.CSVPosition.BANKVERBINDUNG.ordinal()],
                csvData[Rechnung.CSVPosition.ZAHLUNGSZWECK.ordinal()],
                LocalDate.parse(csvData[Rechnung.CSVPosition.ZAHLUNGSZIEL.ordinal()])
        );

        final var gastId = Integer.parseInt(csvData[Rechnung.CSVPosition.ADRESSAT_ID.ordinal()]);
        this.onReferenceFound(Gast.class, gastId, rechnung::setAdressat);

        return rechnung;
    }

    private IPersistable createStellplatzFromCSVData(final String[] csvData) {
        final var stellplatz = new Stellplatz(
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

        for (final var stellplatzfunktionId : csvData[Stellplatz.CSVPosition.VERFUEGBARE_FUNKTIONEN_IDS.ordinal()].trim()
                .split(",")) {
            this.onReferenceFound(Stellplatzfunktion.class,
                    Integer.parseInt(stellplatzfunktionId),
                    stellplatz::addVerfuegbareFunktion);
        }

        final var bereichId = csvData[Stellplatz.CSVPosition.BEREICH_ID.ordinal()];
        if (!bereichId.isEmpty()) {
            this.onReferenceFound(Bereich.class, Integer.parseInt(bereichId), stellplatz::setBereich);
        }

        for (final var fotoId : this.getListValues(csvData[Stellplatz.CSVPosition.FOTO_IDS.ordinal()])) {
            this.onReferenceFound(Foto.class, Integer.parseInt(fotoId), stellplatz::addFoto);
        }

        return stellplatz;
    }

    private IPersistable createStellplatzfunktionFromCSVData(final String[] csvData) {
        final var stellplatzFunktion = new Stellplatzfunktion(
                Integer.parseInt(csvData[Stellplatzfunktion.CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()]),
                new BigDecimal(csvData[Stellplatzfunktion.CSVPosition.GEBUEHR.ordinal()]),
                Integer.parseInt(csvData[Stellplatzfunktion.CSVPosition.MAXIMAL_ANZAHL.ordinal()]),
                csvData[Stellplatzfunktion.CSVPosition.BESCHREIBUNG.ordinal()],
                Stellplatzfunktion.Status.valueOf(csvData[Stellplatzfunktion.CSVPosition.STATUS.ordinal()])
        );

        for (final var stellplatzId : this.getListValues(csvData[Stellplatzfunktion.CSVPosition.STELLPLATZ_IDS.ordinal()])) {
            this.onReferenceFound(Stellplatz.class, Integer.parseInt(stellplatzId), stellplatzFunktion::addStellplatz);
        }

        return stellplatzFunktion;
    }

    private IPersistable createStoerungFromCSVData(final String[] csvData) {
        final var stoerung = new Stoerung(
                Integer.parseInt(csvData[Stoerung.CSVPosition.STOERUNGSNUMMER.ordinal()]),
                csvData[Stoerung.CSVPosition.TITEL.ordinal()],
                csvData[Stoerung.CSVPosition.BESCHREIBUNG.ordinal()],
                LocalDateTime.parse(csvData[Stoerung.CSVPosition.ERSTELLUNGSDATUM.ordinal()]),
                LocalDateTime.parse(csvData[Stoerung.CSVPosition.BEHEBUNGSDATUM.ordinal()]),
                Stoerung.Status.valueOf(csvData[Stoerung.CSVPosition.STATUS.ordinal()])
        );

        final var stellplatzFunktionId = csvData[Stoerung.CSVPosition.STELLPLATZFUNKTION_ID.ordinal()];
        if (!stellplatzFunktionId.isEmpty()) {
            this.onReferenceFound(Stellplatzfunktion.class,
                    Integer.parseInt(stellplatzFunktionId),
                    stoerung::setStellplatzfunktion);
        }

        return stoerung;
    }

    private IPersistable createWartungFromCSVData(final String[] csvData) {
        final var wartung = new Wartung(
                Integer.parseInt(csvData[Wartung.CSVPosition.WARTUNGSNUMMER.ordinal()]),
                LocalDate.parse(csvData[Wartung.CSVPosition.DUERCHFUEHRUNGSDATUM.ordinal()]),
                LocalDate.parse(csvData[Wartung.CSVPosition.RECHNUNGSDATUM.ordinal()]),
                csvData[Wartung.CSVPosition.AUFTRAGSNUMMER.ordinal()],
                csvData[Wartung.CSVPosition.RECHNUNGSNUMMER.ordinal()],
                new BigDecimal(csvData[Wartung.CSVPosition.KOSTEN.ordinal()])
        );

        final var fremdFirmaId = csvData[Wartung.CSVPosition.ZUSTAENDIGE_FIRMA_ID.ordinal()];
        if (!fremdFirmaId.isEmpty()) {
            this.onReferenceFound(Fremdfirma.class, Integer.parseInt(fremdFirmaId), wartung::setZustaendigeFirma);
        }

        final var anlageId = Integer.parseInt(csvData[Wartung.CSVPosition.ANLAGE_ID.ordinal()]);
        this.onReferenceFound(Anlage.class, anlageId, wartung::setAnlage);

        return wartung;
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
