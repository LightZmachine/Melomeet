package com.td.yassine.zekri.melomeet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewHomeFeedAdapter;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.map.MapActivity;
import com.td.yassine.zekri.melomeet.match.MatchActivity;
import com.td.yassine.zekri.melomeet.messages.MessagesActivity;
import com.td.yassine.zekri.melomeet.model.User;
import com.td.yassine.zekri.melomeet.posts.AddPostFragment;
import com.td.yassine.zekri.melomeet.profile.ProfileActivity;
import com.td.yassine.zekri.melomeet.utils.BottomNavigationViewHelper;
import com.td.yassine.zekri.melomeet.utils.UniversalImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class HomeActivity extends AppCompatActivity {

    //Constants
    private static final String TAG = "HomeActivity";

    private Bundle mBundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starting HomeActivity.");

        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        mBundle = getIntent().getExtras();
        init();
    }

    private void init() {
        FeedHomeFragment fragment = new FeedHomeFragment();
        if (mBundle != null) {
            fragment.setArguments(mBundle);
        }
        FragmentTransaction transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("Test");
        transaction.commit();
    }
}
