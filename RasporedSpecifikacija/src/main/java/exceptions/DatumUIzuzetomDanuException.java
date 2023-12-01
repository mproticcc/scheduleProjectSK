package exceptions;

public class DatumUIzuzetomDanuException extends Exception{

    public DatumUIzuzetomDanuException(){
        super("Datum se nalazi u opsegu izuzetih dana!");
    }

}
