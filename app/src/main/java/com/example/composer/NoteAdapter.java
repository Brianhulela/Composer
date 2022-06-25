package com.example.composer;


import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.viewHolder> implements ItemMoveCallback.ItemTouchHelperContract{

    Context context;
    List<Note> data;
    RecyclerView recyclerView;
    private float x1, x2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public NoteAdapter(List<Note> data, Context context) {
        this.context = context;
        this.data = data;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note, parent, false);
        viewHolder viewholder = new viewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Note note = data.get(holder.getAdapterPosition());

        holder.noteText.setText(note.getNoteText());
        holder.noteCheckbox.setChecked(note.isChecked());
        //Toast.makeText(context, String.valueOf(note.getPosition()), Toast.LENGTH_SHORT).show();

        indentNote(holder.noteLayout, holder.indentDivider, note);

        holder.noteCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.noteCheckbox.isChecked()){
                    data.get(holder.getAdapterPosition()).setChecked("true");
                }else{
                    data.get(holder.getAdapterPosition()).setChecked("false");
                }
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.noteText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    data.get(holder.getAdapterPosition()).setNoteText(s.toString());            }
        });

        holder.deleteNoteImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.delete_animation);
                holder.noteLayout.startAnimation(animation);

                holder.noteLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        data.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        Toast.makeText(context, "Note Deleted!", Toast.LENGTH_SHORT).show();
                    }

                }, 200); // 5000ms delay
            }
        });

        holder.noteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    //Toast.makeText(context, "Got the focus", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(context, "Lost the focus", Toast.LENGTH_LONG).show();
                    holder.deleteNoteImagebutton.setVisibility(View.INVISIBLE);

                }
            }
        });

        holder.noteText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    holder.deleteNoteImagebutton.setVisibility(View.VISIBLE);
                    //Toast.makeText(context, "You touched me", Toast.LENGTH_SHORT).show();
                }

                return false; // return is important...
            }
        });

        holder.dragButton.setOnTouchListener(new OnSwipeTouchListener(context.getApplicationContext()) {
            @Override
            public void onSwipeDown() { }

            @Override
            public void onSwipeLeft() {
                note.setIndent(false);
                indentNote(holder.noteLayout, holder.indentDivider, note);
            }

            @Override
            public void onTouch() { }

            @Override
            public void onSwipeUp() { }

            @Override
            public void onSwipeRight() {
                note.setIndent(true);
                indentNote(holder.noteLayout, holder.indentDivider, note);

            }
        });
    }



    public void indentNote(View view, View indentDivider, Note note){

        boolean indent = note.isIndent();

        ViewGroup.MarginLayoutParams noteLayoutMarginParams =(ViewGroup.MarginLayoutParams) view.getLayoutParams();

        if (indent){
            noteLayoutMarginParams.setMargins(150, 0, 0, 0);
            indentDivider.setVisibility(View.VISIBLE);
        }else if(!indent){
            noteLayoutMarginParams.setMargins(0, 0, 0, 0);
            indentDivider.setVisibility(View.INVISIBLE);
        }

        view.setLayoutParams(noteLayoutMarginParams);
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(viewHolder myViewHolder) {
        myViewHolder.noteLayout.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(viewHolder myViewHolder) {
        myViewHolder.noteLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextInputEditText noteText;
        ConstraintLayout noteLayout;
        ImageButton dragButton;
        MaterialCheckBox noteCheckbox;
        ImageButton deleteNoteImagebutton;
        MaterialDivider indentDivider;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            noteText = itemView.findViewById(R.id.checkedNoteTextview);
            noteLayout = itemView.findViewById(R.id.noteLayout);
            dragButton = itemView.findViewById(R.id.checkedDragNoteButton);
            noteCheckbox = itemView.findViewById(R.id.checkedNoteCheckbox);
            deleteNoteImagebutton = itemView.findViewById(R.id.checkedDeleteNoteImagebutton);
            indentDivider = itemView.findViewById(R.id.checkedIndentDivider);
        }
    }
}

