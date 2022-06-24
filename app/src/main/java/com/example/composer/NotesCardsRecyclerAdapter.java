package com.example.composer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NotesCardsRecyclerAdapter extends RecyclerView.Adapter<NotesCardsRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<NotesCard> notesCards;

    public NotesCardsRecyclerAdapter(Context context, ArrayList<NotesCard> notesCards){
        this.context = context;
        this.notesCards = notesCards;
    }

    @NonNull
    @Override
    public NotesCardsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesCardsRecyclerAdapter.ViewHolder holder, int position) {

        NotesCard notesCard = notesCards.get(holder.getAdapterPosition());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        holder.cardNotesRecyclerview.setLayoutManager(linearLayoutManager);
        NoteAdapter noteAdapter = new NoteAdapter(notesCard.getNotes(), context);
        holder.cardNotesRecyclerview.setAdapter(noteAdapter);

        //Setting the background color of the notes card

        //holder.card.setBackgroundColor(Color.parseColor(notesCard.getColor()));
        //Drawable res = context.getDrawable(R.drawable.rounded_corners);

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.rounded_corners);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(notesCard.getColor()));
        holder.card.setBackgroundDrawable(wrappedDrawable);



        holder.notesCardTitle.setText(notesCard.getTitle());

        holder.editImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNotesCard(notesCard.getId());
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNotesCard(notesCard.getId());

            }
        });

    }

    public void viewNotesCard(String id){

        //Sending the reference of the Card to the Fragment
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        // set Fragmentclass Arguments
        ViewCardFragment viewCardFragment = new ViewCardFragment();
        viewCardFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.allNotesLayout, viewCardFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return notesCards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView notesCardTitle;
        RecyclerView cardNotesRecyclerview;
        MaterialCardView card;
        ImageButton editImagebutton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notesCardTitle = itemView.findViewById(R.id.notesCardTitleTextview);
            cardNotesRecyclerview = itemView.findViewById(R.id.notesCardRecyclerview);
            card = itemView.findViewById(R.id.card);
            editImagebutton = itemView.findViewById(R.id.editImagebutton);
        }
    }
}
