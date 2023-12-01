package specifikacija;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.exceptions.CsvValidationException;
import klase.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ImportExport {

    LocalTime vremePocetka;
    LocalTime vremeZavrsetka;
    List<LocalDate> izuzetiDani = new ArrayList<>();

    public ImportExport(LocalTime vremePocetka,LocalTime vremeZavrsetka,String putanjaDoIzuzetihDana) {
        this.vremeZavrsetka = vremeZavrsetka;
        this.vremePocetka = vremePocetka;
        ucitajIzuzeteDate(putanjaDoIzuzetihDana);
    }

    public void ucitajIzuzeteDate(String putanjaFajla){

        try (CSVReader reader = new CSVReader(new FileReader(putanjaFajla))) {
            String[] header = reader.readNext(); // Uzmi zaglavlje
            String[] line;

            while ((line = reader.readNext()) != null) {
                LocalDate datum = LocalDate.parse(line[0]);
                izuzetiDani.add(datum);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public List<Prostorija> ucitajProstorije(String putanjaDoFajla) {
        List<Prostorija> prostorije = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(putanjaDoFajla))) {
            String[] header = reader.readNext(); // Uzmi zaglavlje
            String[] line;

            while ((line = reader.readNext()) != null) {
                String imeProstorije = line[0];
                int kapacitet = Integer.parseInt(line[1]);
                boolean imaLiracunare = Boolean.parseBoolean(line[2]);

                Prostorija prostorija = new Prostorija(imeProstorije, kapacitet, imaLiracunare);
                prostorije.add(prostorija);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        System.out.println("Fajl uspesno ucitan");
        return prostorije;
    }

    public List<Termin> ucitajRasporedJson(String fileName) {
        List<Termin> filtriraniTermini = new ArrayList<>();

        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).create();            List<Termin> sviTermini = gson.fromJson(new FileReader(fileName), new TypeToken<List<Termin>>(){}.getType());
            for (Termin termin : sviTermini) {

                LocalDateTime pocetakTermina = termin.getPocetakPerioda();
                LocalDateTime krajTermina = termin.getKrajPerioda();
                if (pocetakTermina.toLocalTime().isAfter(vremePocetka) && krajTermina.toLocalTime().isBefore(vremeZavrsetka)) {
                    if (!(izuzetiDani.contains(pocetakTermina.toLocalDate()) || izuzetiDani.contains(krajTermina.toLocalDate()))) {
                        filtriraniTermini.add(termin);
                    }
                    else{
                        System.out.println("Datum nije dobar");
                    }
                }
                else{
                    System.out.println("Vreme nije dobro");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Fajl uspesno ucitan");
        return filtriraniTermini;
    }



    public List<Termin> ucitajRasporedCsv(String filepath,String ConfigFile) throws Exception {
        return loadApache(filepath,ConfigFile);
    }

    private List<Termin> loadApache(String filePath, String configPath) throws IOException {
        List<ConfigMapping> columnMappings = readConfig(configPath);
        Map<Integer, String> mappings = new HashMap<>();


        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal());
        }
        FileReader fileReader = new FileReader(filePath);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader);

        List<Termin> termini = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1));

        for (CSVRecord record : parser) {
            Termin termin = new Termin();
            termin.setVremeOdrzavanja(new ArrayList<>());

            for (ConfigMapping entry : columnMappings) {
                int columnIndex = entry.getIndex();

                if(columnIndex == -1) continue;

                String columnName = entry.getCustom();

                switch (mappings.get(columnIndex)) {
                    case "place":
                        termin.setProstorija(new Prostorija(record.get(columnIndex),2));
                        break;
                    case "start":
                        LocalDateTime startDateTime = LocalDateTime.parse(record.get(columnIndex), formatter);
                        termin.setPocetakPerioda(startDateTime);
                        break;
                    case "end":
                        LocalDateTime endDateTime = LocalDateTime.parse(record.get(columnIndex), formatter);
                        termin.setKrajPerioda(endDateTime);
                        termin.odradiVremeOdrzavanja();
                        break;
                    case "additional":
                        termin.getDodatneStvari().put(columnName, record.get(columnIndex));
                        break;
                    case "day":
                        if(record.get(columnIndex).equals("PON"))
                        termin.setDay(DayOfWeek.MONDAY);
                        if(record.get(columnIndex).equals("UTO"))
                            termin.setDay(DayOfWeek.TUESDAY);
                        if(record.get(columnIndex).equals("SRE"))
                            termin.setDay(DayOfWeek.WEDNESDAY);
                        if(record.get(columnIndex).equals("CET"))
                            termin.setDay(DayOfWeek.THURSDAY);
                        if(record.get(columnIndex).equals("PET"))
                            termin.setDay(DayOfWeek.FRIDAY);
                        if(record.get(columnIndex).equals("SUB"))
                            termin.setDay(DayOfWeek.SATURDAY);
                        break;
                }
            }

            termini.add(termin);
        }

        List<Termin> filtriraniTermini = new ArrayList<>();
        for (Termin termin : termini) {

            LocalDateTime pocetakTermina = termin.getPocetakPerioda();
            LocalDateTime krajTermina = termin.getKrajPerioda();

            if (pocetakTermina.toLocalTime().isAfter(vremePocetka) && krajTermina.toLocalTime().isBefore(vremeZavrsetka)) {
                if (!(izuzetiDani.contains(pocetakTermina.toLocalDate()) || izuzetiDani.contains(krajTermina.toLocalDate()))) {
                    filtriraniTermini.add(termin);
                }
                else{
                    System.out.println("Datum nije dobar");
                }
            }
            else{
                System.out.println("Vreme nije dobro");
            }
        }
        System.out.println("Fajl uspesno ucitan");
        return filtriraniTermini;
    }

    private static List<ConfigMapping>  readConfig(String filePath) throws FileNotFoundException {
        List<ConfigMapping> mappings = new ArrayList<>();

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ", 3);

            mappings.add(new ConfigMapping(Integer.valueOf(splitLine[0]), splitLine[1], splitLine[2]));
        }

        scanner.close();


        return mappings;
    }



    public void upisiRasporedUJson(String fileName, String path,Raspored raspored) {
        ObjectMapper objectMapper = new ObjectMapper();
        String outputPath = path + File.separator + fileName + ".json";
        System.out.println("1. Cuvanje svih podataka");
        System.out.println("2. Cuvanje rasporeda za određeni period");
        System.out.println("3. Grupisanje po danima u nedelji");
        System.out.println("4. Cuvanje podataka za određeni podatak(npr. za jedan predmet)");
        Scanner sc = new Scanner(System.in);

        switch (sc.nextLine()){
            case "1":
                try {
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.writeValue(new File(outputPath), raspored.getTermini());
                    System.out.println("JSON fajl je uspešno kreiran i popunjen.");
                } catch (IOException e) {
                    System.out.println("JSON fajl nije uspešno kreiran i nije popunjen.");
                    e.printStackTrace();
                }
                break;
            case "2":
                try {
                    System.out.println("Unesite pocetak i kraj perioda");
                    LocalDate pocetak = LocalDate.parse(sc.nextLine());
                    LocalDate kraj = LocalDate.parse(sc.nextLine());
                    objectMapper.registerModule(new JavaTimeModule());

                    List<Termin> rasporedZaPeriod = raspored.getTermini().stream()
                            .filter(termin -> termin.getKrajPerioda().toLocalDate().isAfter(pocetak.minusDays(1))
                                    && termin.getPocetakPerioda().toLocalDate().isBefore(kraj.plusDays(1)))
                            .collect(Collectors.toList());

                    objectMapper.writeValue(new File(outputPath), rasporedZaPeriod);
                    System.out.println("JSON fajl za period je uspešno kreiran i popunjen.");
                } catch (IOException e) {
                    System.out.println("JSON fajl za period nije uspešno kreiran i nije popunjen.");
                    e.printStackTrace();
                }
                break;
            case "3":try {
                objectMapper.registerModule(new JavaTimeModule());

                Map<DayOfWeek, List<Termin>> rasporedPoDanima = raspored.getTermini().stream()
                        .collect(Collectors.groupingBy(termin -> termin.getPocetakPerioda().toLocalDate().getDayOfWeek()));
                objectMapper.writeValue(new File(outputPath), rasporedPoDanima);
                System.out.println("JSON fajl grupisan po danima u nedelji , uspešno kreiran i popunjen.");
            } catch (IOException e) {
                System.out.println("JSON fajl grupisan po danima u nedelji nije uspešno kreiran i nije popunjen.");
                e.printStackTrace();
            }
                break;
            case "4":
                try {
                    objectMapper.registerModule(new JavaTimeModule());
                    String podatak = sc.nextLine();
                    List<Termin> rasporedZaPredmet = raspored.getTermini().stream()
                            .filter(termin -> termin.getDodatneStvari().containsValue(podatak))
                            .collect(Collectors.toList());

                    objectMapper.writeValue(new File(outputPath), rasporedZaPredmet);
                    System.out.println("JSON fajl za uneti podatak je uspešno kreiran i popunjen.");
                } catch (IOException e) {
                    System.out.println("JSON fajl za uneti podatak za predmet nije uspešno kreiran i nije popunjen.");
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Losa komda je uneta");
        }
    }


    public void upisiRasporedUCsv(String fileName, String path,Raspored raspored) {
        String[] header;
        try (FileWriter fileWriter = new FileWriter(path + "/" + fileName+".csv");
             CSVWriter csvWriter = (CSVWriter) new CSVWriterBuilder(fileWriter)
                     .withSeparator(',')
                     .withQuoteChar('"')
                     .build()) {

            System.out.println("1. Cuvanje svih podataka");
            System.out.println("2. Cuvanje rasporeda za određeni period");
            System.out.println("3. Grupisanje po danima u nedelji");
            System.out.println("4. Cuvanje podataka za određeni podatak(npr. za jedan predmet)");

            Scanner sc = new Scanner(System.in);

            switch (sc.nextLine()){
                case "1":
                     header = new String[]{"pocetak", "kraj", "prostorija", "Dodatne Informacije"};
                    csvWriter.writeNext(header);
                    for (Termin termin : raspored.getTermini()) {
                        String[] data = {String.valueOf(termin.getPocetakPerioda()), String.valueOf(termin.getKrajPerioda()), String.valueOf(termin.getProstorija()), String.valueOf(termin.getDodatneStvari())};
                        csvWriter.writeNext(data);
                    }

                    System.out.println("CSV fajl je uspešno kreiran i popunjen.");
                    break;
                case "2":
                    System.out.println("Unesite pocetak i kraj perioda");
                    LocalDate pocetak = LocalDate.parse(sc.nextLine());
                    LocalDate kraj = LocalDate.parse(sc.nextLine());
                    header = new String[]{"pocetak", "kraj", "prostorija", "Dodatna Oprema"};
                    csvWriter.writeNext(header);

                    for (Termin termin : raspored.getTermini()) {
                        LocalDate terminPocetak = LocalDate.from((termin instanceof Termin) ? ((Termin) termin).getPocetakPerioda() : termin.getPocetakPerioda());
                        if (terminPocetak.isAfter(pocetak) && terminPocetak.isBefore(kraj)) {
                            String[] data = {String.valueOf(terminPocetak), String.valueOf(termin.getKrajPerioda()), termin.getProstorija().getNaziv(), String.valueOf(termin.getDodatneStvari())};
                            csvWriter.writeNext(data);
                        }
                    }
                    System.out.println("CSV fajl za period je uspešno kreiran i popunjen.");
                    break;
                case "3":
                    header = new String[]{"Dan u nedelji", "pocetak", "kraj", "prostorija", "Dodatne Informacije"};
                    csvWriter.writeNext(header);

                    Map<DayOfWeek, List<Termin>> rasporedPoDanima = raspored.getTermini().stream()
                            .collect(Collectors.groupingBy(termin -> termin.getPocetakPerioda().getDayOfWeek()));

                    for (Map.Entry<DayOfWeek, List<Termin>> entry : rasporedPoDanima.entrySet()) {
                        for (Termin termin : entry.getValue()) {
                            String[] data = {entry.getKey().toString(), String.valueOf(termin.getPocetakPerioda()), String.valueOf(termin.getKrajPerioda()), String.valueOf(termin.getProstorija()), String.valueOf(termin.getDodatneStvari())};
                            csvWriter.writeNext(data);
                        }
                    }
                    System.out.println("CSV fajl grupisan po danima u nedelji je uspešno kreiran i popunjen.");
                    break;
                case "4":
                    String podatak = sc.nextLine();
                    header = new String[]{"pocetak", "kraj", "prostorija", "Dodatne Informacije"};
                    csvWriter.writeNext(header);

                    List<Termin> rasporedZaPredmet = raspored.getTermini().stream()
                            .filter(termin -> termin.getDodatneStvari().containsValue(podatak))
                            .collect(Collectors.toList());

                    for (Termin termin : rasporedZaPredmet) {
                        String[] data = {String.valueOf(termin.getPocetakPerioda()), String.valueOf(termin.getKrajPerioda()), String.valueOf(termin.getProstorija()), String.valueOf(termin.getDodatneStvari())};
                        csvWriter.writeNext(data);
                    }

                    System.out.println("CSV fajl za podatak je uspešno kreiran i popunjen.");
                    break;
                default:
                    System.out.println("Losa komanda je uneta");
            }


        } catch (IOException e) {
            System.out.println("CSV fajl nije uspešno kreiran i nije popunjen.");
            e.printStackTrace();
        }
    }


    public void upisiRasporedUPdf(String fileName, String path,Raspored raspored) {
        Document document = new Document(PageSize.A4);

        System.out.println("1. Cuvanje svih podataka");
        System.out.println("2. Cuvanje rasporeda za određeni period");
        System.out.println("3. Grupisanje po danima u nedelji");
        System.out.println("4. Cuvanje podataka za određeni podatak(npr. za jedan predmet)");

        Scanner sc = new Scanner(System.in);

        switch (sc.nextLine()){
            case "1":
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(path + "/" + fileName+".pdf"));
                    document.open();

                    for (Termin termin : raspored.getTermini()) {

                        document.add(new Paragraph("Pocetak: " + termin.getPocetakPerioda()));
                        document.add(new Paragraph("Kraj: " + termin.getKrajPerioda()));
                        document.add(new Paragraph("Prostorija: " + termin.getProstorija()));
                        document.add(new Paragraph("Dodatne informacije: " +termin.getDodatneStvari()));
                    }

                    System.out.println("PDF fajl je uspešno kreiran i popunjen.");
                } catch (ExceptionConverter | DocumentException | IOException e) {
                    e.printStackTrace();
                } finally {
                    document.close();
                }
                break;
            case "2":
                try {
                    System.out.println("Unesite pocetak i kraj perioda");
                    LocalDate pocetak = LocalDate.parse(sc.nextLine());
                    LocalDate kraj = LocalDate.parse(sc.nextLine());
                    PdfWriter.getInstance(document, new FileOutputStream(path + "/" + fileName + ".pdf"));
                    document.open();

                    for (Termin termin : raspored.getTermini()) {
                        LocalDate terminPocetak = LocalDate.from((termin instanceof Termin) ? ((Termin) termin).getPocetakPerioda() : termin.getPocetakPerioda());
                        if (terminPocetak.isAfter(pocetak) && terminPocetak.isBefore(kraj)) {
                            document.add(new Paragraph("Pocetak: " + terminPocetak));
                            document.add(new Paragraph("Kraj: " + termin.getKrajPerioda()));
                            document.add(new Paragraph("Prostorija: " + termin.getProstorija()));
                            document.add(new Paragraph("Dodatne informacije: " + termin.getDodatneStvari()));
                        }
                    }

                    System.out.println("PDF fajl za period je uspešno kreiran i popunjen.");
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                } finally {
                    document.close();
                }
                break;
            case "3":
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(path + "/" + fileName + ".pdf"));
                    document.open();

                    Map<DayOfWeek, List<Termin>> rasporedPoDanima = raspored.getTermini().stream()
                            .collect(Collectors.groupingBy(termin -> termin.getPocetakPerioda().getDayOfWeek()));

                    for (Map.Entry<DayOfWeek, List<Termin>> entry : rasporedPoDanima.entrySet()) {
                        document.add(new Paragraph("Dan u nedelji: " + entry.getKey()));
                        for (Termin termin : entry.getValue()) {
                            document.add(new Paragraph("Pocetak: " + termin.getPocetakPerioda()));
                            document.add(new Paragraph("Kraj: " + termin.getKrajPerioda()));
                            document.add(new Paragraph("Prostorija: " + termin.getProstorija()));
                            document.add(new Paragraph("Dodatne informacije: " + termin.getDodatneStvari()));
                        }
                    }

                    System.out.println("PDF fajl grupisan po danima u nedelji je uspešno kreiran i popunjen.");
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                } finally {
                    document.close();
                }
                break;
            case "4":
                try {
                    String podatak = sc.nextLine();
                    PdfWriter.getInstance(document, new FileOutputStream(path + "/" + fileName + ".pdf"));
                    document.open();

                    List<Termin> rasporedZaPredmet = raspored.getTermini().stream()
                            .filter(termin -> termin.getDodatneStvari().containsValue(podatak))
                            .collect(Collectors.toList());

                    for (Termin termin : rasporedZaPredmet) {
                        document.add(new Paragraph("Pocetak: " + termin.getPocetakPerioda()));
                        document.add(new Paragraph("Kraj: " + termin.getKrajPerioda()));
                        document.add(new Paragraph("Prostorija: " + termin.getProstorija()));
                        document.add(new Paragraph("Dodatne informacije: " + termin.getDodatneStvari()));
                    }

                    System.out.println("PDF fajl za predmet je uspešno kreiran i popunjen.");
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                } finally {
                    document.close();
                }
                break;
            default:
                System.out.println("Losa komanda je uneta");
        }

    }

    public List<LocalDate> getIzuzetiDani() {
        return izuzetiDani;
    }
}
