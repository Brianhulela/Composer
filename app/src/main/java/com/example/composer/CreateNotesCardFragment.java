package com.example.composer;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNotesCardFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    FirebaseFirestore createNoteDatabase = FirebaseFirestore.getInstance();
    NotesCard notesCard;
    ArrayList<Note> allNewNotes = allNewNotes = new ArrayList<>();;
    NoteAdapter adapter;
    String noteTitle;
    TextInputEditText titleEdittext;
    AllNotesActivity allNotesActivity;

    // For the color pallete
    ImageButton colorPalleteImagebutton;
    RecyclerView colorPalleteRecyclerview;
    boolean palleteOpen = false;
    String defaultColor = "#1B1B1B";
    ConstraintLayout createCardContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.create_notes_card, parent, false);
    }

    public void showColorPallete(){
        if (palleteOpen){
            colorPalleteRecyclerview.setVisibility(View.VISIBLE);
        }else {
            colorPalleteRecyclerview.setVisibility(View.GONE);
        }

    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        allNotesActivity = (AllNotesActivity) getActivity();

        updateData();

        titleEdittext = view.findViewById(R.id.createCardTitleEdittext);

        createCardContainer = view.findViewById(R.id.createCardConstraintlayout);

        colorPalleteImagebutton = view.findViewById(R.id.colorPaletteImagebutton);
        colorPalleteImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (palleteOpen){
                    palleteOpen = false;
                }else {
                    palleteOpen = true;
                }
                showColorPallete();
            }
        });


        colorPalleteRecyclerview = view.findViewById(R.id.showColorPaletteRecyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        colorPalleteRecyclerview.setLayoutManager(linearLayoutManager);
        ColorPaletteAdapter colorPaletteAdapter = new ColorPaletteAdapter(getContext(), createCardContainer);
        colorPalleteRecyclerview.setAdapter(colorPaletteAdapter);

        //Setting the background color of the create card layout
        createCardContainer.setBackgroundColor(Color.parseColor(defaultColor));

        MaterialButton discardButton = view.findViewById(R.id.discardNoteCardButton);
        MaterialButton addButton = view.findViewById(R.id.addNoteCardButton);

        //Setting up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.createCardRecyclerview);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);


        adapter = new NoteAdapter(allNewNotes, getContext());

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);

        ImageButton addNoteButton = view.findViewById(R.id.createAddNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String position = String.valueOf(allNewNotes.size());

                boolean noteIndent = false;
                if (allNewNotes.size() > 0){
                    if (allNewNotes.get(allNewNotes.size()-1).isIndent()){
                        noteIndent = true;
                    }
                }


                allNewNotes.add(new Note("", false, position, noteIndent));
                adapter.notifyItemInserted(allNewNotes.size()-1);
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotesCard();
            }
        });
    }

    public void updateData() {
        for (NotesCard notesCard : allNotesActivity.allNotes){
            allNotesActivity.notesFirebaseHandler.updateCardInFirestore(notesCard);
        }
    }

    public void addNotesCard(){
        String noteTitle = titleEdittext.getText().toString();
        List<Object> cardNotes = new ArrayList<>();


        for(Note n : allNewNotes){
            Map<String, Object> note = new HashMap<>();
            note.put("noteText", n.getNoteText());
            note.put("checked", n.isChecked());
            note.put("position", String.valueOf(n.getPosition()));
            note.put("indent", n.isIndent());
            cardNotes.add(note);
        }


        Map<String, Object> notesCard = new HashMap<>();
        notesCard.put("title", noteTitle);
        notesCard.put("notes", cardNotes);
        notesCard.put("position", allNotesActivity.allNotes.size());

        int color = Color.TRANSPARENT;
        Drawable background = createCardContainer.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        notesCard.put("color", hexColor);


        // Add a new document with a generated ID
        createNoteDatabase.collection("Notes")
                .add(notesCard)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        if(fm.getBackStackEntryCount()>0) {
                            fm.popBackStack();
                        }
                        AllNotesActivity allNotesActivity  = (AllNotesActivity) getActivity();
                        allNotesActivity.notesFirebaseHandler.getDataFromFirestore();
                        Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
