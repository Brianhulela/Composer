package com.example.composer;

import java.util.Comparator;

public class SortComparator implements Comparator<NotesCard> {
    @Override
    public int compare(NotesCard o1, NotesCard o2) {
        return o2.getPosition() - o1.getPosition();
    }
}
