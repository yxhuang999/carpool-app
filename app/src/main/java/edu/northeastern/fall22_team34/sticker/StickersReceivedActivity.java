package edu.northeastern.fall22_team34.sticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.adapters.StickerAdapter;
import edu.northeastern.fall22_team34.sticker.models.Sticker;
import edu.northeastern.fall22_team34.sticker.models.User;

public class StickersReceivedActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String username;
    private List<Sticker> stickerReceived;

    private RecyclerView stickerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers_received);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");
        stickerReceived = (List<Sticker>) getIntent().getSerializableExtra("RECEIVED");

        stickerRecyclerView = findViewById(R.id.stickerRecyclerView);
        stickerRecyclerView.setHasFixedSize(true);
        stickerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (username.equals(dataSnapshot.getValue(User.class).username)) {
                        stickerReceived = dataSnapshot.getValue(User.class).stickerReceived;

                        stickerRecyclerView.setAdapter(new StickerAdapter(stickerReceived, getApplicationContext()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.getReference().child("users").child(username).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);

                if (user == null) {
                    return Transaction.success(currentData);
                }
                stickerReceived = user.stickerReceived;
                stickerRecyclerView.setAdapter(new StickerAdapter(stickerReceived, getApplicationContext()));
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }
}