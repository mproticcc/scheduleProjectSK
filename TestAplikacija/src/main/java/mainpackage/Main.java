package mainpackage;

import klase.Manager;
import klase.Prostorija;
import klase.Raspored;
import specifikacija.ImportExport;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        //String putanjaDoIzuzetihDana = args[0];
        String putanjaDoIzuzetihDana = "izuzetiDatumi.txt";
        Raspored raspored = new Raspored();
        ImportExport ie = new ImportExport(raspored.getHourFrom(),raspored.getHourTo(),putanjaDoIzuzetihDana);
        raspored.setIzuzetiDani(ie.getIzuzetiDani());
        Scanner sc = new Scanner(System.in);
        System.out.println("Ucitavanje fajlova:");
        System.out.println("1. Ucitavanje preko csv:");
        System.out.println("2. Ucitavanje preko json-a:");

        Class.forName("implementation1.Imp1");

            switch (sc.nextLine()) {
                case "1":
                    System.out.println("Unesite putanju do fajla");
                    String fajl = sc.nextLine();
                    System.out.println("Unesite putanju do config fajla");
                    String config = sc.nextLine();
                    raspored.setTermini(ie.ucitajRasporedCsv(fajl, config));
                    break;
                case "2":
                    System.out.println("Unesite putanju do fajla");
                    String fajl1 = sc.nextLine();
                    raspored.setTermini(ie.ucitajRasporedJson(fajl1));
                    break;
            }
        List<Prostorija> prostorije ;

            System.out.println("Unesite ime fajla do prostorija");
            String fajl = sc.nextLine();
            prostorije = ie.ucitajProstorije(fajl);


        boolean flag = true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.uuuu HH:mm");
        while(flag){
            System.out.println("Rad sa rasporedom:");
            System.out.println("1. Pretrazivanje rasporeda po kriterijumima:");
            System.out.println("2. Kreiranje termina:");
            System.out.println("3. Promena termina:");
            System.out.println("4. Obrisi termin:");
            System.out.println("5. Snimi fajl:");
            System.out.println("6. Izlistaj sve prostorije:");
            System.out.println("7. Ispisi ceo raspored");
            System.out.println("0. Zavrsetak programa:");

            switch (sc.nextLine()){
                case "1":
                    System.out.println("Unesite kriterijum po kome pretrazujete raspore: ");
                    String kriterijum = sc.nextLine();
                    System.out.println("Zauzeti termini");
                    Manager.getObject().izlisatavnjeZauzetihTermina(kriterijum,raspored);
                    System.out.println("Slobodni termini termini");
                    Manager.getObject().izlistavanjeSlobodniTermini(kriterijum,raspored);
                    break;
                case "2":

                    System.out.println("Unesite pocetak termina: ");
                    LocalDateTime pocetak = LocalDateTime.parse(sc.nextLine(),formatter);
                    System.out.println("Unesite kraj termina: ");
                    LocalDateTime kraj = LocalDateTime.parse(sc.nextLine(),formatter);
                    System.out.println("Unesite naziv prostorije ");
                    String naziv = sc.nextLine();
                    System.out.println("Unesite kapacitet prostorije ");
                    String kapacitet = sc.nextLine();
                    Prostorija prostorija = new Prostorija(naziv,Integer.parseInt(kapacitet));
                    System.out.println("Unesite dodatne informacije o terminu u obliku KEY:VALUE : ");
                    String dodatneInfo = sc.nextLine();
                    System.out.println("Unesite dan zadatog termina: ");
                    String dan = sc.nextLine();
                    DayOfWeek day = pocetak.getDayOfWeek();
                    switch(dan){
                        case "PON":
                            day=DayOfWeek.MONDAY;
                        case "UTO":
                            day=DayOfWeek.TUESDAY;
                        case "SRE":
                            day=DayOfWeek.WEDNESDAY;
                        case "CET":
                            day=DayOfWeek.THURSDAY;
                        case "PET":
                            day=DayOfWeek.FRIDAY;
                        case "SUB":
                            day=DayOfWeek.SATURDAY;
                        case "NED":
                            day=DayOfWeek.SUNDAY;
                        default:
                            day = pocetak.getDayOfWeek();
                    }


                    Manager.getObject().kreirajTerminUzPk(day,pocetak,kraj,raspored,prostorija,dodatneInfo);
                    break;
                case "3":
                    System.out.println("Unesite pocetak termina trenutnog termina: ");
                    LocalDateTime pocetak1 = LocalDateTime.parse(sc.nextLine(),formatter);
                    System.out.println("Unesite kraj termina trenutnog termina: ");
                    LocalDateTime kraj1 = LocalDateTime.parse(sc.nextLine(),formatter);
                    System.out.println("Unesite dan zadatog termina: ");
                    String dan1 = sc.nextLine();
                    DayOfWeek day1 = pocetak1.getDayOfWeek();
                    switch(dan1){
                        case "PON":
                            day1=DayOfWeek.MONDAY;
                        case "UTO":
                            day1=DayOfWeek.TUESDAY;
                        case "SRE":
                            day1=DayOfWeek.WEDNESDAY;
                        case "CET":
                            day1=DayOfWeek.THURSDAY;
                        case "PET":
                            day1=DayOfWeek.FRIDAY;
                        case "SUB":
                            day1=DayOfWeek.SATURDAY;
                        case "NED":
                            day1=DayOfWeek.SUNDAY;
                        default:
                            day1 = pocetak1.getDayOfWeek();
                    }
                    Manager.getObject().premestajTermina(day1,pocetak1,kraj1,raspored);
                    break;
                case "4":

                    System.out.println("Unesite pocetak perioda vremena za brisanje termina: ");
                    LocalDateTime pocetak2 = LocalDateTime.parse(sc.nextLine(),formatter);
                    System.out.println("Unesite kraj perioda vremena za brisanje : ");
                    LocalDateTime kraj2 = LocalDateTime.parse(sc.nextLine(),formatter);
                    Manager.getObject().brisanjeTermina(pocetak2,kraj2,raspored);
                    break;
                case "5":
                    System.out.println("1. Upisivanje preko csv-a:");
                    System.out.println("2. Upisivanje preko json-a:");
                    System.out.println("3. Upisivanje preko pdf-a:");

                    switch (sc.nextLine()) {
                        case "1":
                            System.out.println("Unesite kako hocete da vam se zove novi fajl ");
                            String imeF = sc.nextLine();
                            System.out.println("Unesite putanju gde zelite da sacuvate fajl ");
                            String fPath = sc.nextLine();
                            ie.upisiRasporedUCsv(imeF,fPath,raspored);
                     break;
                     case "2":
                         System.out.println("Unesite kako hocete da vam se zove novi fajl ");
                         String imeF1 = sc.nextLine();
                         System.out.println("Unesite putanju gde zelite da sacuvate fajl ");
                         String fPath1 = sc.nextLine();
                         ie.upisiRasporedUJson(imeF1,fPath1,raspored);
                         break;
                        case "3":
                            System.out.println("Unesite kako hocete da vam se zove novi fajl ");
                            String imeF2 = sc.nextLine();
                            System.out.println("Unesite putanju gde zelite da sacuvate fajl ");
                            String fPath2 = sc.nextLine();
                            ie.upisiRasporedUPdf(imeF2,fPath2,raspored);
                            break;
                        default:
                            System.out.println("Komanda nije na validna");
                    }
                    break;
                case "6":
                    for (Prostorija p:prostorije){
                        System.out.println(p.toString());
                    }
                    break;
                case "7":
                    System.out.println(raspored);
                    break;
                case "0":
                    flag = false;
                    break;

                default:
                    System.out.println("Komanda nije na validna");
            }
        }
    }
}
