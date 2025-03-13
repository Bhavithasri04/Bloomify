package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings;



import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddRoutineActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextTime;
    private RoutineDBHelper dbHelper;
    private String selectedTime = "--:--";
    private final ArrayList<String> selectedDates = new ArrayList<>();
    private TextView textSelectedDate;
    private ImageView btnCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);

        editTextTitle = findViewById(R.id.routine_title);
        editTextTime = findViewById(R.id.editTextTime);
        Button btnSave = findViewById(R.id.btnSaveRoutine);
        btnCalendar = findViewById(R.id.btnCalendar);
        textSelectedDate = findViewById(R.id.editTextRoutine);
        dbHelper = new RoutineDBHelper(this);

        // Weekday Selection Logic
        Map<TextView, String> dayMapping = new HashMap<>();
        dayMapping.put(findViewById(R.id.day_sun), "Sunday");
        dayMapping.put(findViewById(R.id.day_mon), "Monday");
        dayMapping.put(findViewById(R.id.day_tue), "Tuesday");
        dayMapping.put(findViewById(R.id.day_wed), "Wednesday");
        dayMapping.put(findViewById(R.id.day_thu), "Thursday");
        dayMapping.put(findViewById(R.id.day_fri), "Friday");
        dayMapping.put(findViewById(R.id.day_sat), "Saturday");

        for (TextView dayView : dayMapping.keySet()) {
            dayView.setOnClickListener(v -> {
                v.setSelected(!v.isSelected());
                String dayName = dayMapping.get(dayView);
                if (v.isSelected()) selectedDates.add(dayName);
                else selectedDates.remove(dayName);
                updateSelectedDateText();
            });
        }

        editTextTime.setOnClickListener(v -> showTimePicker());
        btnCalendar.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveRoutine());

        updateSelectedDateText();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Exact alarms are disabled. Please enable them in settings.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            editTextTime.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            // Add the selected date to the list without clearing previous ones
            calendar.set(year, month, day);
            String formattedDate = new SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(calendar.getTime());

            // Prevent duplicates in the selectedDates list
            if (!selectedDates.contains(formattedDate)) {
                selectedDates.add(formattedDate);
            }

            updateSelectedDateText();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void updateSelectedDateText() {
        textSelectedDate.setText(selectedDates.isEmpty() ? "Select a date" : "Repeats on " + String.join(", ", selectedDates));
    }

    private void saveRoutine() {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty() || selectedTime.equals("--:--") || selectedDates.isEmpty()) {
            Toast.makeText(this, "Please enter all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert selected dates to a comma-separated string
        String scheduleType = String.join(",", selectedDates);

        // Save to database
        Routine routine = new Routine(0, title, selectedTime, scheduleType, false);
        dbHelper.addRoutine(routine);

        // Extract hour & minute from selectedTime (formatted as "HH:mm")
        String[] timeParts = selectedTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Schedule Notification
        scheduleNotification(title, hour, minute);

        Toast.makeText(this, "Routine Saved!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }



    public void scheduleNotification(String routineName, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("routineName", routineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+ (API 31+)
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    Toast.makeText(this, "Exact alarms are disabled. Please enable them in settings.", Toast.LENGTH_LONG).show();
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

}
