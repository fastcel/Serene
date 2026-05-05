package com.example.serene;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MusicFragment extends Fragment {
    ListView listView;
    Button btnSelect;

    int[] tracks = {
            R.raw.rain,
            R.raw.ambient,
            R.raw.piano,
            R.raw.sad
    };

    String[] names = {
            "Rain Music",
            "Ambient Music",
            "Piano Music",
            "Sad Music"
    };
    int selectedTrack = -1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        listView = view.findViewById(R.id.musicList);
        btnSelect = view.findViewById(R.id.btnSelect);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        selectedTrack = MusicManager.getCurrentTrack();
        if (selectedTrack == -1) {
            selectedTrack = R.raw.rain;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_single_choice,
                names
        );
        listView.setAdapter(adapter);
        int selectedIndex = -1;
        for (int i = 0; i < tracks.length; i++) {
            if (tracks[i] == selectedTrack) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex != -1) {
            listView.setItemChecked(selectedIndex, true);
        }

        listView.setOnItemClickListener((parent, v, position, id) -> {
            selectedTrack = tracks[position];
            listView.setItemChecked(position, true);
        });

        btnSelect.setOnClickListener(v -> {
            if (selectedTrack != -1) {
                MusicManager.play(getContext(), selectedTrack);
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Select a track first", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}