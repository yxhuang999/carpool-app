package edu.northeastern.fall22_team34.carpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.adapters.PassengerNearbyTripsAdapter;
import edu.northeastern.fall22_team34.carpool.models.Trip;
import edu.northeastern.fall22_team34.carpool.models.User;

public class PassengerNearbyTripsActivity extends AppCompatActivity implements OnJoinClicklistener {

    private FirebaseDatabase mDatabase;

    private User userRef;
    private String username;
    private String distanceSelected;

    private List<Trip> trips = new ArrayList<>();

    private RecyclerView tripsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_passenger_nearby_trips);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");
        distanceSelected = getIntent().getStringExtra("DISTANCE");

        tripsRecyclerView = findViewById(R.id.psg_avail_trips_rv);

        tripsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tripsRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                tripsRecyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.divider));
        tripsRecyclerView.addItemDecoration(dividerItemDecoration);

        mDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (username.equals(user.username)) {
                        userRef = user;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PassengerNearbyTripsActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mDatabase.getReference().child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Location tripStart = new Location("");
                    tripStart.setLatitude(trip.startLat);
                    tripStart.setLongitude(trip.startLong);

                    Location userLocation = new Location("");
                    userLocation.setLatitude(userRef.currLat);
                    userLocation.setLongitude(userRef.currLong);

                    double distance = userLocation.distanceTo(tripStart);
                    if (distance <= Integer.parseInt(distanceSelected)) {
                        Boolean check = false;
                        for (int i = 0; i < trips.size(); i++) {
                            if (trip.id == trips.get(i).id) {
                                trips.set(i, trip);
                                check = true;
                                break;
                            }
                        }
                        if (!check) {
                            trips.add(trip);
                        }
                    }
                }
                tripsRecyclerView.setAdapter(new PassengerNearbyTripsAdapter(trips,
                        getApplicationContext(), PassengerNearbyTripsActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PassengerNearbyTripsActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onJoinClick(int position) {
        Trip tripSelected = trips.get(position);
        mDatabase.getReference().child("trips").child(tripSelected.id)
                .runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Trip trip = currentData.getValue(Trip.class);

                if (trip != null) {
                    Boolean check = false;

                    if (trip.passenger == null) {
                        trip.passenger = new ArrayList<>();
                    } else {
                        for (int i = 0; i < trip.passenger.size(); i++) {
                            if (userRef.username.equals(trip.passenger.get(i).username)) {
                                check = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PassengerNearbyTripsActivity.this,
                                                "You Already Joined This Trip",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                        }
                    }
                    if (!check) {
                        trip.passenger.add(userRef);
                        currentData.setValue(trip);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PassengerNearbyTripsActivity.this,
                                        "Joined Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                if (!committed) {
                    Toast.makeText(PassengerNearbyTripsActivity.this,
                            "DBError: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}