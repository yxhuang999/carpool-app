package edu.northeastern.fall22_team34.sticker;

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
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.models.User;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    protected String REGISTRATION_TOKEN;
    public List<String> validUsernames = new ArrayList<>();

    private EditText mEditText;
    private String username;
    private Button mLoginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers_registration);

        mDatabase = FirebaseDatabase.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        REGISTRATION_TOKEN = task.getResult();
                    }
                });

        mEditText = findViewById(R.id.username_text);
        mLoginBtn = findViewById(R.id.loginUserBtn);

        mDatabase.getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                validUsernames.add(user.username);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                validUsernames.remove(user.username);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent sendStickerActivity = new Intent(getApplicationContext(), SendStickerActivity.class);
        sendStickerActivity.putExtra("REGISTRATION_TOKEN", REGISTRATION_TOKEN);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username Cannot be Empty",
                            Toast.LENGTH_SHORT).show();
                } else {
                    username = mEditText.getText().toString();
                    if (!validUsernames.contains(username)) {
                        validUsernames.add(username);
                        createUser(username, REGISTRATION_TOKEN);
                    }
                    sendStickerActivity.putExtra("USERNAME", username);
                    sendStickerActivity.putExtra("VALID_USERNAMES", (Serializable) validUsernames);
                    startActivity(sendStickerActivity);
                }
            }
        });
    }

    private void createUser(String username, String token) {
        User user = new User(username, token);
        mDatabase.getReference().child("users").child(username).setValue(user);
    }
}