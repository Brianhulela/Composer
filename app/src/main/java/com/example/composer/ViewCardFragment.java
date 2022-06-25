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
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewCardFragment extends Fragment {

    // The activity hosting the Fragment
    AllNotesActivity allNotesActivity;

    // From clicked card in the activity
    String idFromCardClick;
    NotesCard notesCard;

    // The notes recyclerview
    RecyclerView notesRecyclerView;
    LinearLayoutManager notesHorizontalLayoutManager;
    NoteAdapter adapter;

    // Layout views
    TextInputEditText titleEdittext;
    ImageButton deleteCardImagebutton;
    ConstraintLayout viewConstraintLayout;
    ImageButton addNoteButton;

    // For the color pallete
    ImageButton colorPalleteImagebutton;
    RecyclerView colorPalleteRecyclerview;
    LinearLayoutManager colorPalleteLinearLayoutManager;
    ColorPaletteAdapter colorPaletteAdapter;
    boolean palleteOpen = false;

    // For the checked notes
    RecyclerView checkedNotesRecyclerview;
    LinearLayoutManager checkedNotesLayoutManager;
    CheckedNotesRecyclerAdapter checkedNotesRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Assign fragment layout to fragment
        idFromCardClick = getArguments().getString("id");
        return inflater.inflate(R.layout.view_note_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Update notesCard in AllNotesActivity when fragment is closed
        updateParent();
    }

    public void updateParent(){
        // Updates the AllNotes activity card of any changes made while viewing the card
        if (((allNotesActivity.allNotes.size()-1) - notesCard.getPosition()) >= 0){
            String noteTitle = titleEdittext.getText().toString();
            notesCard.setTitle(noteTitle);

            int color = Color.TRANSPARENT;
            Drawable background = viewConstraintLayout.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();
            String hexColor = String.format("#%06X", (0xFFFFFF & color));

            notesCard.setColor(hexColor);
            allNotesActivity.allNotes.set((allNotesActivity.allNotes.size()-1) - notesCard.getPosition(), notesCard);
            allNotesActivity.notesCardsRecyclerAdapter.notifyItemChanged((allNotesActivity.allNotes.size()-1) - notesCard.getPosition());

        }

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialising the layout views
        titleEdittext = view.findViewById(R.id.notesTitleEdittext);
        viewConstraintLayout = view.findViewById(R.id.viewConstraintLayout);
        deleteCardImagebutton = view.findViewById(R.id.deleteCardImagebutton);
        colorPalleteImagebutton = view.findViewById(R.id.viewCardPalleteImagebutton);
        addNoteButton = view.findViewById(R.id.createAddNoteButton);


        // Getting the hosting activity of fragment
        allNotesActivity  = (AllNotesActivity) getActivity();

        //Get data from click
        getData();
        setCardColor();

        //Setting up the color pallete recyclerview
        colorPalleteRecyclerview = view.findViewById(R.id.viewCardPaletteRecyclerview);
        colorPalleteLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        colorPalleteRecyclerview.setLayoutManager(colorPalleteLinearLayoutManager);
        colorPaletteAdapter = new ColorPaletteAdapter(getContext(), viewConstraintLayout);
        colorPalleteRecyclerview.setAdapter(colorPaletteAdapter);

        //Setting up the notes RecyclerView
        notesRecyclerView = view.findViewById(R.id.notesRecyclerview);
        notesHorizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        notesRecyclerView.setLayoutManager(notesHorizontalLayoutManager);
        adapter = new NoteAdapter(notesCard.getNotes(), getContext());

        // Callback for the dragging and rearranging of the notes
        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(notesRecyclerView);

        notesRecyclerView.setAdapter(adapter);

        // Setting up the checkedNotesRecyclerview
        checkedNotesRecyclerview = view.findViewById(R.id.checkedNotesRecyclerview);
        checkedNotesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        checkedNotesRecyclerview.setLayoutManager(checkedNotesLayoutManager);

        // Making the checked notes arrayList
        ArrayList<Note> checkedNotes = new ArrayList<>();
        for (Note note : notesCard.getNotes()){
            if (note.isChecked){
                checkedNotes.add(note);
            }
        }

        // Binding the checkedNotes to the checkedNotesRecyclerview;
        checkedNotesRecyclerAdapter = new CheckedNotesRecyclerAdapter(getContext(), checkedNotes);
        checkedNotesRecyclerview.setAdapter(checkedNotesRecyclerAdapter);

        // Delete the notescard when delete button is clicked
        deleteCardImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allNotesActivity.notesFirebaseHandler.deleteNotesCardFromFirestore(idFromCardClick);
            }
        });

        // The onClickLister to show/close the color pallete
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


        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String position = String.valueOf(notesCard.getNotes().size());
                notesCard.getNotes().add(new Note("", false, position, false));
                adapter.notifyItemInserted(notesCard.getNotes().size()-1);
                allNotesActivity.notesCardsRecyclerAdapter.notifyItemChanged(notesCard.getPosition());
            }
        });

    }

    public void setCardColor(){
        if(!notesCard.getColor().equals("-")){
            viewConstraintLayout.setBackgroundColor(Color.parseColor(notesCard.getColor()));
        }
    }

    public void getData(){
        // Searches all notesCards and finds the clicked notes card from idFromClick
        for (NotesCard nC : allNotesActivity.allNotes){
            if (nC.getId().equals(idFromCardClick)){
                notesCard = nC;
                titleEdittext.setText(notesCard.getTitle());
            }
        }
    }
}
