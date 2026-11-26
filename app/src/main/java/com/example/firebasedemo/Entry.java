package com.example.firebasedemo;

public class Entry {
    public String name;
    public String message;

    public Entry() { } // required by Firebase

    public Entry(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
