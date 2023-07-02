package swe.ka.dhbw.model;

import java.time.LocalDateTime;

public class Einrichtung extends Anlage{

    private String name;
    private String beschreibung;
    private LocalDateTime letzteWartung;
    private Oeffnungstag oeffnungstag;
    private Fremdfirma zustaendigeFirma;

}
