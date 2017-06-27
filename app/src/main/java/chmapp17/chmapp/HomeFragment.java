package chmapp17.chmapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import chmapp17.chmapp.login.email.LoginActivity;

public class HomeFragment extends Fragment {

    private View view;
    private FirebaseAuth auth;

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //do view
            view = inflater.inflate(R.layout.fragment_home_signedin, container, false);
            TextView user_name = (TextView) view.findViewById(R.id.user_name);
            FirebaseUser user = auth.getCurrentUser();
            user_name.setText("Welcome to CHMap: " + user.getDisplayName() +
                    "\nYour email is: " + user.getEmail());

            Button signout = (Button) view.findViewById(R.id.sign_out_button);
            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_home_unsigned, container, false);
            Button emailSignIn = (Button) view.findViewById(R.id.email_sign_in_button);


            emailSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new LoginActivity(), "LoginWithEmail").commit();
                }
            });

        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
