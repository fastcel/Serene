package com.example.serene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class JournalFavouritesFragment extends Fragment {

    RecyclerView recyclerView;
    TextView tvEmpty;

    JournalAdapter adapter;
    List<Journal> favoriteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_journal_favourites, container, false);

        recyclerView = view.findViewById(R.id.recyclerFavorites);
        tvEmpty = view.findViewById(R.id.tvEmptyFavorites);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavorites();

        return view;
    }

    private void loadFavorites() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("journals");

        ref.get().addOnSuccessListener(snapshot -> {

            favoriteList.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {

                Journal j = ds.getValue(Journal.class);

                if (j != null && j.isFavorite) {
                    j.id = ds.getKey();
                    favoriteList.add(j);
                }
            }

            if (favoriteList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);

                adapter = new JournalAdapter(favoriteList);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}