package com.example.composer;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesFirebaseHandler {
    /*
    *  This class handles communication between firebase
    *  and the application notes updates;
    * */

    FragmentManager fragmentManager;
    FirebaseFirestore db;
    ArrayList<NotesCard> allNotes;
    ShimmerFrameLayout shimmerFrameLayout;
    NotesCardsRecyclerAdapter notesCardsRecyclerAdapter;
    RecyclerView recyclerView;
    Context context;


    public NotesFirebaseHandler(FragmentManager fragmentManager, Context context, ArrayList<NotesCard> allNotes, RecyclerView recyclerView, ShimmerFrameLayout shimmerFrameLayout, NotesCardsRecyclerAdapter notesCardsRecyclerAdapter){
        this.fragmentManager = fragmentManager;
        this.db = FirebaseFirestore.getInstance();
        this.allNotes = allNotes;
        this.shimmerFrameLayout = shimmerFrameLayout;
        this.notesCardsRecyclerAdapter = notesCardsRecyclerAdapter;
        this.recyclerView = recyclerView;
        this.context = context;
    }

    // Gets all the notes from firebase firestore;
    public void getDataFromFirestore() {

        allNotes.clear();
        db.collection("Notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                processData(document.getId(), document);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            notesCardsRecyclerAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Error loading notes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Delete a NotesCard from firebase;
    public void deleteNotesCardFromFirestore(String idFromCardClick){
        db.collection("Notes").document(idFromCardClick)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FragmentManager fm = fragmentManager;
                        if(fm.getBackStackEntryCount()>0) {
                            fm.popBackStack();
                        }
                        getDataFromFirestore();
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    // Updates all the NotesCard data in firebase;
    public void updateData() {
        for (NotesCard notesCard : allNotes){
            updateCardInFirestore(notesCard);
        }
    }

    // Updates individual NotesCard in firebase;
    public void updateCardInFirestore(NotesCard notesCard){
        String noteTitle = notesCard.getTitle();
        String noteColor = notesCard.getColor();
        List<Object> cardNotes = new ArrayList<>();


        for (Note n : notesCard.getNotes()) {
            Map<String, String> note = new HashMap<>();
            note.put("noteText", n.getNoteText().toString());
            note.put("checked", String.valueOf(n.isChecked()));
            note.put("position", String.valueOf(n.getPosition()));
            note.put("indent", String.valueOf(n.isIndent()));
            cardNotes.add(note);
        }


        Map<String, Object> cardToUpdate = new HashMap<>();
        cardToUpdate.put("title", noteTitle);
        cardToUpdate.put("notes", cardNotes);
        cardToUpdate.put("color", noteColor);

        // update the document
        db.collection("Notes").document(notesCard.getId())
                .set(notesCard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    // Creates NotesCard objects from firebase data;
    public void processData(String id, QueryDocumentSnapshot document){

        Object positionLong = document.getData().get("position");
        int positionInt =  Math.toIntExact((Long) positionLong);
        NotesCard notesCard = new NotesCard(id, document.getData().get("title").toString(), positionInt, document.getData().get("color").toString());

        List<Object> temp = (List<Object>) document.get("notes");
        for (int i=0; i<temp.size(); i++){
            HashMap<String, Object> map = new HashMap<>();
            map = (HashMap<String, Object>) temp.get(i);

            //tempTextview.setText(map.get("Checked").toString());
            String noteText = map.get("noteText").toString();
            boolean checked = (boolean) map.get("checked");
            String position = map.get("position").toString();
            boolean indent = (boolean) map.get("indent");

            Note note = new Note(noteText, checked, position, indent);
            notesCard.addNote(note);
        }
        allNotes.add(notesCard);
        Collections.sort(allNotes, new SortComparator());
    }

}
