package chmapp17.chmapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddCrimeFragment extends Fragment {

    public AddCrimeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_crime, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        getView().setBackgroundColor(Color.YELLOW);
    }
}
