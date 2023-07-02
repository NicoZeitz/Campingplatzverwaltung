package swe.ka.dhbw.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Rechnung {

    private LocalDate rechnungsdatum;
    private int rechnungsnummer;
    private BigDecimal betragNetto;
    private String zahlungsanweisung;
    private String bankverbindung;
    private String zahlungszweck;
    private LocalDate zahlungsziel;
    private Gast adressat;

}
