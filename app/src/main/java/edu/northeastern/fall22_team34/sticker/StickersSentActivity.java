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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.adapters.StickerSentAdapter;
import edu.northeastern.fall22_team34.sticker.models.Sticker;
import edu.northeastern.fall22_team34.sticker.models.User;

public class StickersSentActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String username;
    private Map<String, Integer> stickerSent;
    private List<Sticker> stickerList;

    private List<Integer> countList;

    private RecyclerView stickerSentRecyclerView;


    /* Start of onCreate */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers_sent);

        mDatabase = FirebaseDatabase.getInstance();

        username = getIntent().getStringExtra("USERNAME");
        stickerSent = (Map<String, Integer>) getIntent().getSerializableExtra("SENT");
        stickerList = (List<Sticker>) getIntent().getSerializableExtra("STICKERLIST");
        countList = getStickerCount(stickerSent, stickerList);

        stickerSentRecyclerView = findViewById(R.id.stickerSentRecyclerView);
        stickerSentRecyclerView.setHasFixedSize(true);
        stickerSentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (username.equals(dataSnapshot.getValue(User.class).username)) {
                        stickerSent = dataSnapshot.getValue(User.class).stickerSent;
                        stickerList = dataSnapshot.getValue(User.class).stickerList;

                        countList = getStickerCount(stickerSent, stickerList);
                        stickerSentRecyclerView.setAdapter(new StickerSentAdapter(stickerList, countList, getApplicationContext()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StickersSentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /* End of onCreate */


    // get how many have been sent for each sticker
    private List<Integer> getStickerCount(Map<String, Integer> stickerSent, List<Sticker> stickerList) {
        List<Integer> counts = new ArrayList<>();
        for (int i = 0; i < stickerList.size(); i++) {
            counts.add(stickerSent.get(stickerList.get(i).name));
        }
        return counts;
    }
}