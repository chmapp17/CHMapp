package chmapp17.chmapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import chmapp17.chmapp.authentication.anonymous.AnonymousSignInFragment;
import chmapp17.chmapp.authentication.email.EmailSignInFragment;
import chmapp17.chmapp.authentication.gmail.GoogleSignInFragment;

public class HomeFragment extends Fragment {

    private Context context;
    private FirebaseAuth auth;
    private static Bitmap bitmap;
    public static GoogleApiClient googleApiClient;
    public static GoogleSignInOptions googleSignInOptions;

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        context = getContext();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            view = inflater.inflate(R.layout.fragment_home_auth, container, false);

            TextView user_details = (TextView) view.findViewById(R.id.user_details);
            FirebaseUser user = auth.getCurrentUser();
            if (user.getEmail() == null) {
                user_details.setText("Welcome to CHMap: You are logged in as anonymous");
            } else {
                user_details.setText("Welcome to CHMap: " + user.getDisplayName() +
                        "\nYour email is: " + user.getEmail() + "\nProvider: " + user.getProviders());
            }

            Button signout = (Button) view.findViewById(R.id.sign_out_button);
            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    if (googleApiClient != null) {
                        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                                        fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
                                    }
                                });
                    } else {
                        final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                        fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
                    }
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_home, container, false);

            if (bitmap == null) {
                DisplayMetrics size = context.getResources().getDisplayMetrics();
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.home_bg_image), size.widthPixels, size.heightPixels, true);

            }

            ImageView bg_image = (ImageView) view.findViewById(R.id.bg_image);
            bg_image.setImageBitmap(bitmap);

            Button emailSignIn = (Button) view.findViewById(R.id.email_sign_in_button);
            SignInButton gmailSignIn = (SignInButton) view.findViewById(R.id.gmail_sign_in_button);
            Button anonymousSignIn = (Button) view.findViewById(R.id.anoymous_sign_in_button);

            emailSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new EmailSignInFragment(), "EmailSignIn").commit();
                }
            });

            gmailSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new GoogleSignInFragment(), "GoogleSignIn").commit();
                }
            });

            anonymousSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new AnonymousSignInFragment(), "AnonymousSignIn").commit();
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
