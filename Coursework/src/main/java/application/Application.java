package application;

import com.google.gson.reflect.TypeToken;
import ui.UserInterface;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        try {
            JDialog dialog = new UserInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
