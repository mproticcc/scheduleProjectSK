package specifikacija;

import exceptions.DatumUIzuzetomDanuException;
import klase.Prostorija;
import klase.Raspored;
import klase.Termin;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public interface DodelaTermina {


    boolean kreirajTerminUzPk(DayOfWeek day, LocalDateTime pocetakPerioda, LocalDateTime krajPerioda, Raspored r, Prostorija p, String dodatneStvari) throws Exception;

    Termin kreirajTerminPt(LocalDateTime pocetak, int trajanje, Prostorija prostorija, Raspored raspored);

    boolean brisanjeTermina(LocalDateTime pocetak, LocalDateTime kraj, Raspored raspored);

    boolean premestajTermina(DayOfWeek day, LocalDateTime pocetakPerioda, LocalDateTime krajPerioda, Raspored r);

    void izlistavanjeSlobodniTermini(String kriterijum,Raspored raspored);

    void izlisatavnjeZauzetihTermina(String podatak, Raspored raspored);

    void isProstorijaZauzeta(Prostorija prostorija, Raspored raspored);

    void isTerminSlobodan(LocalDateTime pocetak1,LocalDateTime kraj1, Raspored raspored);


}
