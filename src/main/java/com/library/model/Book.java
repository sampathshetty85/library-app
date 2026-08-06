package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book")
public class Book extends AbstractLibraryItem {

    private String author;
    private int pageCount;

    protected Book() {}

    public Book(String title, String author, int pageCount) {
        super(title);
        this.author = author;
        this.pageCount = pageCount;
    }

    public String getAuthor() { return author; }

    public int getPageCount() { return pageCount; }

    public void setPageCount(int pageCount) {
        if (pageCount > 0) {
            this.pageCount = pageCount;
        }
    }

    @Override
    public String getSummary() {
        return "\"" + getTitle() + "\" by " + author + " (" + pageCount + " pages)";
    }
}
