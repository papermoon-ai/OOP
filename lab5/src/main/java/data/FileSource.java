package data;

import java.util.List;

public interface FileSource<T> {
    void toFile(List<T> list);
    List<T> fromFile();
}
