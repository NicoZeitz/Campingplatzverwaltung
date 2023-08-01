# Campingplatzverwaltung

Software für Campingplatzverwaltung für Softwareengineering

# TODO

bei zweiseitigen Beziehungen die set/add/remvoe Methoden anpassen
bei mehrseitigen Beziheungen die equals/string/hashcode Methoden anpassen
FOTO klasse

# Dummy Data

Java string split (was in CSVReader/CSVWriter verwendet wird) splitted string wie "a;;b;;;" in [a, null, b] auf.
Ignoriert also leere Einträge am Ende (und nur am Ende). Deswegen endet jede von unseren CSV Dateien mit einem
DUMMY_DATA NULL

# Änderungen

# Weiterer Plan

Beide

- [ ] Ausrüstungsdialog anschauen (kann sein, dass da noch ein Bug ist)
- [ ] Buchungsliste: Sucheingabe kann nicht einfach gelöscht werden
- [ ] Benötigte Java Version in der Bedinungsanleitung/Doku eintragen
- [ ] Test auf Linux
- [ ] Löschen der Config-Datei vor Meisterübergabe

Fabian:

- [x] Aktion abbrechen -> Antworten YES / NO (JOptionPane) global -> guiCont openMain
- [ ] Fahrzeugtyp sollte Leerfeld besitzen
- [ ] Bedienungsanleitung-Start
- [ ] Anforderungen durchlesen

Nico:

- [x] Fenster X Clicken Während man am bearbeiten ist ist erlaubt
- [x] Validierung von Stellplatzfunktion
- [x] GUIController
    - [x] Handlers: look that all handlers treat collections as immutable, don't get as payload, check before
      fire update
    - [x] Dialog: don't pass data in constructor but in processUpdateEvent (CalendarComponent is impossible as Lutz does
      not allow setting the date after creating the dialog)
- [x] Buchung/Ausruestung/EntityFactory/GUIController/BookingCreateComponent: Equipment is not saved / associated
- [x] GUIConfiguration: The window location is not always saved
- [x] BookingCreateComponent
    - [x] GUI: Disable chip card selector iff "" is the only entry
    - [x] GUI: Sort Booked Services by Time
    - [x] GUI: Sort Equipment by ???
    - [x] BookingIds of Guest don't get updated
    - [x] BookingEditComponent: Modify BookingCreateComponent to allow editing
        - add a new button to delete the booking
- [x] BookingListComponent
    - [x] Sort Bookings by Time
    - [x] Search Bookings (Buchungen werden über ihre Termindaten gesucht. Termindaten werden über
      einen Zeitraum gesucht (Start- und Enddatum im Format DD.MM.YYYY). Alle Termine, die im gesamten Zeitraum zwischen
      Start- und Enddatum mindestens einen gebuchten Tag beinhalten, werden beim Ergebnis angezeigt)
- [x] Payload: Move classes somewhere else
- [ ] Documentation: What was changed in the implementation
    - Stoerung daten mit Zeit
    - Anlage zu abstrakter Klasse gemacht
    - GUIObserver zu abstrakter Klasse gemacht
    - Attribute in Person, ... protected gemacht
- [ ] Document swe utils bugs (BUG:SWE_UTILS:)
    - [ ] CalendarComponent null pointer exception
    - [ ] SimpleTableComponent adds a new listener every time the data is updated
    - [ ] builder methoden setzten bei .observer() 2 mal den observer einmal in der Methode und einmal beim build