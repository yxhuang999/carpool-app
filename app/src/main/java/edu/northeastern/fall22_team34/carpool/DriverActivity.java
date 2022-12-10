package edu.northeastern.fall22_team34.carpool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.northeastern.fall22_team34.R;

public class DriverActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_driver);

        username = getIntent().getStringExtra("USERNAME");

        Button profileButton = findViewById(R.id.driver_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplicationContext(), DriverProfileActivity.class);
                profileIntent.putExtra("USERNAME", username);
                startActivity(profileIntent);
            }
        });

        Button vhProfileButton = findViewById(R.id.vehicle_profile);
        vhProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vhProfileIntent = new Intent(getApplicationContext(), VehicleProfileActivity.class);
                vhProfileIntent.putExtra("USERNAME", username);
                startActivity(vhProfileIntent);
            }
        });

        Button newRideButton = findViewById(R.id.driver_new_ride);
        newRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newRideIntent = new Intent(getApplicationContext(), NewRideActivity.class);
                newRideIntent.putExtra("USERNAME", username);
                startActivity(newRideIntent);
            }
        });
    }
}