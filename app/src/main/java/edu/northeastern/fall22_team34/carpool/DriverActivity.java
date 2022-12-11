package edu.northeastern.fall22_team34.carpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.models.User;

public class DriverActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String username;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_driver);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");

        mDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (username.equals(dataSnapshot.getValue(User.class).username)) {
                        user = dataSnapshot.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button profileButton = findViewById(R.id.driver_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplicationContext(),
                        DriverProfileActivity.class);
                profileIntent.putExtra("USERNAME", username);
                startActivity(profileIntent);
            }
        });

        Button vhProfileButton = findViewById(R.id.vehicle_profile);
        vhProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vhProfileIntent = new Intent(getApplicationContext(),
                        VehicleProfileActivity.class);
                vhProfileIntent.putExtra("USERNAME", username);
                startActivity(vhProfileIntent);
            }
        });

        Button newTripButton = findViewById(R.id.driver_new_trip);
        newTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.driverProfile == null || user.vehicleProfile == null) {
                    Toast.makeText(DriverActivity.this,
                            "Please Create Your Driver and Vehicle Profile First",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent newTripIntent = new Intent(getApplicationContext(),
                            DriverNewTripActivity.class);
                    newTripIntent.putExtra("USERNAME", username);
                    startActivity(newTripIntent);
                }
            }
        });

        Button tripHistoryButton = findViewById(R.id.driver_history);
        tripHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(getApplicationContext(),
                        DriverTripHistoryActivity.class);
                historyIntent.putExtra("USERNAME", username);
                startActivity(historyIntent);
            }
        });
    }
}