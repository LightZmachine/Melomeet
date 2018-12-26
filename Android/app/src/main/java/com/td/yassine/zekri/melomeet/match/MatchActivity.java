package com.td.yassine.zekri.melomeet.match;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.td.yassine.zekri.melomeet.HomeActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.map.MapActivity;
import com.td.yassine.zekri.melomeet.messages.MessagesActivity;
import com.td.yassine.zekri.melomeet.model.User;
import com.td.yassine.zekri.melomeet.profile.ProfileActivity;
import com.td.yassine.zekri.melomeet.utils.BottomNavigationViewHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MatchActivity extends AppCompatActivity {

    //Constants
    private static final String TAG = "MatchActivity";
    private static final int ACTIVITY_NUM = 3;
    //Widgets
    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNavigationView;

    //Variables
    private User mUser;
    private Bundle mBundle;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        ButterKnife.bind(this);

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mUser = mBundle.getParcelable(getString(R.string.bundle_object_user));
            setupBottomNavigationView();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        initFirebaseAuth();
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(mBottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(MatchActivity.this, this, mBottomNavigationView, mUser);
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Display the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Click item menu listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sign_out:
                Log.d(TAG, "onOptionsItemSelected: signOut clicked.");
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Intent i = new Intent(MatchActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged: true.");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in.
                    Log.d(TAG, "onAuthStateChanged: signedIn");
                } else {
                    //User is signed out.
                    Log.d(TAG, "onAuthStateChanged: user isn't signed in. Going to login activity.");

                    Intent i = new Intent(MatchActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
    }

}
