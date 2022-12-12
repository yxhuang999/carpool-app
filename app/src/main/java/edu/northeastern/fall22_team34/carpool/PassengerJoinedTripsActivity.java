package edu.northeastern.fall22_team34.carpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.adapters.DriverTripAdapter;
import edu.northeastern.fall22_team34.carpool.models.Trip;
import edu.northeastern.fall22_team34.carpool.models.User;

public class PassengerJoinedTripsActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String username;

    private List<Trip> trips = new ArrayList<>();

    private RecyclerView tripsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_passenger_joined_trips);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");

        tripsRecyclerView = findViewById(R.id.psg_joined_trips_rv);

        tripsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tripsRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                tripsRecyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.divider));
        tripsRecyclerView.addItemDecoration(dividerItemDecoration);

        mDatabase.getReference().child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Trip trip = dataSnapshot.getValue(Trip.class);

                    if (trip.passenger != null && trip.passenger.size() > 0) {
                        for (int i = 0; i < trip.passenger.size(); i++) {
                            User user = trip.passenger.get(i);
                            if (username.equals(user.username)) {
                                trips.add(trip);
                                break;
                            }
                        }
                    }

                }
                tripsRecyclerView.setAdapter(new DriverTripAdapter(trips, getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PassengerJoinedTripsActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}