package edu.northeastern.fall22_team34.carpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.models.Ride;
import edu.northeastern.fall22_team34.carpool.models.User;

public class NewRideActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private String username;
    private Location userLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private GeoApiContext mGeoApiContext = null;
    private Geocoder mGeocoder;
    private List<Address> addresses;
    private String currAddress;
    private Address destAddress;
    private String rideDuration;

    private EditText from;
    private EditText destination;
    private TextView duration;
    private EditText time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_new_ride);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");

        from = findViewById(R.id.new_ride_et_from);
        destination = findViewById(R.id.new_ride_et_dest);
        duration = findViewById(R.id.new_ride_et_duration);
        time = findViewById(R.id.new_ride_et_time);

        durationInit();

        locationInit();

        mDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (username.equals(dataSnapshot.getValue(User.class).username)) {
                        User user = dataSnapshot.getValue(User.class);
                        try {
                            addresses = mGeocoder.getFromLocation(user.currLat,
                                    user.currLong, 1);
                            currAddress = addresses.get(0).getAddressLine(0);
                            from.setText(currAddress);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewRideActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        onDestinationEntered();

        onRideSubmit();
    }

    private void durationInit() {
        // use to get directions information
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyDuuVWibsm9I87nGciMh-WYj5v8QIBzIyQ")
                    .build();
        }

        mGeocoder = new Geocoder(this, Locale.getDefault());
    }

    private void calculateDirections(double latitude, double longitude) {
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                latitude, longitude);
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        userLocation.getLatitude(),
                        userLocation.getLongitude()
                )
        );
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                rideDuration = result.routes[0].legs[0].duration.humanReadable;

                runOnUiThread(new Runnable() {
                    public void run() {
                        duration.setText(rideDuration);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });
    }

    // set up location update services
    private void locationInit() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                mDatabase.getReference().child("users").child(username)
                        .runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                User user = currentData.getValue(User.class);

                                if (user != null) {
                                    user.currLat = location.getLatitude();
                                    user.currLong = location.getLongitude();

                                    if (userLocation == null) {
                                        userLocation = new Location("");
                                    }
                                    userLocation.setLatitude(location.getLatitude());
                                    userLocation.setLongitude(location.getLongitude());

                                    currentData.setValue(user);
                                }
                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                                   @Nullable DataSnapshot currentData) {
                                if (!committed) {
                                    Toast.makeText(NewRideActivity.this,
                                            "DBError: " + error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, locationListener);
            }
        }
    }

    private void onDestinationEntered() {
        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && destination.getText().toString().length() > 5) {
                    try {
                        List<Address> dest = mGeocoder
                                .getFromLocationName(destination.getText().toString(), 5);
                        destAddress = dest.get(0);
                        destination.setText(destAddress.getAddressLine(0));
                        calculateDirections(destAddress.getLatitude(), destAddress.getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void onRideSubmit() {
        Button rideSubmit = findViewById(R.id.btn_submit_ride);
        rideSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destination.getText().toString().isEmpty()
                        || time.getText().toString().isEmpty()) {
                    Toast.makeText(NewRideActivity.this,
                            "Please Provide All Necessary Information For This Ride",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.getReference().child("users").child(username)
                            .runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            User user = currentData.getValue(User.class);

                            String uniqueID = UUID.randomUUID().toString();
                            Ride ride = new Ride(uniqueID, user, userLocation.getLatitude(),
                                    userLocation.getLongitude(), destAddress.getLatitude(),
                                    destAddress.getLongitude(), rideDuration, time.getText().toString());

                            mDatabase.getReference().child("rides").child(uniqueID).setValue(ride);

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error,
                                               boolean committed, @Nullable DataSnapshot currentData) {
                            if (!committed) {
                                Toast.makeText(NewRideActivity.this,
                                        "DBError: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}