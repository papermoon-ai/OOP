package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import data.exceptions.ConsoleErrorHandler;
import data.exceptions.IOErrorHandler;
import data.shapes.Shape;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ShapeSource implements FileSource<Shape> {
    private final ConsoleErrorHandler handler;
    private final String path;

    public ShapeSource(String path) {
        handler = new IOErrorHandler();
        this.path = path;
    }

    public void toFile(List<Shape> shapes) {
        try (Writer writer = new FileWriter(path, false)) {
            GsonBuilder builder = new GsonBuilder();
            Type type = new TypeToken<List<Shape>>() {}.getType();

            builder.registerTypeAdapter(Shape.class, new InterfaceAdapter());
            Gson gson = builder.create();

            gson.toJson(shapes, type, writer);
        } catch (Exception e) {
            handler.handle(e);
        }
    }

    public List<Shape> fromFile() {
        try (FileReader reader = new FileReader(path)) {
            GsonBuilder builder = new GsonBuilder();
            Type type = new TypeToken<List<Shape>>() {}.getType();

            builder.registerTypeAdapter(Shape.class, new InterfaceAdapter());
            Gson gson = builder.create();

            return gson.fromJson(reader, type);
        } catch (Exception e) {
            handler.handle(e);
            return Collections.emptyList();
        }
    }
}
