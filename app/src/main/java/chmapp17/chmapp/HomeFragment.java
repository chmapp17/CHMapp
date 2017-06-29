package chmapp17.chmapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import chmapp17.chmapp.login.anonymous.AnonymousAuthActivity;
import chmapp17.chmapp.login.email.LoginActivity;
import chmapp17.chmapp.login.gmail.GoogleSignInActivity;

public class HomeFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private View view;
    private FirebaseAuth auth;
    private GoogleSignInActivity gmailSignIn;
    static public GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private Context context;
    static public GoogleSignInOptions gso;
    public ProgressDialog mProgressDialog;
    static private ImageView img_background;
    static Bitmap bmp;
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //do view
            view = inflater.inflate(R.layout.fragment_home_signedin, container, false);
            TextView user_name = (TextView) view.findViewById(R.id.user_name);
            FirebaseUser user = auth.getCurrentUser();
            if (user.getEmail() == null)
            {
                user_name.setText("Welcome to CHMap: You are logged in as anonymous");
            }
            else{
                user_name.setText("Welcome to CHMap: " + user.getDisplayName() +
                        "\nYour email is: " + user.getEmail() + "\nProvider: "+ user.getProviders());
            }


            Button signout = (Button) view.findViewById(R.id.sign_out_button);
            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    if (mGoogleApiClient != null){
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {

                                        final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                                        fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
                                    }
                                });
                    }
                    else{
                        final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                        fragmentManager.replace(R.id.content, new HomeFragment(), "home").commit();
                    }
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_home_unsigned, container, false);

            if (bmp == null){
                DisplayMetrics size = context.getResources().getDisplayMetrics();
                bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(),R.drawable.home_image_unsigned),size.widthPixels,size.heightPixels,true);

            }

                img_background = (ImageView) view.findViewById(R.id.view_img_background);
                img_background.setImageBitmap(bmp);



            Button emailSignIn = (Button) view.findViewById(R.id.email_sign_in_button);
            Button anonymousSignIn = (Button) view.findViewById(R.id.anoymous_sign_in_button);

            emailSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new LoginActivity(), "LoginWithEmail").commit();
                }
            });



            view.findViewById(R.id.gmail_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new GoogleSignInActivity(), "GoogleSignIn").commit();
                }
            });

            anonymousSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.content, new AnonymousAuthActivity(), "LoginAnonymous").commit();
                }
            });

        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {

    }
}
