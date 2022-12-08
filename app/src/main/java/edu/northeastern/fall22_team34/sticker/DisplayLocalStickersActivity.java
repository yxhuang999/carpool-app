package edu.northeastern.fall22_team34.sticker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.adapters.LocalStickersAdapter;

public class DisplayLocalStickersActivity extends AppCompatActivity implements LocalStickerOnClickListener {

    private RecyclerView localStickerRecyclerView;

    private List<Integer> localStickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers_display_local_stickers);

        localStickerRecyclerView = findViewById(R.id.localStickerRecyclerView);
        localStickerRecyclerView.setHasFixedSize(true);
        localStickerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        localStickers = new ArrayList<>();


        Field[] fields = R.drawable.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            localStickers.add(getResources().getIdentifier("sticker" + (i + 1),
                    "drawable", getPackageName()));
        }

        localStickerRecyclerView.setAdapter(new LocalStickersAdapter(localStickers,
                getApplicationContext(), DisplayLocalStickersActivity.this));
    }

    @Override
    public void onStickerClick(int position) {
        Intent intent = new Intent();
        Uri uri = Uri.parse("android.resource://edu.northeastern.fall22_team34/drawable/" +
                getResources().getResourceEntryName(localStickers.get(position)));
        intent.putExtra("NAME", getResources().getResourceEntryName(localStickers.get(position)));
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}