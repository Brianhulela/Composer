package com.example.composer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CheckedNotesRecyclerAdapter extends RecyclerView.Adapter<CheckedNotesRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Note> data;

    public CheckedNotesRecyclerAdapter(Context context, ArrayList<Note> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CheckedNotesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checked_note, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckedNotesRecyclerAdapter.ViewHolder holder, int position) {
        Note note = data.get(holder.getAdapterPosition());
        holder.checkedNoteTextview.setText(note.getNoteText());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView checkedNoteTextview;
        MaterialCheckBox checkedNoteCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkedNoteTextview = itemView.findViewById(R.id.checkedNoteTextview);
            checkedNoteCheckbox = itemView.findViewById(R.id.checkedNoteCheckbox);

        }
    }
}
