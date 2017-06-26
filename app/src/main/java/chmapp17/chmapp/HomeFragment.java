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

import chmapp17.chmapp.database.UsersInfo;
import chmapp17.chmapp.login.email.LoginActivity;

public class HomeFragment extends Fragment {

    private View view;
    private Button emailSignIn, signout;
    private FirebaseAuth auth;
    private TextView user_name;

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //do view
            view = inflater.inflate(R.layout.fragment_home_signedin, container, false);
            user_name = (TextView) view.findViewById(R.id.user_name);
            FirebaseUser user_details = auth.getCurrentUser();
            UsersInfo ui = GetUserData(user_details.getUid());
            user_name.setText("Welcome to CHMap: " + ui.user_name + "\nYour email is: " + ui.user_email);

            signout = (Button) view.findViewById(R.id.sign_out_button);
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
            emailSignIn = (Button) view.findViewById(R.id.email_sign_in_button);


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

    public UsersInfo GetUserData(String Uid) {
        UsersInfo ui = new UsersInfo("UserInvalid", "UserInvalid", "UserInvalid");
        for (UsersInfo user : MainActivity.userList) {
            if (user.user_id.equals(Uid))
                ui = new UsersInfo(user.user_name, user.user_email, user.user_id);
        }

        return ui;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
