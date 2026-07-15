package com.example.mylibrary5;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static Utils instance;

    // SharedPreferences constants
    private static final String PREFS_NAME       = "lib_app_prefs";
    private static final String KEY_ALL          = "key_all_books";
    private static final String KEY_ALREADY      = "key_already_read";
    private static final String KEY_WANT         = "key_want_to_read";
    private static final String KEY_CURRENTLY    = "key_currently_reading";
    private static final String KEY_FAVORITES    = "key_favorites";

    private final ArrayList<Book> allBooks         = new ArrayList<>();
    private final ArrayList<Book> alreadyRead      = new ArrayList<>();
    private final ArrayList<Book> wantToRead       = new ArrayList<>();
    private final ArrayList<Book> currentlyReading = new ArrayList<>();
    private final ArrayList<Book> favorites        = new ArrayList<>();

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    // Singleton constructor
    private Utils(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 1) Load anything previously saved
        loadData();

        // 2) If first run (no books yet), seed and save
        if (allBooks.isEmpty()) {
            seedInitialBooks();
            saveData();
        }
    }

    // Singleton accessor
    public static Utils getInstance(Context ctx) {
        if (instance == null) {
            instance = new Utils(ctx.getApplicationContext());
        }
        return instance;
    }

    // ------------------- Persistence helpers -------------------

    private Type listType() {
        return new TypeToken<ArrayList<Book>>() {}.getType();
    }

    private void loadData() {
        Type type = listType();

        String jsonAll   = prefs.getString(KEY_ALL, null);
        String jsonAlrd  = prefs.getString(KEY_ALREADY, null);
        String jsonWant  = prefs.getString(KEY_WANT, null);
        String jsonCurr  = prefs.getString(KEY_CURRENTLY, null);
        String jsonFav   = prefs.getString(KEY_FAVORITES, null);

        allBooks.clear();
        alreadyRead.clear();
        wantToRead.clear();
        currentlyReading.clear();
        favorites.clear();

        if (jsonAll != null) {
            allBooks.addAll(gson.fromJson(jsonAll, type));
        }
        if (jsonAlrd != null) {
            alreadyRead.addAll(gson.fromJson(jsonAlrd, type));
        }
        if (jsonWant != null) {
            wantToRead.addAll(gson.fromJson(jsonWant, type));
        }
        if (jsonCurr != null) {
            currentlyReading.addAll(gson.fromJson(jsonCurr, type));
        }
        if (jsonFav != null) {
            favorites.addAll(gson.fromJson(jsonFav, type));
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KEY_ALL,       gson.toJson(allBooks));
        editor.putString(KEY_ALREADY,   gson.toJson(alreadyRead));
        editor.putString(KEY_WANT,      gson.toJson(wantToRead));
        editor.putString(KEY_CURRENTLY, gson.toJson(currentlyReading));
        editor.putString(KEY_FAVORITES, gson.toJson(favorites));

        editor.apply();
    }

    // ------------------- Initial seed -------------------

    private void seedInitialBooks() {
        // Only called when allBooks was empty after loadData()
        allBooks.add(new Book(
                1, "Clean Code", "Robert C. Martin", 464,
                "https://covers.openlibrary.org/b/isbn/0132350882-L.jpg",
                "Learn how to write readable, maintainable, and professional code.",
                "Clean Code explains how to transform messy code into clean, organized, and maintainable code. " +
                        "It introduces principles such as meaningful naming, small functions, proper class design, " +
                        "and test-driven development, encouraging developers to treat programming as a craft."
        ));

        allBooks.add(new Book(
                2, "Effective Java", "Joshua Bloch", 416,
                "https://covers.openlibrary.org/b/isbn/0134685997-L.jpg",
                "Practical best practices for writing reliable and efficient Java code.",
                "Effective Java is organized into short items that each present a best practice for designing " +
                        "and implementing Java code. It covers object creation, generics, enums, lambdas, " +
                        "exceptions, and performance, helping developers write safer and more expressive programs."
        ));

        allBooks.add(new Book(
                3, "Android Programming", "Big Nerd Ranch", 720,
                "https://covers.openlibrary.org/b/isbn/0134706056-L.jpg",
                "Learn Android development by building real apps step-by-step.",
                "This book takes a project-based approach to Android development. You build real applications " +
                        "while learning activities, fragments, RecyclerView, networking, data storage, and " +
                        "Material Design. By the end, you’re comfortable structuring and shipping Android apps."
        ));
    }

    // ------------------- Query helpers -------------------

    public Book getBookById(int id) {
        for (Book b : allBooks)         if (b.getId() == id) return b;
        for (Book b : alreadyRead)      if (b.getId() == id) return b;
        for (Book b : wantToRead)       if (b.getId() == id) return b;
        for (Book b : currentlyReading) if (b.getId() == id) return b;
        for (Book b : favorites)        if (b.getId() == id) return b;
        return null;
    }

    public List<Book> getAllBooks()         { return allBooks; }
    public List<Book> getAlreadyRead()      { return alreadyRead; }
    public List<Book> getWantToRead()       { return wantToRead; }
    public List<Book> getCurrentlyReading() { return currentlyReading; }
    public List<Book> getFavorites()        { return favorites; }

    // ------------------- Add / Remove (auto-save) -------------------

    public boolean addToAlreadyRead(Book b) {
        boolean added = addNoDup(alreadyRead, b);
        if (added) saveData();
        return added;
    }

    public boolean addToWantToRead(Book b) {
        boolean added = addNoDup(wantToRead, b);
        if (added) saveData();
        return added;
    }

    public boolean addToCurrentlyReading(Book b) {
        boolean added = addNoDup(currentlyReading, b);
        if (added) saveData();
        return added;
    }

    public boolean addToFavorites(Book b) {
        boolean added = addNoDup(favorites, b);
        if (added) saveData();
        return added;
    }

    public boolean removeFromAlreadyRead(Book b) {
        boolean removed = removeById(alreadyRead, b);
        if (removed) saveData();
        return removed;
    }

    public boolean removeFromWantToRead(Book b) {
        boolean removed = removeById(wantToRead, b);
        if (removed) saveData();
        return removed;
    }

    public boolean removeFromCurrentlyReading(Book b) {
        boolean removed = removeById(currentlyReading, b);
        if (removed) saveData();
        return removed;
    }

    public boolean removeFromFavorites(Book b) {
        boolean removed = removeById(favorites, b);
        if (removed) saveData();
        return removed;
    }

    // ------------------- Internal list helpers -------------------

    private boolean addNoDup(ArrayList<Book> list, Book b) {
        if (b == null) return false;
        for (Book x : list) {
            if (x.getId() == b.getId()) {
                return false;  // already there, don’t add again
            }
        }
        list.add(b);
        return true;
    }

    private boolean removeById(ArrayList<Book> list, Book b) {
        if (b == null) return false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == b.getId()) {
                list.remove(i);
                return true;
            }
        }
        return false;
    }
}
