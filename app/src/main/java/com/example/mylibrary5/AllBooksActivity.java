package com.example.mylibrary5;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Books");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // RecyclerView
        RecyclerView rv = findViewById(R.id.recyclerBooks);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Adapter - parent type = ALL BOOKS so delete is hidden
        BookRecViewAdapter adapter =
                new BookRecViewAdapter(this, BookRecViewAdapter.PARENT_ALL);
        rv.setAdapter(adapter);

        // Get data from Utils singleton
        Utils utils = Utils.getInstance(this);
        adapter.setBooks(utils.getAllBooks());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
