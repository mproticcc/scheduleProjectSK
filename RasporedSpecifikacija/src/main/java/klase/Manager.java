package klase;

import specifikacija.DodelaTermina;

public class Manager {

    private static DodelaTermina object;

    static DodelaTermina obj(){

        return null;
    }

    public static DodelaTermina getObject(){

        return object;
    }

    public static void setObject(DodelaTermina object) {
        Manager.object = object;
    }
}
