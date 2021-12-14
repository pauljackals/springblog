package converterCSVToXML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.apache.commons.text.StringEscapeUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CSVToXML {
    private String snakeToCamelCase(String string) {
        boolean underscoreDetected = false;
        StringBuilder stringNew = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char letter = string.charAt(i);
            if(underscoreDetected) {
                stringNew.append(Character.toUpperCase(letter));
                underscoreDetected = false;
            } else if (letter == '_') {
                underscoreDetected = true;
            } else {
                stringNew.append(letter);
            }
        }
        return stringNew.toString();
    }

    private String parseClassNameFromFile(String fileName) {
        String[] parts = fileName.split("_");
        StringBuilder className = new StringBuilder();
        for (String part : parts) {
            className.append(part.substring(0, part.length()-1));
        }
        return className.toString();
    }

    public void run(String beansPackage) throws IOException, CsvException, URISyntaxException {
        String rootPath = (new File("./data").getCanonicalPath());
        Stream<Path> pathsStream = Files.list(Paths.get(rootPath, "csv/"));
        List<Path> paths = pathsStream.collect(Collectors.toList());
        pathsStream.close();

        StringBuilder xmlFile = new StringBuilder();
        xmlFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlFile.append("<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">\n");
        
        for (Path path : paths) {
            String fileName = path.getFileName().toString().replace(".csv", "");
            String className = parseClassNameFromFile(fileName);

            Reader reader = Files.newBufferedReader(path);
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> lines = csvReader.readAll();
            csvReader.close();

            String[] headers = lines.get(0);
            String[] headersCamelCase = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                String header = snakeToCamelCase(headers[i]);
                headersCamelCase[i] = header;
            }

            for (int i = 1; i < lines.size(); i++) {
                String[] line = lines.get(i);
                xmlFile.append(String.format("\t<bean id=\"%s%d\" class=\"%s.%s\">\n", className, i-1, beansPackage, className));
                for (int j = 0; j < line.length; j++) {
                    String name = headersCamelCase[j];
                    if(name.equals("id")) {
                        name += "CSV";
                    }
                    xmlFile.append(String.format("\t\t<constructor-arg name=\"%s\" value=\"%s\"></constructor-arg>\n", name, StringEscapeUtils.escapeXml10(line[j])));
                }
                xmlFile.append("\t</bean>\n");
            }
        }

        xmlFile.append("</beans>\n");

        BufferedWriter writer = Files.newBufferedWriter(Paths.get(rootPath, "beans.xml"));
        writer.write(xmlFile.toString());
        writer.close();
    }
}
