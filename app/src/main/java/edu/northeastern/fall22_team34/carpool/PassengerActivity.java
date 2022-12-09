package edu.northeastern.fall22_team34.carpool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.northeastern.fall22_team34.R;

public class PassengerActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_passenger);

        username = getIntent().getStringExtra("USERNAME");

        Button profileButton = findViewById(R.id.passenger_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplicationContext(), PassengerProfileActivity.class);
                profileIntent.putExtra("USERNAME", username);
                startActivity(profileIntent);
            }
        });
    }
}