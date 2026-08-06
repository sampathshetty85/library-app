package com.library.model;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private String name;
    private List<LibraryItem> items;

    public Library(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public void addItem(LibraryItem item) {
        items.add(item);
    }

    public void printAllItems() {
        System.out.println("=== " + name + " ===");
        for (LibraryItem item : items) {
            System.out.println(item.getSummary());
            System.out.println("  Available: " + item.isAvailable());
        }
    }

    public int getTotalItems() {
        return items.size();
    }
}
