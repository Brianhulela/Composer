package com.example.composer;

import java.util.ArrayList;

public class NotesCard {
    String id;
    String title;
    ArrayList<Note> notes;
    int position;
    String color;

    public NotesCard(String id, String title, int position, String color){
        this.id = id;
        this.title = title;
        this.notes = new ArrayList<>();
        this.position = position;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }
}
