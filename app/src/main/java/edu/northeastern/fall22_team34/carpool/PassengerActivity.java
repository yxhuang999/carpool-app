package edu.northeastern.fall22_team34.carpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.models.User;

public class PassengerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseDatabase mDatabase;

    private User user;
    private String username;

    private Spinner distanceSpinner;
    private String distanceSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_passenger);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");

        distanceSpinner = findViewById(R.id.distance_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.nearbyTrip_distance, R.layout.item_container_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter);

        distanceSpinner.setOnItemSelectedListener(this);

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
                Toast.makeText(PassengerActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button profileButton = findViewById(R.id.passenger_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplicationContext(),
                        PassengerProfileActivity.class);
                profileIntent.putExtra("USERNAME", username);
                startActivity(profileIntent);
            }
        });

        Button tripsButton = findViewById(R.id.passenger_nearby_trips);
        tripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.passengerProfile == null) {
                    Toast.makeText(PassengerActivity.this,
                            "Please Create Your Passenger Profile First",
                            Toast.LENGTH_SHORT).show();
                } else if (distanceSelected == null) {
                    Toast.makeText(PassengerActivity.this,
                            "Please Select The Distance Of Where Your Trip Will Begin",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent tripsIntent = new Intent(getApplicationContext(),
                            PassengerNearbyTripsActivity.class);
                    tripsIntent.putExtra("USERNAME", username);
                    tripsIntent.putExtra("DISTANCE", distanceSelected);
                    startActivity(tripsIntent);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        distanceSelected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}