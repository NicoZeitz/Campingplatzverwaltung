package swe.ka.dhbw.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Wartung {

    private int wartungsnummer;
    private LocalDate duerchfuehrungsdatum;
    private LocalDate rechnungsdatum;
    private String auftragsnummer;
    private String rechnungsnummer;
    private BigDecimal kosten;
    private Fremdfirma zustaendigeFirma;
    private Anlage anlage;

}
