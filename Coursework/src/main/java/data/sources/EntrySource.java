package data.sources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import data.Entry;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class EntrySource implements FileSource {
    private final String path;

    public EntrySource(String path) {
        this.path = path;
    }

    public void toFile(List<Entry> entries) throws IOException {
        try (Writer writer = new FileWriter(path, false)) {
            GsonBuilder builder = new GsonBuilder();
            Type type = new TypeToken<List<Entry>>() {}.getType();
            builder.registerTypeAdapter(Entry.class, new InterfaceAdapter());

            Gson gson = builder.create();
            gson.toJson(entries, type, writer);
        } catch (IOException e) {
            throw new IOException("An error has occurred while working with the file. Check file path.");
        }
    }

    public List<Entry> fromFile() throws IOException {
        try (FileReader reader = new FileReader(path)) {
            GsonBuilder builder = new GsonBuilder();
            Type type = new TypeToken<List<Entry>>() {}.getType();
            builder.registerTypeAdapter(Entry.class, new InterfaceAdapter());

            Gson gson = builder.create();

            List<Entry> entries = gson.fromJson(reader, type);
            return entries != null ? entries : Collections.emptyList();
        } catch (IOException e) {
            throw new IOException("An error has occurred while working with the file. Check file path.");
        }
    }
}
