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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;

        Entry that = (Entry) o;
        if (!this.getHeadline().equals(that.headline))
            return false;
        if (!this.getDateOfChange().isEqual(that.dateOfChange))
            return false;
        return this.getContent().equals(that.getContent());
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = prime * result + headline.hashCode();
        result = prime * result + dateOfChange.hashCode();
        result = prime * result + content.hashCode();
        return result;
    }
}
