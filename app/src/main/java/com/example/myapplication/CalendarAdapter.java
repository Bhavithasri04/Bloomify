package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private ArrayList<String> daysList;
    private HashMap<String, Integer> statusMap;
    private Context context;
    private String currentMonthYear;
    private SharedPreferences sharedPreferences;
    private TextView streakTextView;

    public CalendarAdapter(Context context, ArrayList<String> daysList, String currentMonthYear, TextView streakTextView) {
        this.context = context;
        this.daysList = daysList;
        this.currentMonthYear = currentMonthYear;
        this.streakTextView = streakTextView;
        this.statusMap = new HashMap<>();

        sharedPreferences = context.getSharedPreferences("CalendarPrefs", Context.MODE_PRIVATE);
        loadMarkedDates();
        updateLongestStreak();
    }

    private void loadMarkedDates() {
        for (String date : daysList) {
            String key = currentMonthYear + "_" + date;
            int state = sharedPreferences.getInt(key, 0);
            statusMap.put(date, state);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = daysList.get(position);
        holder.tvDate.setText(date);

        int currentState = statusMap.getOrDefault(date, 0);
        setColor(holder.cardView, currentState);

        holder.cardView.setOnClickListener(v -> {
            int newState = (currentState + 1) % 3;
            statusMap.put(date, newState);

            String key = currentMonthYear + "_" + date;
            sharedPreferences.edit().putInt(key, newState).apply();

            Log.d("CalendarAdapter", "Date: " + date + " | New State: " + newState);

            setColor(holder.cardView, newState);
            notifyItemChanged(position);
            updateLongestStreak();
        });
    }

    private void setColor(CardView cardView, int state) {
        switch (state) {
            case 1:
                cardView.setCardBackgroundColor(Color.GREEN);
                break;
            case 2:
                cardView.setCardBackgroundColor(Color.RED);
                break;
            default:
                cardView.setCardBackgroundColor(Color.WHITE);
                break;
        }
    }

    private void updateLongestStreak() {
        int longestStreak = 0;
        int currentStreak = 0;

        ArrayList<Integer> sortedDays = new ArrayList<>();
        for (String day : daysList) {
            try {
                sortedDays.add(Integer.parseInt(day));
            } catch (NumberFormatException e) {
                Log.e("CalendarAdapter", "Invalid day format: " + day);
            }
        }

        // Sort days in ascending order
        sortedDays.sort(Integer::compare);

        for (int i = 0; i < sortedDays.size(); i++) {
            int day = sortedDays.get(i);
            int state = statusMap.getOrDefault(String.valueOf(day), 0);

            if (state == 1) { // Green (completed)
                currentStreak++;
                longestStreak = currentStreak; // Set longestStreak to current streak directly
            } else { // Red (missed)
                currentStreak = 0; // Reset streak

            }
        }

        Log.d("CalendarAdapter", "Updated Longest Streak: " + longestStreak);
        streakTextView.setText("🔥 Longest Streak: " + longestStreak + " Days");
    }






    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
