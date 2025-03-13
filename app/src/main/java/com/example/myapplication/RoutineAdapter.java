package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private ArrayList<Routine> routineList;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    public RoutineAdapter(ArrayList<Routine> routineList, OnItemClickListener listener, OnDeleteClickListener deleteListener) {
        this.routineList = routineList != null ? routineList : new ArrayList<>();
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routineList.get(position);
        holder.textRoutineTitle.setText(routine.getTitle());
        holder.textRoutineTime.setText(routine.getTime());
        holder.textRoutineSchedule.setText(routine.getScheduleType());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(routine));

        holder.btnDeleteRoutine.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(routine, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routineList.size();
    }

    public void updateList(ArrayList<Routine> newList) {
        if (newList != null) {
            routineList.clear();
            routineList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    public void removeRoutine(int position) {
        routineList.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Routine routine);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Routine routine, int position);
    }

    public static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView textRoutineTitle, textRoutineTime, textRoutineSchedule;
        ImageView btnDeleteRoutine;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            textRoutineTitle = itemView.findViewById(R.id.textRoutineTitle);
            textRoutineTime = itemView.findViewById(R.id.textRoutineTime);
            textRoutineSchedule = itemView.findViewById(R.id.textRoutineSchedule);
            btnDeleteRoutine = itemView.findViewById(R.id.btnDeleteRoutine);
        }
    }
}
