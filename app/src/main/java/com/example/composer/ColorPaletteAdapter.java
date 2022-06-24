package com.example.composer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ColorPaletteAdapter extends RecyclerView.Adapter<ColorPaletteAdapter.viewHolder> {

    Context context;
    String[] data = new String[]{"#FFC62828", "#FF6A1B9A", "#FF283593", "#FF0277BD", "#FF00695C", "#FF558B2F", "#FFF9A825", "#FFEF6C00"};
    ConstraintLayout createCardLayout;

    public ColorPaletteAdapter(Context context, ConstraintLayout createCardLayout){
        this.context = context;
        this.createCardLayout = createCardLayout;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_palette_layout, parent, false);
        viewHolder viewholder = new viewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.colorbutton.setBackgroundColor(Color.parseColor(data[holder.getAdapterPosition()]));

        holder.colorbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createCardLayout.setBackgroundColor(Color.parseColor(data[holder.getAdapterPosition()]));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        Button colorbutton;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            colorbutton = itemView.findViewById(R.id.colorButton);
        }
    }
}
