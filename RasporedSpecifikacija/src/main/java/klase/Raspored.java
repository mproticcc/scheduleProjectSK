package klase;


import specifikacija.DodelaTermina;
import specifikacija.ImportExport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Raspored  {

   private List<Termin> termini;

   private LocalDate from;

   private LocalDate to;

   private LocalTime hourFrom; //-------> ovo sve oznacava od kad do kad ce trajati raspored

   private LocalTime hourTo;

   private List<LocalDate> izuzetiDani;

   // promenjen konstruktor rasporeda za inicijalizaciju samog raspored
    // Raspored sada ima od kad i do kad traje kao i od koliko i do koliko sati svakoga dana

    // Drugi konstruktor se samo koristi u svrhe metoda da se ne bi pravila nova trajanja


   public Raspored(){

       try {
           initializeSchedule();
       }
       catch(Exception e) {
           e.printStackTrace();
         }

   }


   public void initializeSchedule() throws ParseException {

       System.out.println("Unesite od kad do kad ce trajati raspored: ");
       // 12.10.2023 12.12.2023 8 - 10  00-24h format
       Scanner sc = new Scanner(System.in); //
       String[] datum = sc.nextLine().split(" ");
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.uuuu");

       this.from = LocalDate.parse(datum[0],formatter);

       this.to = LocalDate.parse(datum[1],formatter);

       //this.from = new SimpleDateFormat("dd.MM.yyyy").parse(datum[0]);

       //this.to = new SimpleDateFormat("dd.MM.yyyy").parse(datum[1]);

       this.hourFrom = LocalTime.parse(datum[2]);

       this.hourTo = LocalTime.parse(datum[3]);

   }


    public void addTermin(Termin t){
        termini.add(t);
    }

    public List<Termin> getTermini() {
        return termini;
    }

    public void setTermini(List<Termin> termini) {
        this.termini = termini;
    }

    @Override
    public String toString() {
        return "Raspored{" +
                "termini=" + termini +
                '}' +  '\n';
    }


    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public LocalTime getHourFrom() {
        return hourFrom;
    }

    public void setHourFrom(LocalTime hourFrom) {
        this.hourFrom = hourFrom;
    }

    public LocalTime getHourTo() {
        return hourTo;
    }

    public void setHourTo(LocalTime hourTo) {
        this.hourTo = hourTo;
    }

    public void setIzuzetiDani(List<LocalDate> izuzetiDani) {
        this.izuzetiDani = izuzetiDani;
    }

    public List<LocalDate> getIzuzetiDani() {
        return izuzetiDani;
    }
}
