package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_ROUTINE_REQUEST = 1;

    private ArrayList<Routine> routineList;
    private RoutineAdapter routineAdapter;
    private RoutineDBHelper dbHelper;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        createNotificationChannel();
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        FloatingActionButton fabAddRoutine = findViewById(R.id.fabAddRoutine);
//
//
//        dbHelper = new RoutineDBHelper(this);
//
//        // Load data from DB
//        routineList = dbHelper.getAllRoutines();
//
//        // Setup RecyclerView with item click listener
//        routineAdapter = new RoutineAdapter(routineList, new RoutineAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Routine routine) {
//                Intent intent = new Intent(MainActivity.this, RoutineDetailsActivity.class);
//                intent.putExtra("ROUTINE_ID", routine.getId());
//                startActivity(intent);
//            }
//        });
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(routineAdapter);
//
//        // Floating Action Button Click Listener
//        fabAddRoutine.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, AddRoutineActivity.class);
//            startActivityForResult(intent, ADD_ROUTINE_REQUEST);
//        });
//
//
//        routineAdapter = new RoutineAdapter(routineList, new RoutineAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Routine routine) {
//                Intent intent = new Intent(MainActivity.this, RoutineDetailsActivity.class);
//                intent.putExtra("ROUTINE_ID", routine.getId());
//                startActivity(intent);
//            }
//        }, new RoutineAdapter.OnDeleteClickListener() {
//            @Override
//            public void onDeleteClick(Routine routine, int position) {
//                dbHelper.deleteRoutine(routine.getId());
//                routineAdapter.removeRoutine(position);
//                Toast.makeText(MainActivity.this, "Routine deleted", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAddRoutine = findViewById(R.id.fabAddRoutine);

        dbHelper = new RoutineDBHelper(this);

        // Load data from DB
        routineList = dbHelper.getAllRoutines();

        // Setup RecyclerView with item click listener and delete click listener
        routineAdapter = new RoutineAdapter(routineList, new RoutineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Routine routine) {
                Intent intent = new Intent(MainActivity.this, RoutineDetailsActivity.class);
                intent.putExtra("ROUTINE_ID", routine.getId());
                startActivity(intent);
            }
        }, (routine, position) -> {
            dbHelper.deleteRoutine(routine.getId());
            routineList.remove(position);  // Update the list
            routineAdapter.notifyItemRemoved(position);
            Toast.makeText(MainActivity.this, "Routine deleted", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(routineAdapter);

        // Floating Action Button Click Listener
        fabAddRoutine.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRoutineActivity.class);
            startActivityForResult(intent, ADD_ROUTINE_REQUEST);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ROUTINE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra("ROUTINE_TITLE");
            String time = data.getStringExtra("ROUTINE_TIME");
            String scheduleType = data.getStringExtra("ROUTINE_SCHEDULE_TYPE");

            // Create the new Routine object with a default completion status of false
            Routine newRoutine = new Routine(0, title, time, scheduleType, false);
            dbHelper.addRoutine(newRoutine);

            // Update UI
            routineList.add(newRoutine);
            routineAdapter.notifyItemInserted(routineList.size() - 1);
        }
    }



    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RoutineReminderChannel";
            String description = "Channel for Routine Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("bloomify_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}
