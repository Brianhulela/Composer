package com.example.composer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AllNotesActivity extends AppCompatActivity {

    ArrayList<NotesCard> allNotes;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    NotesCardsRecyclerAdapter notesCardsRecyclerAdapter;
    NotesFirebaseHandler notesFirebaseHandler;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);

        // Initialise the allNotes arrayList of NotesCard objects
        allNotes = new ArrayList<>();

        // Starting the shimmer layout when activity starts(while loading data from firebase);
        shimmerFrameLayout = findViewById(R.id.cardsShimmer);
        shimmerFrameLayout.startShimmer();

        // Setting up the recyclerView;
        recyclerView = findViewById(R.id.NotesCardsRecyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        notesCardsRecyclerAdapter = new NotesCardsRecyclerAdapter(this, allNotes);
        recyclerView.setAdapter(notesCardsRecyclerAdapter);

        // Initialise the fragment manager;
        fragmentManager = getSupportFragmentManager();

        // Instantiate the NotesFirebaseHandler object;
        notesFirebaseHandler = new NotesFirebaseHandler(fragmentManager,this, allNotes, recyclerView, shimmerFrameLayout, notesCardsRecyclerAdapter);
        notesFirebaseHandler.getDataFromFirestore();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Update firebase of all the changes made to notes during operation
        notesFirebaseHandler.updateData();
    }

    public void addNotesCard(View view){
        //Opens the CreateNotesCardFragment() to create a new notesCard
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.allNotesLayout, new CreateNotesCardFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}