package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "magazine")
public class Magazine extends AbstractLibraryItem {

    private String publisher;
    private int issueNumber;

    protected Magazine() {}

    public Magazine(String title, String publisher, int issueNumber) {
        super(title);
        this.publisher = publisher;
        this.issueNumber = issueNumber;
    }

    public String getPublisher() { return publisher; }

    public int getIssueNumber() { return issueNumber; }

    @Override
    public String getSummary() {
        return "\"" + getTitle() + "\" — Issue #" + issueNumber + " by " + publisher;
    }
}
