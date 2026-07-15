package com.example.mylibrary5;

import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BookRecViewAdapter extends RecyclerView.Adapter<BookRecViewAdapter.BookVH> {

    // Parent types so we know which list we're showing
    public static final int PARENT_ALL            = 0;
    public static final int PARENT_ALREADY_READ   = 1;
    public static final int PARENT_WANT_TO_READ   = 2;
    public static final int PARENT_CURRENTLY_READ = 3;
    public static final int PARENT_FAVORITES      = 4;

    private final Context context;
    private final List<Book> books = new ArrayList<>();
    private final SparseBooleanArray expandState = new SparseBooleanArray();
    private final int parentType;

    public BookRecViewAdapter(Context context, int parentType) {
        this.context = context;
        this.parentType = parentType;
    }

    public void setBooks(List<Book> newBooks) {
        books.clear();
        expandState.clear();
        if (newBooks != null) {
            books.addAll(newBooks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH h, int position) {
        Book b = books.get(position);

        h.tvTitle.setText(b.getName());
        h.tvMeta.setText(b.getAuthor() + " • " + b.getPages() + " pages");
        h.tvDesc.setText(b.getShortDesc());
        h.tvLong.setText(b.getLongDesc());

        Glide.with(context)
                .load(b.getImageUrl())
                .into(h.ivCover);

        // Expand / collapse state
        boolean expanded = expandState.get(position, false);
        h.tvLong.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.ivExpand.setRotation(expanded ? 180f : 0f);

        // Hide delete button on "All Books", show it elsewhere
        if (parentType == PARENT_ALL) {
            h.btnDelete.setVisibility(View.GONE);
        } else {
            h.btnDelete.setVisibility(View.VISIBLE);
        }

        // Click whole item -> open BookActivity
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookActivity.class);
            intent.putExtra(BookActivity.EXTRA_ID, b.getId());
            intent.putExtra(BookActivity.EXTRA_PARENT, parentType);
            context.startActivity(intent);
        });

        // Expand / collapse arrow
        h.ivExpand.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            boolean newState = !expandState.get(pos, false);
            expandState.put(pos, newState);

            TransitionManager.beginDelayedTransition(
                    (ViewGroup) h.itemView, new AutoTransition());

            h.tvLong.setVisibility(newState ? View.VISIBLE : View.GONE);
            h.ivExpand.animate()
                    .rotation(newState ? 180f : 0f)
                    .setDuration(150)
                    .start();
        });

        // Delete button (only visible on list screens)
        h.btnDelete.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Book bookToRemove = books.get(pos);
            boolean removed = false;
            Utils utils = Utils.getInstance(context);

            switch (parentType) {
                case PARENT_ALREADY_READ:
                    removed = utils.removeFromAlreadyRead(bookToRemove);
                    break;
                case PARENT_WANT_TO_READ:
                    removed = utils.removeFromWantToRead(bookToRemove);
                    break;
                case PARENT_CURRENTLY_READ:
                    removed = utils.removeFromCurrentlyReading(bookToRemove);
                    break;
                case PARENT_FAVORITES:
                    removed = utils.removeFromFavorites(bookToRemove);
                    break;
            }

            if (removed) {
                books.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, books.size() - pos);
                Toast.makeText(context, "Book removed from list", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Could not remove book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookVH extends RecyclerView.ViewHolder {

        final TextView tvTitle, tvMeta, tvDesc, tvLong;
        final ImageView ivCover, ivExpand;
        final MaterialButton btnDelete;

        BookVH(@NonNull View itemView) {
            super(itemView);
            tvTitle  = itemView.findViewById(R.id.tvTitle);
            tvMeta   = itemView.findViewById(R.id.tvMeta);
            tvDesc   = itemView.findViewById(R.id.tvDesc);
            tvLong   = itemView.findViewById(R.id.tvLong);
            ivCover  = itemView.findViewById(R.id.ivCover);
            ivExpand = itemView.findViewById(R.id.ivExpand);
            btnDelete= itemView.findViewById(R.id.btnDelete);
        }
    }
}
