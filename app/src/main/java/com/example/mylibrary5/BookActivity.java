package com.example.mylibrary5;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BookActivity extends AppCompatActivity {

    public static final String EXTRA_ID     = "extra_id";
    public static final String EXTRA_PARENT = "extra_parent";

    private Book activeBook;
    private int parentType = BookRecViewAdapter.PARENT_ALL;

    // Views
    private ImageView ivCoverBig;
    private TextView tvTitle, tvMeta, tvLong;
    private MaterialButton btnCurrentlyReading, btnWantToRead,
            btnAlreadyRead, btnAddToFavorites, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Action bar title + up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Book Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Find views
        ivCoverBig        = findViewById(R.id.ivCoverBig);
        tvTitle           = findViewById(R.id.tvTitle);
        tvMeta            = findViewById(R.id.tvMeta);
        tvLong            = findViewById(R.id.tvLong);

        btnCurrentlyReading = findViewById(R.id.btnCurrentlyReading);
        btnWantToRead       = findViewById(R.id.btnWantToRead);
        btnAlreadyRead      = findViewById(R.id.btnAlreadyRead);
        btnAddToFavorites   = findViewById(R.id.btnAddToFavorites);
        btnDelete           = findViewById(R.id.btnDelete);

        // Get data from Intent
        Intent intent = getIntent();
        int bookId = intent.getIntExtra(EXTRA_ID, -1);
        parentType = intent.getIntExtra(EXTRA_PARENT, BookRecViewAdapter.PARENT_ALL);

        Utils utils = Utils.getInstance(this);

        if (bookId == -1) {
            Toast.makeText(this, "No book id passed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        activeBook = utils.getBookById(bookId);
        if (activeBook == null) {
            Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fill UI
        bindBookToViews();

        // Set up button visibility + click listeners
        setupButtons(utils);

        // Disable buttons for lists the book is already in
        refreshButtonStates(utils);
    }

    // ---------------- UI BINDING ----------------

    private void bindBookToViews() {
        // Title
        safeSetText(tvTitle, activeBook.getName());

        // "Author • 464 pages"
        String meta = activeBook.getAuthor() + " • " + activeBook.getPages() + " pages";
        safeSetText(tvMeta, meta);

        // LONG description
        safeSetText(tvLong, activeBook.getLongDesc());

        // Big cover
        Glide.with(this)
                .load(activeBook.getImageUrl())
                .into(ivCoverBig);
    }

    // ---------------- BUTTON LOGIC ----------------

    private void setupButtons(Utils utils) {

        // Delete button is only for list screens (not All Books)
        if (parentType == BookRecViewAdapter.PARENT_ALL) {
            btnDelete.setEnabled(false);
            btnDelete.setAlpha(0f);         // hide visually
        } else {
            btnDelete.setEnabled(true);
            btnDelete.setAlpha(1f);
        }

        // Add to Currently Reading
        btnCurrentlyReading.setOnClickListener(v -> {
            boolean added = utils.addToCurrentlyReading(activeBook);
            Toast.makeText(this,
                    added ? "Added to Currently Reading"
                            : "Already in Currently Reading",
                    Toast.LENGTH_SHORT).show();
            refreshButtonStates(utils);
        });

        // Add to Want To Read
        btnWantToRead.setOnClickListener(v -> {
            boolean added = utils.addToWantToRead(activeBook);
            Toast.makeText(this,
                    added ? "Added to Want To Read"
                            : "Already in Want To Read",
                    Toast.LENGTH_SHORT).show();
            refreshButtonStates(utils);
        });

        // Add to Already Read
        btnAlreadyRead.setOnClickListener(v -> {
            boolean added = utils.addToAlreadyRead(activeBook);
            Toast.makeText(this,
                    added ? "Marked as Already Read"
                            : "Already in Already Read",
                    Toast.LENGTH_SHORT).show();
            refreshButtonStates(utils);
        });

        // Add to Favorites
        btnAddToFavorites.setOnClickListener(v -> {
            boolean added = utils.addToFavorites(activeBook);
            Toast.makeText(this,
                    added ? "Added to Favorites"
                            : "Already in Favorites",
                    Toast.LENGTH_SHORT).show();
            refreshButtonStates(utils);
        });

        // Delete from current list (if not All Books)
        btnDelete.setOnClickListener(v -> {
            if (parentType == BookRecViewAdapter.PARENT_ALL) return;

            boolean removed = false;
            switch (parentType) {
                case BookRecViewAdapter.PARENT_ALREADY_READ:
                    removed = utils.removeFromAlreadyRead(activeBook);
                    break;
                case BookRecViewAdapter.PARENT_WANT_TO_READ:
                    removed = utils.removeFromWantToRead(activeBook);
                    break;
                case BookRecViewAdapter.PARENT_CURRENTLY_READ:
                    removed = utils.removeFromCurrentlyReading(activeBook);
                    break;
                case BookRecViewAdapter.PARENT_FAVORITES:
                    removed = utils.removeFromFavorites(activeBook);
                    break;
            }

            Toast.makeText(this,
                    removed ? "Book removed from this list"
                            : "Could not remove book",
                    Toast.LENGTH_SHORT).show();

            if (removed) {
                // go back to previous list screen
                finish();
            }
        });
    }

    /**
     * Disable any button for lists that already contain this book.
     * Also dims them visually with alpha.
     */
    private void refreshButtonStates(Utils utils) {
        setButtonEnabledForList(btnAlreadyRead,      utils.getAlreadyRead());
        setButtonEnabledForList(btnWantToRead,       utils.getWantToRead());
        setButtonEnabledForList(btnCurrentlyReading, utils.getCurrentlyReading());
        setButtonEnabledForList(btnAddToFavorites,   utils.getFavorites());
    }

    private void setButtonEnabledForList(MaterialButton btn, List<Book> list) {
        if (btn == null || activeBook == null) return;

        boolean inList = isInList(list, activeBook);
        btn.setEnabled(!inList);
        btn.setAlpha(inList ? 0.4f : 1f);   // 0.4f looks disabled but still visible
    }

    private boolean isInList(List<Book> list, Book b) {
        if (list == null || b == null) return false;
        for (Book x : list) {
            if (x.getId() == b.getId()) return true;
        }
        return false;
    }

    // ---------------- HELPERS ----------------

    private void safeSetText(TextView tv, String txt) {
        if (tv != null) {
            tv.setText(txt != null ? txt : "");
        }
    }

    // Up-button in action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
