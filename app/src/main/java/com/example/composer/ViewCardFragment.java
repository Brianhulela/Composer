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

public class ViewCardFragment extends Fragment {

    String idFromCardClick;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NotesCard notesCard;
    NoteAdapter adapter;
    AllNotesActivity allNotesActivity;

    TextInputEditText titleEdittext;
    ImageButton deleteCardImagebutton;
    ConstraintLayout viewConstraintLayout;

    // For the color pallete
    ImageButton colorPalleteImagebutton;
    RecyclerView colorPalleteRecyclerview;
    boolean palleteOpen = false;

    // For the checked notes
    RecyclerView checkedNotesRecyclerview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        idFromCardClick = getArguments().getString("id");

        return inflater.inflate(R.layout.view_note_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateParent();
    }

    public void updateParent(){
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
        if (palleteOpen){
            colorPalleteRecyclerview.setVisibility(View.VISIBLE);
        }else {
            colorPalleteRecyclerview.setVisibility(View.GONE);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleEdittext = view.findViewById(R.id.notesTitleEdittext);
        viewConstraintLayout = view.findViewById(R.id.viewConstraintLayout);

        allNotesActivity  = (AllNotesActivity) getActivity();
        //Get data from click
        getData();
        setCardColor();

        deleteCardImagebutton = view.findViewById(R.id.deleteCardImagebutton);
        deleteCardImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allNotesActivity.notesFirebaseHandler.deleteNotesCardFromFirestore(idFromCardClick);
            }
        });

        //Setting up the color pallete tingz

        colorPalleteRecyclerview = view.findViewById(R.id.viewCardPaletteRecyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        colorPalleteRecyclerview.setLayoutManager(linearLayoutManager);
        ColorPaletteAdapter colorPaletteAdapter = new ColorPaletteAdapter(getContext(), viewConstraintLayout);
        colorPalleteRecyclerview.setAdapter(colorPaletteAdapter);

        colorPalleteImagebutton = view.findViewById(R.id.viewCardPalleteImagebutton);
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


        //Setting up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.notesRecyclerview);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new NoteAdapter(notesCard.getNotes(), getContext());

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);

        ImageButton addNoteButton = view.findViewById(R.id.createAddNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String position = String.valueOf(notesCard.getNotes().size());
                notesCard.getNotes().add(new Note("", false, position, false));
                adapter.notifyItemInserted(notesCard.getNotes().size()-1);
                allNotesActivity.notesCardsRecyclerAdapter.notifyItemChanged(notesCard.getPosition());
            }
        });


        // Setting up the checkedNotesRecyclerview
        checkedNotesRecyclerview = view.findViewById(R.id.checkedNotesRecyclerview);
        LinearLayoutManager checkedNotesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        checkedNotesRecyclerview.setLayoutManager(checkedNotesLayoutManager);
        CheckedNotesRecyclerAdapter checkedNotesRecyclerAdapter = new CheckedNotesRecyclerAdapter(getContext(), notesCard.getNotes());
        checkedNotesRecyclerview.setAdapter(checkedNotesRecyclerAdapter);
    }

    public void setCardColor(){
        if(!notesCard.getColor().equals("-")){
            viewConstraintLayout.setBackgroundColor(Color.parseColor(notesCard.getColor()));
        }
    }

    public void getData(){
        for (NotesCard nC : allNotesActivity.allNotes){
            if (nC.getId().equals(idFromCardClick)){
                notesCard = nC;
                titleEdittext.setText(notesCard.getTitle());
            }
        }
    }
}
