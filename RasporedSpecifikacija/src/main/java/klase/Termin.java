package klase;

import com.google.gson.annotations.Expose;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Termin {
    @Expose
    private LocalDateTime pocetakPerioda;
    @Expose
    private LocalDateTime krajPerioda;
    @Expose
    private List<LocalDate> vremeOdrzavanja;
    private int trajanje;
    @Expose
    private Prostorija prostorija;
    @Expose
    private Map<String,String> dodatneStvari;
    @Expose
    private DayOfWeek day;

    public Termin(LocalDateTime pocetak, int trajanje, Prostorija prostorija) {
        this.pocetakPerioda = pocetak;
        this.trajanje = trajanje;
        this.prostorija = prostorija;
    }
    public Termin(LocalDateTime pocetakPerioda,LocalDateTime krajPerioda,Prostorija p){
        this.pocetakPerioda = pocetakPerioda;
        this.krajPerioda=krajPerioda;
        vremeOdrzavanja = new ArrayList<>();
        this.prostorija=p;
        dodatneStvari = new HashMap<>();
    }

    public Termin(Map<String, String> dodatneStvari) {
        this.dodatneStvari = dodatneStvari;
    }

    public Termin() {
        dodatneStvari = new HashMap<>();
    }

    public Termin(LocalDateTime pocetak, LocalDateTime kraj, Prostorija prostorija, Map<String, String> dodatneStvari) {
        this.pocetakPerioda = pocetak;
        this.krajPerioda = kraj;
        this.prostorija = prostorija;
        this.dodatneStvari = dodatneStvari;
    }

    public void setPocetakPerioda(LocalDateTime pocetakPerioda) {
        this.pocetakPerioda = pocetakPerioda;
    }

    public void setKrajPerioda(LocalDateTime krajPerioda) {
        this.krajPerioda = krajPerioda;
    }

    public void setVremeOdrzavanja(List<LocalDate> vremeOdrzavanja) {
        this.vremeOdrzavanja = vremeOdrzavanja;
    }

    public void odradiVremeOdrzavanja(){
        LocalDateTime odrzavanje = pocetakPerioda;
        LocalDateTime krajOdrzavanja = krajPerioda;
        if(!pocetakPerioda.isEqual(krajPerioda)) {
            while (odrzavanje.isBefore(krajOdrzavanja)) {
                if(odrzavanje.getDayOfWeek() == this.day)
                this.vremeOdrzavanja.add(odrzavanje.toLocalDate());

                odrzavanje = odrzavanje.plusDays(1);
            }
        }
    }



    public LocalDateTime getPocetakPerioda() {
        return pocetakPerioda;
    }

    public LocalDateTime getKrajPerioda() {
        return krajPerioda;
    }

    public List<LocalDate> getVremeOdrzavanja() {
        return vremeOdrzavanja;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public Prostorija getProstorija() {
        return prostorija;
    }

    public void setProstorija(Prostorija prostorija) {
        this.prostorija = prostorija;
    }

    public Map<String, String> getDodatneStvari() {
        return dodatneStvari;
    }

    public void setDodatneStvari(Map<String, String> dodatneStvari) {
        this.dodatneStvari = dodatneStvari;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Termin{" +
                "pocetak=" + pocetakPerioda +
                ", kraj=" + krajPerioda +
                ", trajanje=" + trajanje +
                ", prostorija=" + prostorija +
                ", dodatneStvari=" + dodatneStvari +
                '}' + '\n';
    }
}
