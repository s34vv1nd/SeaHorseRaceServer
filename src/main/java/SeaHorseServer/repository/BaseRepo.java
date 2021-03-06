package SeaHorseServer.repository;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

public class BaseRepo {
    protected synchronized void appendToCSV(String url, String[] data) throws IOException {
        String csv = url;
        CSVWriter writer = new CSVWriter(new FileWriter(csv, true), ',', '\0','\0', "\n");
        writer.writeNext(data);
        writer.close();
    }

    protected synchronized void writeToCSV(String url, String[] data) throws IOException {
        String csv = url;
        CSVWriter writer = new CSVWriter(new FileWriter(csv), ',', '\0','\0', "\n");
        writer.writeNext(data);
        writer.close();
    }

    
}
