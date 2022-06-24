package com.example.composer;

public class Note {
    String noteText;
    boolean isChecked;
    int position;
    boolean indent;

    public Note(String noteText, boolean isChecked, String position, boolean indent){
        this.noteText = noteText;
        this.isChecked = isChecked;
        this.position = Integer.parseInt(position);
        this.indent = indent;
    }

    public boolean isIndent() {
        return indent;
    }

    public void setIndent(){

    }

    public void setIndent(boolean indent) {
        this.indent = indent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(String checked) {
        isChecked = Boolean.parseBoolean(checked);
    }


}
