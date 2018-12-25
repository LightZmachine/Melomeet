package com.td.yassine.zekri.melomeet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewHomeFeedAdapter;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.map.MapActivity;
import com.td.yassine.zekri.melomeet.match.MatchActivity;
import com.td.yassine.zekri.melomeet.messages.MessagesActivity;
import com.td.yassine.zekri.melomeet.model.User;
import com.td.yassine.zekri.melomeet.profile.ProfileActivity;
import com.td.yassine.zekri.melomeet.utils.BottomNavigationViewHelper;
import com.td.yassine.zekri.melomeet.utils.UniversalImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class HomeActivity extends AppCompatActivity {

    //Constants
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;

    //Widgets
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.main_feed)
    RelativeLayout mMain_feed;
    @BindView(R.id.logo_home_middle)
    ImageView mImageView_home_middle;
    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //Variables
    private Context mContext = HomeActivity.this;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starting HomeActivity.");

        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initImageLoader();

        setupBottomNavigationView();

        mAuth = FirebaseAuth.getInstance();
        setupToolbar();
        initFirebaseAuth();
        initRecyclerViewFeed();
    }

    /**
     * Setting up the toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }


    private void initRecyclerViewFeed() {
        RecyclerViewHomeFeedAdapter adapter = new RecyclerViewHomeFeedAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(mBottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(HomeActivity.this, this, mBottomNavigationView);
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @OnTouch(R.id.logo_home_middle)
    public boolean logo_middleTouch(final View view, MotionEvent event) {

        Log.d(TAG, "logo_middleTouch: true.");
        final Animation shake = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.shake);
        final Animation rotate = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.rotate);


        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(700);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(700);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                mMain_feed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeOut);
        animation.addAnimation(rotate);
        animation.addAnimation(shake);

        view.startAnimation(animation);
        return true;
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
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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

                    Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
}
