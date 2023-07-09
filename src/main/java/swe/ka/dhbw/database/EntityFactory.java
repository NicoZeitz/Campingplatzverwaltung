package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.model.*;
import swe.ka.dhbw.util.Validator;

import java.io.IOException;
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

    public void loadAllEntities() throws IOException {
        final var entityClasses = new Class<?>[] {
                Adresse.class,
                Gast.class
        };
        for (final var entityClass : entityClasses) {
            for (final var entity : database.read(entityClass)) {
                this.createElement(entityClass, entity.getCSVData());
            }
        }

        this.resolveUnresolvedReferences();
    }

    public IPersistable createElement(final Class<?> c, final String[] csvData) {
        Validator.getInstance().validateNotNull(c);
        Validator.getInstance().validateNotNull(csvData);

        IPersistable persistable;
        if (c == Adresse.class) {
            persistable = this.createAdresseFromCSVData(csvData);
        } else if (c == Buchung.class) {
            persistable = this.createBuchungFromCSVData(csvData);
        } else if (c == Gast.class) {
            persistable = this.createGastFromCSVData(csvData);
        } else if (c == Oeffnungszeit.class) {
            persistable = this.createOeffnungszeitFromCSVData(csvData);
        } else if (c == Oeffnungstag.class) {
            persistable = this.createOeffnungstagFromCSVData(csvData);
        } else if (c == Ausruestung.class) {
            persistable = this.createAusruestungFromCSVData(csvData);
        } else {
            throw new IllegalArgumentException("EntityFactor::createElement: Unknown class: " + c.getName());
        }

        this.entityManager.persist(persistable);
        this.resolveUnresolvedReferences();
        return persistable;
    }

    public void resolveUnresolvedReferences() {
        for (final var entity : this.entityManager.getAll()) {
            final var keyToEntity = this.missingReferences.get(entity.getClass());
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

    private <T extends IPersistable> void onReferenceFound(final Class<T> c, final Object id, final Consumer<T> callback) {
        var keyToEntity = this.missingReferences.get(c);
        if (keyToEntity == null) {
            keyToEntity = new HashMap<>();
            this.missingReferences.put(c, keyToEntity);
        }

        var callbacks = this.missingReferences.get(c).get(id);
        if (callbacks == null) {
            callbacks = new HashSet<>();
            this.missingReferences.get(c).put(id, callbacks);
        }

        callbacks.add((Consumer<IPersistable>) callback);

        var entity = this.entityManager.findOne(c, id);
        if (entity.isPresent()) {
            callbacks.forEach((cb) -> cb.accept(entity.get()));
            callbacks.clear();
            keyToEntity.remove(entity.get().getPrimaryKey());
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
        return gast;
    }

    private IPersistable createOeffnungszeitFromCSVData(final String[] csvData) {
        return new Oeffnungszeit(
                Integer.parseInt(csvData[Oeffnungszeit.CSVPosition.OEFFNUNGSZEIT_ID.ordinal()]),
                LocalTime.parse(csvData[Oeffnungszeit.CSVPosition.START.ordinal()]),
                LocalTime.parse(csvData[Oeffnungszeit.CSVPosition.ENDE.ordinal()])
        );
    }

    private IPersistable createOeffnungstagFromCSVData(final String[] csvData) {
        final var oeffnungstag = new Oeffnungstag(
                Integer.parseInt(csvData[Oeffnungstag.CSVPosition.OEFFNUNGSTAG_ID.ordinal()]),
                Oeffnungstag.Wochentag.valueOf(csvData[Oeffnungstag.CSVPosition.WOCHENTAG.ordinal()])
        );
        for (final var oeffnungszeitId : csvData[Oeffnungstag.CSVPosition.OEFFNUNGSZEITEN_IDS.ordinal()].trim().split(",")) {
            this.onReferenceFound(Oeffnungszeit.class, Integer.parseInt(oeffnungszeitId), oeffnungstag::addOeffnungszeit);
        }
        return oeffnungstag;
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

        final var rechnungId = Integer.parseInt(csvData[Buchung.CSVPosition.RECHNUNG_ID.ordinal()]);
        this.onReferenceFound(Rechnung.class, rechnungId, buchung::setRechnung);

        for (final var zugehoerigerGastId : csvData[Buchung.CSVPosition.ZUGEHOERIGE_GAESTE_IDS.ordinal()].trim().split(",")) {
            this.onReferenceFound(Gast.class, Integer.parseInt(zugehoerigerGastId), buchung::addZugehoerigerGast);
        }

        final var verantwortlicherGastId = Integer.parseInt(csvData[Buchung.CSVPosition.VERANTWORTLICHER_GAST_ID.ordinal()]);
        this.onReferenceFound(Gast.class, verantwortlicherGastId, buchung::setVerantwortlicherGast);

        for (final var gebuchteLeistungId : csvData[Buchung.CSVPosition.GEBUCHTE_LEISTUNGEN_IDS.ordinal()].trim().split(",")) {
            this.onReferenceFound(GebuchteLeistung.class, Integer.parseInt(gebuchteLeistungId), buchung::addGebuchteLeistung);
        }

        return buchung;
    }
}
