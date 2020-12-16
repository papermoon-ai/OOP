package ui;

import data.Entry;
import data.sources.EntrySource;
import data.sources.FileSource;
import ui.additionalClasses.GhostTextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInterface extends JDialog {
    private JPanel contentPane;
    private JList entriesList;
    private JButton addTextButton;
    private JButton removeButton;
    private JButton addTodoListButton;
    private JButton addImageButton;
    private JTextField filterField;
    private JButton filterButton;
    private JFormattedTextField date1Field;
    private JFormattedTextField date2Field;
    private JButton editButton;
    private JCheckBox textCheckBox;
    private JCheckBox todoListCheckBox;
    private JCheckBox imageCheckBox;
    private JLabel errorLabel;
    private FileSource source;

    public UserInterface() throws IOException {
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Записная книжка");

        source = new EntrySource("entries.json");

        DefaultListModel<Entry> dlm = new DefaultListModel<>();
        dlm.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                entriesList.setListData(dlm.toArray());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                entriesList.setListData(dlm.toArray());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                entriesList.setListData(dlm.toArray());
            }
        });

        try {
            dlm.addAll(source.fromFile());
        } catch (IOException e) {
            throw new IOException("An exception has occurred while reading data from files");
        }

        addTextButton.addActionListener(e -> {
            JDialog dialog = new EditText(dlm::addElement);
        });

        addTodoListButton.addActionListener(e -> {
            JDialog dialog = new EditTodoList(dlm::addElement);
        });

        addImageButton.addActionListener(e -> {
            JDialog dialog = new EditImage(dlm::addElement);
        });

        editButton.addActionListener(e -> {
            errorLabel.setText("");
            if (entriesList.isSelectionEmpty())
                errorLabel.setText("Выберите элемент списка");

            Entry entry = (Entry) entriesList.getSelectedValue();

            if (entry.getType() == String.class) {
                JDialog dialog = new EditText(entry);
            } else if (entry.getType() == byte[].class) {
                JDialog dialog = new EditImage(entry);
            } else {
                JDialog dialog = new EditTodoList(entry);
            }
        });

        removeButton.addActionListener(e -> {
            errorLabel.setText("");

            if (entriesList.isSelectionEmpty())
                errorLabel.setText("Выберите элемент списка");

            Entry entry = (Entry) entriesList.getSelectedValue();
            dlm.removeElement(entry);

            filterButton.doClick();
        });

        filterButton.addActionListener(e -> {
            errorLabel.setText("");

            List<Entry> temp = new ArrayList<>();
            Arrays.stream(dlm.toArray()).forEach(element -> temp.add((Entry) element));

            if (!filterField.getText().equals("Введите ключевые слова")) {
                String filter = filterField.getText();

                temp.removeIf(entry -> {
                    if (entry.getHeadline().toLowerCase().contains(filter.toLowerCase()))
                        return false;
                    else return !entry.getContent().toString().toLowerCase().contains(filter.toLowerCase());
                });
            }

            temp.removeIf(entry -> {
                if (!textCheckBox.isSelected() && entry.getType() == String.class)
                    return true;
                else if (!todoListCheckBox.isSelected() && entry.getType() == ArrayList.class)
                    return true;
                else return !imageCheckBox.isSelected() && entry.getType() == byte[].class;
            });

            if ((!date1Field.getText().equals("__.__.____") || !date2Field.getText().equals("__.__.____"))) {
                try {
                    LocalDate date1;
                    LocalDate date2;

                    if (!date1Field.getText().equals("__.__.____"))
                        date1 = LocalDate.parse(date1Field.getText(),
                                DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    else
                        date1 = LocalDate.MIN;


                    if (!date2Field.getText().equals("__.__.____"))
                        date2 = LocalDate.parse(date2Field.getText(),
                                DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    else
                        date2 = LocalDate.now();


                    if (date1.isAfter(date2)) {
                        errorLabel.setText("Неверно указан период времени");
                        return;
                    }

                    temp.removeIf(entry -> {
                        LocalDate entryDate = entry.getDateOfChange().toLocalDate();
                        if (entryDate.isEqual(date1) || entryDate.isEqual(date2)) return false;
                        return !entryDate.isAfter(ChronoLocalDate.from(date1))
                                || !entryDate.isBefore(ChronoLocalDate.from(date2));
                    });
                } catch (DateTimeParseException ex) {
                    errorLabel.setText("Неверный формат входных данных");
                }
            }

            entriesList.setListData(temp.toArray());
        });

        entriesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    Entry entry = (Entry) entriesList.getSelectedValue();

                    if (entry.getType() == String.class) {
                        JDialog dialog = new EditText(entry);
                    } else if (entry.getType() == byte[].class) {
                        JDialog dialog = new EditImage(entry);
                    } else {
                        JDialog dialog = new EditTodoList(entry);
                    }
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                List<Entry> updatedEntries = new ArrayList<>();
                Arrays.stream(dlm.toArray()).forEach(element -> updatedEntries.add((Entry) element));

                try {
                    source.toFile(updatedEntries);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                e.getWindow().dispose();
            }
        });

        setSize(650, 450);
        setVisible(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setBackground(new Color(-1));
        contentPane.setForeground(new Color(-1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-1));
        panel1.setForeground(new Color(-1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-1));
        panel2.setForeground(new Color(-1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        removeButton = new JButton();
        removeButton.setBackground(new Color(-35473));
        Font removeButtonFont = this.$$$getFont$$$(null, Font.PLAIN, 14, removeButton.getFont());
        if (removeButtonFont != null) removeButton.setFont(removeButtonFont);
        removeButton.setText("Удалить");
        panel2.add(removeButton, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addTodoListButton = new JButton();
        addTodoListButton.setBackground(new Color(-7426629));
        Font addTodoListButtonFont = this.$$$getFont$$$(null, Font.PLAIN, 14, addTodoListButton.getFont());
        if (addTodoListButtonFont != null) addTodoListButton.setFont(addTodoListButtonFont);
        addTodoListButton.setText("Добавить Todo List");
        panel2.add(addTodoListButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addImageButton = new JButton();
        addImageButton.setBackground(new Color(-7426629));
        Font addImageButtonFont = this.$$$getFont$$$(null, Font.PLAIN, 14, addImageButton.getFont());
        if (addImageButtonFont != null) addImageButton.setFont(addImageButtonFont);
        addImageButton.setText("Добавить изображение");
        panel2.add(addImageButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        addTextButton = new JButton();
        addTextButton.setBackground(new Color(-7426629));
        Font addTextButtonFont = this.$$$getFont$$$(null, Font.PLAIN, 14, addTextButton.getFont());
        if (addTextButtonFont != null) addTextButton.setFont(addTextButtonFont);
        addTextButton.setText("Добавить текст");
        panel2.add(addTextButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editButton = new JButton();
        editButton.setBackground(new Color(-628));
        Font editButtonFont = this.$$$getFont$$$(null, Font.PLAIN, 14, editButton.getFont());
        if (editButtonFont != null) editButton.setFont(editButtonFont);
        editButton.setText("Изменить");
        panel2.add(editButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        errorLabel = new JLabel();
        Font errorLabelFont = this.$$$getFont$$$(null, Font.PLAIN, 12, errorLabel.getFont());
        if (errorLabelFont != null) errorLabel.setFont(errorLabelFont);
        errorLabel.setForeground(new Color(-64224));
        errorLabel.setText("");
        panel2.add(errorLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-1));
        contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.PLAIN, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("\uD83D\uDD0D");
        panel3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Font filterFieldFont = this.$$$getFont$$$(null, Font.PLAIN, 14, filterField.getFont());
        if (filterFieldFont != null) filterField.setFont(filterFieldFont);
        filterField.putClientProperty("html.disable", Boolean.FALSE);
        panel3.add(filterField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setBackground(new Color(-1));
        contentPane.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textCheckBox = new JCheckBox();
        textCheckBox.setBackground(new Color(-1));
        Font textCheckBoxFont = this.$$$getFont$$$(null, Font.PLAIN, 14, textCheckBox.getFont());
        if (textCheckBoxFont != null) textCheckBox.setFont(textCheckBoxFont);
        textCheckBox.setSelected(true);
        textCheckBox.setText("Текст");
        panel4.add(textCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        todoListCheckBox = new JCheckBox();
        todoListCheckBox.setBackground(new Color(-1));
        Font todoListCheckBoxFont = this.$$$getFont$$$(null, Font.PLAIN, 14, todoListCheckBox.getFont());
        if (todoListCheckBoxFont != null) todoListCheckBox.setFont(todoListCheckBoxFont);
        todoListCheckBox.setSelected(true);
        todoListCheckBox.setText("Todo List");
        panel4.add(todoListCheckBox, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        imageCheckBox = new JCheckBox();
        imageCheckBox.setBackground(new Color(-1));
        Font imageCheckBoxFont = this.$$$getFont$$$(null, Font.PLAIN, 14, imageCheckBox.getFont());
        if (imageCheckBoxFont != null) imageCheckBox.setFont(imageCheckBoxFont);
        imageCheckBox.setSelected(true);
        imageCheckBox.setText("Изображения");
        panel4.add(imageCheckBox, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filterButton = new JButton();
        filterButton.setBackground(new Color(-7284327));
        Font filterButtonFont = this.$$$getFont$$$(null, Font.PLAIN, 14, filterButton.getFont());
        if (filterButtonFont != null) filterButton.setFont(filterButtonFont);
        filterButton.setHorizontalAlignment(0);
        filterButton.setHorizontalTextPosition(11);
        filterButton.setText("Применить фильтр");
        panel4.add(filterButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setBackground(new Color(-1));
        Font panel5Font = this.$$$getFont$$$(null, Font.PLAIN, 14, panel5.getFont());
        if (panel5Font != null) panel5.setFont(panel5Font);
        contentPane.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.PLAIN, 14, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("—");
        panel5.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Font date1FieldFont = this.$$$getFont$$$(null, Font.PLAIN, 14, date1Field.getFont());
        if (date1FieldFont != null) date1Field.setFont(date1FieldFont);
        panel5.add(date1Field, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        Font date2FieldFont = this.$$$getFont$$$(null, Font.PLAIN, 14, date2Field.getFont());
        if (date2FieldFont != null) date2Field.setFont(date2FieldFont);
        panel5.add(date2Field, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.setBackground(new Color(-1));
        contentPane.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 4, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel6.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        entriesList = new JList();
        entriesList.setBackground(new Color(-1));
        Font entriesListFont = this.$$$getFont$$$(null, Font.PLAIN, 14, entriesList.getFont());
        if (entriesListFont != null) entriesList.setFont(entriesListFont);
        scrollPane1.setViewportView(entriesList);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setBackground(new Color(-1));
        panel6.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.PLAIN, 14, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Записи");
        panel7.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        filterField = new JTextField();
        GhostTextField ghostFilterText = new GhostTextField(filterField, "Введите ключевые слова");
        date1Field = new JFormattedTextField();
        GhostTextField ghostDate1 = new GhostTextField(date1Field, "__.__.____");
        date2Field = new JFormattedTextField();
        GhostTextField ghostDate2 = new GhostTextField(date2Field, "__.__.____");
    }
}
