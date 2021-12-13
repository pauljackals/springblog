package converterCSVToXML;

import java.io.IOException;
import java.net.URISyntaxException;

import com.opencsv.exceptions.CsvException;

public class App {
    public static void main(String[] args) {
        String envBeansPackage = System.getenv("BEANS_PACKAGE");
        String beansPackage = envBeansPackage!=null ? envBeansPackage : "net.pauljackals.springblog.domain";
        
        CSVToXML csvToXML = new CSVToXML();
        try {
            csvToXML.run(beansPackage);
        } catch (IOException|CsvException|URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
