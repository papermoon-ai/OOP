package data;

import java.time.LocalDateTime;

public class Entry<T> {
    private String headline;
    private LocalDateTime dateOfChange;
    private T content;
    private final Class<T> type;

    public Entry(Class<T> type) {
        this.type = type;
        dateOfChange = LocalDateTime.now();
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public LocalDateTime getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(LocalDateTime dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return headline;
    }

    public Class<T> getType() {
        return type;
    }
}
