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
import edu.northeastern.fall22_team34.carpool.models.Driver;
import edu.northeastern.fall22_team34.carpool.models.User;

public class DriverProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String username;

    private String phoneNumber;
    private String licenseNumber;
    private String prefName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_driver_profile);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");

        EditText phone = findViewById(R.id.dr_et_phone);
        EditText license = findViewById(R.id.dr_et_license);
        EditText name = findViewById(R.id.dr_et_name);

        mDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (username.equals(dataSnapshot.getValue(User.class).username)) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.driverProfile != null) {
                            phone.setText(user.driverProfile.phoneNumber);
                            license.setText(user.driverProfile.license);
                            name.setText(user.driverProfile.prefName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverProfileActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phone.getText().toString();
                licenseNumber = license.getText().toString();
                prefName = name.getText().toString();

                if (phoneNumber.isEmpty() || licenseNumber.isEmpty() || prefName.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please Fill Up All Fields", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadProfile();
                        }
                    }).start();
                    Toast.makeText(DriverProfileActivity.this,
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
                            Driver driver = new Driver(phoneNumber, licenseNumber, prefName);
                            user.driverProfile = driver;

                            currentData.setValue(user);
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed,
                                           @Nullable DataSnapshot currentData) {
                        Toast.makeText(DriverProfileActivity.this,
                                "DBError: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}