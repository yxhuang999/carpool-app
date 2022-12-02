package edu.northeastern.fall22_team34;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.northeastern.fall22_team34.carpool.LoginActivity;
import edu.northeastern.fall22_team34.sticker.RegistrationActivity;
import edu.northeastern.fall22_team34.tvshows.activities.TVShowsActivity;

public class MainActivity extends AppCompatActivity {

    private Button AboutButton;

    private Button TVShowsButton;
    private Button StickerButton;

    private Button carpoolButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TVShowsButton = findViewById(R.id.EnterTVShowsButton);
        TVShowsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TVShowsActivity.class);
                startActivity(intent);
            }
        });

        StickerButton = findViewById(R.id.EnterStickerButton);
        StickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        AboutButton = findViewById(R.id.AboutButton);
        AboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        carpoolButton = findViewById(R.id.btn_carpool);
        carpoolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}