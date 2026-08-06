package com.library.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "abstract_library_item")
public abstract class AbstractLibraryItem implements LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean available;

    protected AbstractLibraryItem() {}

    public AbstractLibraryItem(String title) {
        this.title = title;
        this.available = true;
    }

    public Long getId() { return id; }

    @Override
    public String getTitle() { return title; }

    @Override
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }
}
