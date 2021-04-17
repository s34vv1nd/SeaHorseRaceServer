package SeaHorseServer.repository;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BaseRepo {
    public void AppendToCSVExample(String url, String[] data) throws IOException {
        String csv = url;
        CSVWriter writer = new CSVWriter(new FileWriter(csv, true), ',', '\0','\0');
        writer.writeNext(data);
        writer.close();
    }
}
