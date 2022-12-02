package edu.northeastern.fall22_team34.carpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.models.User;

public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private List<String> currUsernames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance();

        currUsernames = new ArrayList<>();

        EditText usernameEditText = findViewById(R.id.et_username);
        Button loginButton = findViewById(R.id.btn_login);

        mDatabase.getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                currUsernames.add(user.username);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                currUsernames.remove(user.username);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username Cannot be Empty",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String username = usernameEditText.getText().toString();
                    if (!currUsernames.contains(username)) {
                        createUser(username);
                    }

                    Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    homeActivity.putExtra("USERNAME", username);
                    homeActivity.putExtra("CURR_USERNAMES", (Serializable) currUsernames);
                    startActivity(homeActivity);
                }
            }
        });
    }

    private void createUser(String username) {
        User user = new User(username);
        mDatabase.getReference().child("users").child(username).setValue(user);
    }
}