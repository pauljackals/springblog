package converterCSVToXML;

import java.io.IOException;

import com.opencsv.exceptions.CsvException;

public class App {
    public static void main(String[] args) {
        String envBeansPath = System.getenv("BEANS_PATH");
        String beansPath = envBeansPath!=null ? envBeansPath : "../springblog/src/main/resources/beans";
        String envBeansPackage = System.getenv("BEANS_PACKAGE");
        String beansPackage = envBeansPackage!=null ? envBeansPackage : "net.pauljackals.springblog.domain";
        
        CSVToXML csvToXML = new CSVToXML();
        try {
            csvToXML.run(beansPath, beansPackage);
        } catch (IOException|CsvException e) {
            e.printStackTrace();
        }
    }
}
