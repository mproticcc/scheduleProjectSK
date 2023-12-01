package klase;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Prostorija {
    @Expose
    private String naziv;
    @Expose
    private int kapacitet;
    @Expose
    private boolean racunari;


    public Prostorija(String naziv, int kapacitet) {
        this.naziv = naziv;
        this.kapacitet = kapacitet;
    }
    public Prostorija(String naziv, int kapacitet, boolean racunari) {
        this.naziv = naziv;
        this.kapacitet = kapacitet;
        this.racunari = racunari;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getKapacitet() {
        return kapacitet;
    }

    public void setKapacitet(int kapacitet) {
        this.kapacitet = kapacitet;
    }

    public boolean isRacunari() {
        return racunari;
    }

    public void setRacunari(boolean racunari) {
        this.racunari = racunari;
    }

    @Override
    public String toString() {
        return naziv + " " + kapacitet + " "+ racunari;
    }
}
