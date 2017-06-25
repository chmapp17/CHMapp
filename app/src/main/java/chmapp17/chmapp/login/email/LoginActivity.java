package chmapp17.chmapp.login.email;

/**
 * Created by Edward on 6/24/2017.
 */
import chmapp17.chmapp.HomeFragment;
import chmapp17.chmapp.MainActivity;
import chmapp17.chmapp.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Fragment {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private Context lContext;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_login, container, false);
        lContext = getContext();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        //to delete this after test

        if (auth.getCurrentUser() != null) {
            final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
            fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
        }

        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        inputEmail = (EditText) view.findViewById(R.id.email);
        inputPassword = (EditText) view.findViewById(R.id.password);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btnSignup = (Button) view.findViewById(R.id.btn_signup);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnReset = (Button) view.findViewById(R.id.btn_reset_password);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.content, new SignupActivity(), "SignUp").commit();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.content, new ResetPasswordActivity(), "ResetPassword").commit();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(lContext, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(lContext, "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(lContext, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                                    fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
                                }
                            }
                        });
            }
        });
    return view;
    }
}
