package edu.northeastern.fall22_team34.carpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.models.User;
import edu.northeastern.fall22_team34.carpool.models.Vehicle;

public class VehicleProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String username;

    private String plate;
    private String color;
    private int seat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_vehicle_profile);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");

        EditText plateET = findViewById(R.id.vh_et_plate);
        EditText colorET = findViewById(R.id.vh_et_color);
        EditText seatET = findViewById(R.id.vh_et_seat);

        mDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (username.equals(dataSnapshot.getValue(User.class).username)) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.driverProfile != null && user.vehicleProfile != null) {
                            plateET.setText(user.vehicleProfile.plate);
                            colorET.setText(user.vehicleProfile.color);
                            seatET.setText(user.vehicleProfile.seat + "");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VehicleProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Button saveButton = findViewById(R.id.vh_btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plate = plateET.getText().toString();
                color = colorET.getText().toString();
                seat = Integer.parseInt(seatET.getText().toString());

                if (plate.isEmpty() || color.isEmpty() || seatET.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please Fill Up All Fields", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadProfile();
                        }
                    }).start();
                    Toast.makeText(VehicleProfileActivity.this,
                            "Your Profile is Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadProfile() {
        mDatabase.getReference().child("users").child(username)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        User user = currentData.getValue(User.class);
                        if (user != null) {
                            Vehicle vehicle = new Vehicle(plate, color, seat);
                            user.vehicleProfile = vehicle;

                            currentData.setValue(user);
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed,
                                           @Nullable DataSnapshot currentData) {
                        if (!committed) {
                            Toast.makeText(VehicleProfileActivity.this,
                                    "DBError: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}