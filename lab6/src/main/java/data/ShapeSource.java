package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import data.shapes.Shape;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ShapeSource implements FileSource<Shape> {
    private final String path;

    public ShapeSource(String path) {
        this.path = path;
    }

    public void toFile(List<Shape> shapes) throws IOException {
        Writer writer = new FileWriter(path, false);

        GsonBuilder builder = new GsonBuilder();
        Type type = new TypeToken<List<Shape>>() {}.getType();

        builder.registerTypeAdapter(Shape.class, new InterfaceAdapter());
        Gson gson = builder.create();

        gson.toJson(shapes, type, writer);
        writer.close();
    }

    public List<Shape> fromFile() throws IOException {
        FileReader reader = new FileReader(path);

        GsonBuilder builder = new GsonBuilder();
        Type type = new TypeToken<List<Shape>>() {}.getType();

        builder.registerTypeAdapter(Shape.class, new InterfaceAdapter());
        Gson gson = builder.create();

        List<Shape> shapes = gson.fromJson(reader, type);

        reader.close();
        return shapes != null ? shapes : Collections.emptyList();
    }
}
