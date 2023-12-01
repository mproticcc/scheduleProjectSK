package implementation1;

import exceptions.DatumUIzuzetomDanuException;
import exceptions.TerminJeZauzetException;
import klase.Manager;
import klase.Prostorija;
import klase.Raspored;
import klase.Termin;
import specifikacija.DodelaTermina;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Imp1 implements DodelaTermina {

    static{
        Imp1 imp1 = new Imp1();
        Manager.setObject(imp1);
    }


    private boolean preklapanjeTermina(LocalDateTime pocetak1, LocalDateTime kraj1, LocalDateTime pocetak2, LocalDateTime kraj2) {
        if (pocetak1.toLocalDate().isBefore(kraj2.toLocalDate()) && kraj1.toLocalDate().isAfter(pocetak2.toLocalDate())) {
            return true;
        }
        else if(pocetak1.isEqual(pocetak2) && kraj1.isEqual(kraj2)){
            return true;
        }

        return false;
    }

    @Override
    public void isProstorijaZauzeta(Prostorija prostorija, Raspored raspored){
        for(Termin t:raspored.getTermini()){
            if(t.getProstorija().equals(prostorija)){
                System.out.println("Prostorija "+t.getProstorija().getNaziv()+" je zauzeta u terminu "+t.getPocetakPerioda().toLocalTime() + " do "+ t.getKrajPerioda().toLocalTime());
            }
        }
        System.out.println("Prostorija je slobodna");
    }

    @Override
    public void isTerminSlobodan(LocalDateTime pocetak1, LocalDateTime kraj1, Raspored raspored) {
        for(Termin t:raspored.getTermini()){
            if(t.getPocetakPerioda() == pocetak1 && t.getKrajPerioda() == kraj1){
                System.out.println("Zadati termin je zauzet");
            }
        }
        System.out.println("Termin je slobodan");
    }



    private void addAdditional(Map<String,String> dodatneStvari,String s){
        if(s.isEmpty()){

        }
        else {
            String[] info = s.split(","); //Znaci string izgleda kao : Profesor:Arsenije Petrovic,Racunar=DA,Predmet="UUP"

            String finale = "";
            int i = 0;
            while (i < info.length) {
                finale = info[i]; // Profesor:Arsenije Petrovic
                String[] keyValue = finale.split(":");
                dodatneStvari.put(keyValue[0], keyValue[1]);
                i++;
            }
        }
    }

    @Override
    public Termin kreirajTerminPt(LocalDateTime pocetak, int trajanje, Prostorija prostorija, Raspored raspored) {
        for(Termin t:raspored.getTermini()){
            if(!preklapanjeTermina(pocetak,pocetak.plusHours(trajanje/60).minusMinutes(trajanje%60),t.getPocetakPerioda(),t.getKrajPerioda())){
                System.out.println("Termin je uspesno kreiran");
                return new Termin(pocetak,trajanje,prostorija);
            }
        }
        System.out.println("Ovaj termin je zauzet, tako da termin u datim vrememnima ne moze biti kreiran");
        return null;

    }

    @Override
    public boolean brisanjeTermina(LocalDateTime pocetak, LocalDateTime kraj, Raspored raspored) {
        int brojac=0;
        for(int i=0;i<raspored.getTermini().size();i++){
            Termin t = raspored.getTermini().get(i);
            if(preklapanjeTermina(pocetak,kraj,t.getPocetakPerioda(),t.getKrajPerioda())){
                raspored.getTermini().remove(t);
                System.out.println("Termin" + t + " je uspesno obrisan");
                brojac++;
            }
        }
        if(brojac>0)
            return true;
        System.out.println("Termin nije obrisan");
        return false;
    }

    @Override
    public boolean premestajTermina(DayOfWeek day,LocalDateTime pocetak, LocalDateTime kraj, Raspored raspored) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.uuuu H:mm");
        for(Termin t:raspored.getTermini()){
            if(preklapanjeTermina(pocetak,kraj,t.getPocetakPerioda(),t.getKrajPerioda())){
                Scanner sc = new Scanner(System.in);
                System.out.println("Unesite novi pocetak");
                LocalDateTime pocetak1 = LocalDateTime.parse(sc.nextLine(),formatter);
                System.out.println("Unesite novi kraj");
                LocalDateTime kraj1 = LocalDateTime.parse(sc.nextLine(),formatter);
                t.setPocetakPerioda(pocetak1);
                t.setKrajPerioda(kraj1);
                System.out.println("Termin je uspesno promenjen");
                return true;
            }
        }
        System.out.println("Termin nije promenjen");
        return false;
    }



    //izlistavanje slobodnih termina po ucionicamam i vezanim podacima
    @Override
    public void izlistavanjeSlobodniTermini(String kriterijum, Raspored raspored) {

        boolean flag = true;
       //Za dan DATUM je slobodan termin Od ... Do ...
      LocalDateTime pocetniDatum = raspored.getTermini().get(0).getPocetakPerioda();
      System.out.println("Za DAN " + pocetniDatum.toLocalDate() + " slobodni termini su: \n");
        for(Termin t:raspored.getTermini()){
            if(t.getDodatneStvari().containsValue(kriterijum) || t.getProstorija().getNaziv().equals(kriterijum) ){
                if(pocetniDatum.getDayOfMonth()!=t.getPocetakPerioda().getDayOfMonth()) {
                    System.out.println("Za DAN " + t.getPocetakPerioda().toLocalDate() + " slobodni termini su:");
                    pocetniDatum = t.getPocetakPerioda();
                }
                if(raspored.getHourFrom() == t.getPocetakPerioda().toLocalTime()){
                    System.out.println("Od: "+t.getKrajPerioda().getHour() + ":"+t.getKrajPerioda().getMinute() + " do " +raspored.getTermini().get(raspored.getTermini().indexOf(t) +1).getPocetakPerioda().getHour() + ":"+raspored.getTermini().get(raspored.getTermini().indexOf(t) +1).getPocetakPerioda().getMinute());
                }
                else{

                    try {
                        if(flag){
                            flag = false;
                            System.out.println("Od: "+ raspored.getHourFrom() + " do: " +t.getPocetakPerioda().getHour() + ":"+t.getPocetakPerioda().getMinute());
                        }
                         else if(raspored.getTermini().get(raspored.getTermini().indexOf(t) +1).getPocetakPerioda().getDayOfMonth() != t.getKrajPerioda().getDayOfMonth() ){
                            flag = true;
                            System.out.println("Od: "+raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getKrajPerioda().getHour() + ":"+raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getKrajPerioda().getMinute()  + " do: "  +raspored.getTermini().get(raspored.getTermini().indexOf(t)).getPocetakPerioda().getHour() + ":"+raspored.getTermini().get(raspored.getTermini().indexOf(t)).getPocetakPerioda().getMinute());
                            System.out.println("Od: "+t.getKrajPerioda().getHour() + ":" +t.getKrajPerioda().getMinute() +" do: "+ raspored.getHourTo());
                        }
                        else if(raspored.getTermini().get(raspored.getTermini().indexOf(t)+1).getPocetakPerioda().toLocalTime() != t.getKrajPerioda().toLocalTime() ){
                            System.out.println("Od: "+raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getKrajPerioda().getHour() + ":"+raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getKrajPerioda().getMinute()  + " do: "  +raspored.getTermini().get(raspored.getTermini().indexOf(t)).getPocetakPerioda().getHour() + ":"+raspored.getTermini().get(raspored.getTermini().indexOf(t)).getPocetakPerioda().getMinute());
                        }
                        else{
                            System.out.println("Od: " +raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getPocetakPerioda().getHour() + ":"+raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getPocetakPerioda().getMinute() + " do: " +raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getKrajPerioda().getHour() + ":"+raspored.getTermini().get(raspored.getTermini().indexOf(t) -1).getKrajPerioda().getMinute() );
                        }
                    }
                    catch (IndexOutOfBoundsException exc){
                        System.out.println("Od: "+ raspored.getHourFrom() + " do: " +t.getPocetakPerioda().getHour() + ":"+t.getPocetakPerioda().getMinute());
                    }

                    if(raspored.getTermini().indexOf(t) == raspored.getTermini().size()){
                        System.out.println("Od: "+t.getKrajPerioda().getHour() + ":" +t.getKrajPerioda().getMinute() +" do: "+ raspored.getHourTo()+"\n");
                    }
                }
            }

        }
    }

    @Override
    public void izlisatavnjeZauzetihTermina(String podatak, Raspored raspored) {
        System.out.println("Zauzeti termini za kriterijum: " +podatak);
        for(Termin t:raspored.getTermini()){
            if(t.getDodatneStvari().containsValue(podatak)){
                System.out.println("Od "+t.getPocetakPerioda().toLocalTime() + " do " + t.getKrajPerioda().toLocalTime());
            }
        }
    }

    @Override
    public boolean kreirajTerminUzPk(DayOfWeek day, LocalDateTime pocetakPerioda, LocalDateTime krajPerioda, Raspored r, Prostorija p, String dodatneStvari) throws Exception {
        Map<String, String> mapaDodatnihInfo = new HashMap<>();
        addAdditional(mapaDodatnihInfo,dodatneStvari);

        if(pocetakPerioda.toLocalDate().isBefore(r.getFrom())){
            throw new DatumUIzuzetomDanuException();
        }

        if(r.getIzuzetiDani().contains(pocetakPerioda.toLocalDate())){
            throw new DatumUIzuzetomDanuException();
        }

        for(Termin t:r.getTermini()){
            if(!preklapanjeTermina(pocetakPerioda,krajPerioda,t.getPocetakPerioda(),t.getKrajPerioda())){
                System.out.println("Termin je uspesno kreiran");
                Termin noviT = new Termin(pocetakPerioda,krajPerioda,p);
                noviT.setDodatneStvari(mapaDodatnihInfo);
                r.getTermini().add(noviT);
                return true;
            }
        }
        throw new TerminJeZauzetException();

    }




}
