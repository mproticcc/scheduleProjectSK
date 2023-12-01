package exceptions;

public class TerminJeZauzetException extends Exception{

    public TerminJeZauzetException(){
        super("Vec postoji termin u zadatom periodu!");
    }

}
