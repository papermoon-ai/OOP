package services;

import org.jetbrains.annotations.NotNull;
import services.contacts.ContactType;
import services.entities.Entry;
import services.contacts.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneBook {

    private final Map<String, Entry> data;

    private static final String ENTRY_IS_ALREADY_ADDED_MESSAGE = "The entry with these parameters already exists";

    private static final String ENTRY_DOES_NOT_EXIST_MESSAGE = "The entry with these parameters doesn't exist";

    public PhoneBook() {
        data = new HashMap<>();
    }

    public void addEntry(@NotNull String name, @NotNull String surname,
                         @NotNull ContactType type, @NotNull String info) {

        if (data.containsKey(name + " " + surname))
            throw new IllegalArgumentException(ENTRY_IS_ALREADY_ADDED_MESSAGE);

        Entry entry = new Entry(name, surname, new Contact(type, info));
        data.put(name + " " + surname, entry);
    }

    public void addContact(@NotNull String name, @NotNull String surname,
                           @NotNull ContactType type, @NotNull String info) {

        if (!data.containsKey(name + " " + surname))
            throw new IllegalArgumentException(ENTRY_DOES_NOT_EXIST_MESSAGE);

        data.get(name + " " + surname).addContact(new Contact(type, info));
    }

    public void editContact(@NotNull String name, @NotNull String surname,
                            @NotNull ContactType oldType, @NotNull String oldInfo,
                            @NotNull ContactType newType, @NotNull String newInfo) {

        if (!data.containsKey(name + " " + surname))
            throw new IllegalArgumentException(ENTRY_DOES_NOT_EXIST_MESSAGE);

        data.get(name + " " + surname).editContact(new Contact(oldType, oldInfo), new Contact(newType, newInfo));
    }

    public void deleteEntry(@NotNull String name, @NotNull String surname) {
        if (!data.containsKey(name + " " + surname))
            throw new IllegalArgumentException(ENTRY_DOES_NOT_EXIST_MESSAGE);

        data.remove(name + " " + surname);
    }

    public void deleteContact(@NotNull String name, @NotNull String surname,
                              @NotNull ContactType type, @NotNull String info) {

        if (!data.containsKey(name + " " + surname))
            throw new IllegalArgumentException(ENTRY_DOES_NOT_EXIST_MESSAGE);

        data.get(name + " " + surname).deleteContact(new Contact(type, info));
    }

    public String get(@NotNull String name, @NotNull String surname) {
        if (!data.containsKey(name + " " + surname))
            throw new IllegalArgumentException(ENTRY_DOES_NOT_EXIST_MESSAGE);

        return data.get(name + " " + surname).getInfo();
    }

    public List<String> find(@NotNull String sequence) {
        List<String> contacts = new ArrayList<>();

        for (Entry entry : data.values()) {
            if (entry.toString().toLowerCase().contains(sequence.toLowerCase())) {
                contacts.add(entry.getInfo());
            }
        }

        return contacts;
    }

    public List<String> getAll() {
        List<String> entries = new ArrayList<>();
        data.values().forEach(entry -> entries.add(entry.getInfo()));

        return entries;
    }
}
