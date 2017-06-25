package chmapp17.chmapp.login.email;

/**
 * Created by Edward on 6/23/2017.
 */
import chmapp17.chmapp.HomeFragment;
import chmapp17.chmapp.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import chmapp17.chmapp.R;
import chmapp17.chmapp.database.DataBaseHandling;
import chmapp17.chmapp.database.UsersInfo;

public class SignupActivity extends Fragment {

    private EditText inputEmail, inputPassword,inputUserName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private View view;
    private Context sContext;
    private DataBaseHandling dbHandling;
    private String user_name,email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sContext = getContext();
        view = inflater.inflate(R.layout.activity_signup, container, false);
        //view.inflater.inflate(R.layout.activity_signup, container, false);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        dbHandling = new DataBaseHandling();

        btnSignIn = (Button) view.findViewById(R.id.sign_in_button);
        btnSignUp = (Button) view.findViewById(R.id.sign_up_button);
        inputUserName = (EditText) view.findViewById(R.id.user);
        inputEmail = (EditText) view.findViewById(R.id.email);
        inputPassword = (EditText) view.findViewById(R.id.password);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btnResetPassword = (Button) view.findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.content, new ResetPasswordActivity(), "ResetPassword").commit();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.content, new LoginActivity(), "Login").commit();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_name = inputUserName.getText().toString().trim();
                email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(user_name)) {
                    Toast.makeText(sContext, "Enter user name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(sContext, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(sContext, "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(sContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(sContext, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(sContext, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    UsersInfo user_info = new UsersInfo(user_name,email,auth.getCurrentUser().getUid());
                                    dbHandling.addUser(user_info);

                                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                                    fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
                                }
                            }
                        });

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}