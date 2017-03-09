package chmapp17.chmapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewCrimesFragment extends Fragment {

    public ViewCrimesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_crimes, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        getView().setBackgroundColor(Color.MAGENTA);
    }
}
