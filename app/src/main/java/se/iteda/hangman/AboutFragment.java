package se.iteda.hangman;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar mToolbar = view.findViewById(R.id.toolbarID);
        ((MainActivity)getActivity()).setSupportActionBar(mToolbar);

        mToolbar.setTitle("About");

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.setGroupVisible(R.id.playButtonGroupID, false);
            menu.setGroupVisible(R.id.aboutButtonGroupID, false);
        }
    }

}
