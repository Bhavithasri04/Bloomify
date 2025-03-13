package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RoutineDetailsActivity extends AppCompatActivity {
    private TextView tvMonthYear, streakTextView;
    private RecyclerView rvCalendar;
    private Button btnPrev, btnNext;
    private CalendarAdapter adapter;
    private Calendar currentCalendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_details);

        tvMonthYear = findViewById(R.id.tv_month_year);
        rvCalendar = findViewById(R.id.rv_calendar);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        streakTextView = findViewById(R.id.streakTextView);

        currentCalendar = Calendar.getInstance();

        setupCalendar();

        btnPrev.setOnClickListener(v -> changeMonth(-1));
        btnNext.setOnClickListener(v -> changeMonth(1));

        // Retrieve routine ID from intent
        String routineId = getIntent().getStringExtra("routine_id");

        // Get saved completed and not completed dates from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("RoutineStreaks", MODE_PRIVATE);
        String completedDatesString = prefs.getString("completed_" + routineId, "");
        String notCompletedDatesString = prefs.getString("not_completed_" + routineId, "");

        // Convert them back to lists
        List<String> completedDates = Arrays.asList(completedDatesString.split(","));
        List<String> notCompletedDates = Arrays.asList(notCompletedDatesString.split(","));

        // Update calendar UI based on these lists
        updateCalendar(completedDates, notCompletedDates);
    }

    private void setupCalendar() {
        ArrayList<String> daysList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String currentMonthYear = sdf.format(currentCalendar.getTime());

        tvMonthYear.setText(currentMonthYear);

        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            daysList.add(String.valueOf(i));
        }

        adapter = new CalendarAdapter(this, daysList, currentMonthYear, streakTextView);
        rvCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        rvCalendar.setAdapter(adapter);
    }

    private void changeMonth(int delta) {
        currentCalendar.add(Calendar.MONTH, delta);
        setupCalendar();
    }

    private void updateCalendar(List<String> completedDates, List<String> notCompletedDates) {
        // Implement logic to update the calendar UI with completed and not completed dates
    }
}
