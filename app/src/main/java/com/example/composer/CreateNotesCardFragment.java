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
    // Fragment for creating a new notes card

    // AllNotesActivity hosting the fragment
    AllNotesActivity allNotesActivity;

    FirebaseFirestore createNoteDatabase = FirebaseFirestore.getInstance();

    // List containing all the notes in card
    ArrayList<Note> allNewNotes = new ArrayList<>();

    // Fragment views
    ImageButton colorPalleteImagebutton;
    TextInputEditText titleEdittext;
    MaterialButton discardButton;
    MaterialButton addButton;
    ImageButton addNoteButton;

    // Notes Recyclerview things
    NoteAdapter adapter;
    RecyclerView notesRecylerview;
    LinearLayoutManager notesHorizontalLayoutManager;

    // For the color pallete
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
        // To show or close the color pallete when the pallete button is clicked
        if (palleteOpen){
            colorPalleteRecyclerview.setVisibility(View.VISIBLE);
        }else {
            colorPalleteRecyclerview.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Getting reference to AllNotesActivity hosting the fragment
        allNotesActivity = (AllNotesActivity) getActivity();

        // Initialising the views in Fragment
        createCardContainer = view.findViewById(R.id.createCardConstraintlayout);
        titleEdittext = view.findViewById(R.id.createCardTitleEdittext);
        colorPalleteImagebutton = view.findViewById(R.id.colorPaletteImagebutton);
        discardButton = view.findViewById(R.id.discardNoteCardButton);
        addButton = view.findViewById(R.id.addNoteCardButton);
        addNoteButton = view.findViewById(R.id.createAddNoteButton);

        // Updates all the changes made in AllNotesActivity before creating a new notesCard
        updateData();

        // Setting up the recyclerview for the color pallete
        colorPalleteRecyclerview = view.findViewById(R.id.showColorPaletteRecyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        colorPalleteRecyclerview.setLayoutManager(linearLayoutManager);
        ColorPaletteAdapter colorPaletteAdapter = new ColorPaletteAdapter(getContext(), createCardContainer);
        colorPalleteRecyclerview.setAdapter(colorPaletteAdapter);

        //Setting the background color of the create card layout
        createCardContainer.setBackgroundColor(Color.parseColor(defaultColor));

        //Setting up the notes RecyclerView
        notesRecylerview = view.findViewById(R.id.createCardRecyclerview);
        notesHorizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        notesRecylerview.setLayoutManager(notesHorizontalLayoutManager);
        adapter = new NoteAdapter(allNewNotes, getContext());
        // Allowing the dragging and rearranging of note functionality
        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(notesRecylerview);
        notesRecylerview.setAdapter(adapter);

        // Shows or closes the color pallete
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

        // Make a new note
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

        // Cancels notesCard creation
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
            }
        });

        // Add notescard to list of notesCards and firebase
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotesCard();
            }
        });
    }

    public void updateData() {
        // Updates all the changes made in AllNotesActivity before creating a new notesCard
        for (NotesCard notesCard : allNotesActivity.allNotes){
            allNotesActivity.notesFirebaseHandler.updateCardInFirestore(notesCard);
        }
    }

    public void addNotesCard(){
        // Adds the newly created notesCard to firebase
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
                        // Close the fragment after adding to firebase
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        if(fm.getBackStackEntryCount()>0) {
                            fm.popBackStack();
                        }
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
