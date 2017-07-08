package chmapp17.chmapp.authentication.email;

import android.content.Context;
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

import chmapp17.chmapp.HomeFragment;
import chmapp17.chmapp.R;

public class EmailSignInFragment extends Fragment {

    private Context context;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_signin, container, false);
        context = getContext();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
            fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
        }

        final EditText inputEmail = (EditText) view.findViewById(R.id.email);
        final EditText inputPassword = (EditText) view.findViewById(R.id.password);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Button btnSignup = (Button) view.findViewById(R.id.btn_signup);
        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
        Button btnReset = (Button) view.findViewById(R.id.btn_reset_password);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.content, new EmailSignUpFragment(), "SignUp").commit();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.content, new ResetPasswordFragment(), "ResetPassword").commit();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(context, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(context, "Enter password!", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(context, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
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
