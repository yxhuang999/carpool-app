package edu.northeastern.fall22_team34.sticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Map;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.adapters.StickerReceivedAdapter;
import edu.northeastern.fall22_team34.sticker.adapters.StickerSentAdapter;
import edu.northeastern.fall22_team34.sticker.models.Sticker;
import edu.northeastern.fall22_team34.sticker.models.User;

public class StickersSentActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String username;
    private Map<String, Integer> stickerSent;
    private List<Sticker> stickerList;

    private RecyclerView stickerSentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers_sent);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");
        stickerSent = (Map<String, Integer>) getIntent().getSerializableExtra("SENT");
        stickerList = (List<Sticker>) getIntent().getSerializableExtra("STICKERLIST");

        stickerSentRecyclerView = findViewById(R.id.stickerSentRecyclerView);
        stickerSentRecyclerView.setHasFixedSize(true);
        stickerSentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (username.equals(dataSnapshot.getValue(User.class).username)) {
                        stickerSent = dataSnapshot.getValue(User.class).stickerSent;

                        stickerSentRecyclerView.setAdapter(new StickerSentAdapter(stickerList, new ArrayList<>(stickerSent.values()), getApplicationContext()));
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
                stickerSent = user.stickerSent;

                stickerSentRecyclerView.setAdapter(new StickerSentAdapter(stickerList, new ArrayList<>(stickerSent.values()), getApplicationContext()));
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }
}