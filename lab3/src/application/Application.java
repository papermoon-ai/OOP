package application;

import services.PhoneBook;

import java.util.List;

import static services.contacts.ContactType.MOBILE_PHONE;
import static services.contacts.ContactType.WORK_PHONE;
import static services.contacts.ContactType.HOME_PHONE;
import static services.contacts.ContactType.EMAIL;

public class Application {
    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();

        phoneBook.addEntry("Joseph", "Joestar", EMAIL, "lalala@gmail.com");
        phoneBook.addEntry("John", "Snow", MOBILE_PHONE, "+77123556789");
        phoneBook.addEntry("Adam", "Wilson", HOME_PHONE,"335577");
        phoneBook.addEntry("David", "Flatcher", EMAIL, "omaewa@gmail.com");

        phoneBook.addContact("Joseph", "Joestar", MOBILE_PHONE, "+44384123924");
        phoneBook.editContact("Adam", "Wilson",
                HOME_PHONE, "335577", WORK_PHONE, "+7800553535");

        System.out.println("Get one entry using name and surname as the key");
        System.out.println(phoneBook.get("David", "Flatcher"));

        phoneBook.deleteContact("David", "Flatcher", EMAIL, "omaewa@gmail.com");
        phoneBook.deleteEntry("David", "Flatcher");

        System.out.println("Get entries using char sequence");
        System.out.println("Search for \"jo\":");
        List<String> list = phoneBook.find("jo");
        list.forEach(System.out::println);

        System.out.println("Search for \"55\":");
        list = phoneBook.find("55");
        list.forEach(System.out::println);

        System.out.println("Get all of the entries:");
        list = phoneBook.getAll();
        list.forEach(System.out::println);
    }
}
