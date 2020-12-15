package data;

import java.io.IOException;
import java.util.List;

public interface FileSource {
    void toFile(List<Entry> list) throws IOException;
    List<Entry> fromFile() throws IOException;
}
