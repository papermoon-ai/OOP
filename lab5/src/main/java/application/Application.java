package application;

import data.FileSource;
import data.ShapeSource;
import data.shapes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();

        shapes.add(new Circle(3));
        shapes.add(new Square(5));
        shapes.add(new Rectangle(2, 3));
        shapes.add(new Triangle(3, 4, 5));

        FileSource<Shape> converter = new ShapeSource("shapes.json");
        try {
            converter.toFile(shapes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<Shape> shapesFromFile = converter.fromFile();
            shapesFromFile.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
