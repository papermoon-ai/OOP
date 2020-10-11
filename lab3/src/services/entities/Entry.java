package services.entities;

import services.contacts.Contact;
import services.contacts.ContactType;

import java.util.*;

public class Entry {
    private final String name;
    private final String surname;
    private final Map<ContactType, List<String>> contacts;

    private static final String CONTACT_IS_ALREADY_ADDED_MESSAGE = "This contact is already added";

    private static final String CONTACT_DOES_NOT_EXIST_MESSAGE = "This contact is already added";

    public Entry(String name, String surname, Contact contact) {
        this.name = name;
        this.surname = surname;

        List<String> info = new ArrayList<>();
        info.add(contact.getInfo());

        contacts = new HashMap<>();
        contacts.put(contact.getType(), info);
    }

    public void addContact(Contact data) {
        if (!contacts.containsKey(data.getType())) {
            List<String> info = new ArrayList<>();
            info.add(data.getInfo());

            contacts.put(data.getType(), info);
        } else {
            if (contacts.get(data.getType()).contains(data.getInfo()))
                throw new IllegalArgumentException(CONTACT_IS_ALREADY_ADDED_MESSAGE);

            contacts.get(data.getType()).add(data.getInfo());
        }
    }

    public void editContact(Contact oldData, Contact newData) {
        deleteContact(oldData);
        addContact(newData);
    }

    public void deleteContact(Contact data) {
        if (!contacts.containsKey(data.getType()))
            throw new IllegalArgumentException(CONTACT_DOES_NOT_EXIST_MESSAGE);

        if (!contacts.get(data.getType()).contains(data.getInfo()))
            throw new IllegalArgumentException(CONTACT_DOES_NOT_EXIST_MESSAGE);

        contacts.get(data.getType()).remove(data.getInfo());

        if (contacts.get(data.getType()).isEmpty())
            contacts.remove(data.getType());
    }

    public String getInfo() {
        StringBuilder data = new StringBuilder(name + " " + surname + ":\n");

        for (ContactType type : contacts.keySet()) {
            data.append(type.toString()).append(": ");

            contacts.get(type).forEach(info -> data.append(info).append("; "));
            data.append("\n");
        }

        return data.toString();
    }

    public String toString() {
        StringBuilder data = new StringBuilder();
        data.append(name).append(surname);

        contacts.values().forEach(data::append);
        return data.toString();
    }
}
