package com.example.mylibrary5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnAllBooks, btnCurrentlyReading, btnAlreadyRead,
            btnWantToRead, btnFavorite, btnAbout;
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Library");
        }

        Utils.getInstance(this);

        initViews();
        wireClicks();
    }

    private void initViews() {
        imgLogo            = findViewById(R.id.imgLogo);
        btnAllBooks        = findViewById(R.id.btnAllBooks);
        btnCurrentlyReading= findViewById(R.id.btnCurrentlyReading);
        btnAlreadyRead     = findViewById(R.id.btnAlreadyRead);
        btnWantToRead      = findViewById(R.id.btnWantToRead);
        btnFavorite        = findViewById(R.id.btnFavorite);
        btnAbout           = findViewById(R.id.btnAbout);
    }

    private void wireClicks() {
        btnAllBooks.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AllBooksActivity.class)));

        btnCurrentlyReading.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CurrentlyReadingActivity.class)));

        btnAlreadyRead.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AlreadyReadActivity.class)));

        btnWantToRead.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WantToReadActivity.class)));

        btnFavorite.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class)));

        // ABOUT – WebView with a little spin animation on click
        btnAbout.setOnClickListener(v -> {
            v.animate().rotationBy(360f).setDuration(300).start();
            startActivity(new Intent(MainActivity.this, WebsiteActivity.class));
        });

        imgLogo.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "MyLibrary3 ❤️", Toast.LENGTH_SHORT).show());
    }
}
