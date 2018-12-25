package com.td.yassine.zekri.melomeet.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewGalleryProfilAdapter;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewMessageAdapter;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.profile.EditProfileFragment;
import com.td.yassine.zekri.melomeet.profile.ProfileActivity;
import com.td.yassine.zekri.melomeet.utils.BottomNavigationViewHelper;
import com.td.yassine.zekri.melomeet.utils.GridSpacingItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ViewMessageFragment extends Fragment {

    //constants
    private static final String TAG = "ViewMessageFragment";
    private static final int ACTIVITY_NUM = 4;

    //widgets
    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNavigationView;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;


    //variables
    private Context mContext;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_message, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        mContext = getActivity();

        Log.d(TAG, "onCreateView: started.");

        setupBottomNavigationView();
        setupFirebaseAuth();
        initRecyclerViewMessage();

        return view;
    }

    private void initRecyclerViewMessage() {
        RecyclerViewMessageAdapter adapter = new RecyclerViewMessageAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    /**
     * BottomNavigation View setup
     */
    private void setupBottomNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(mBottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity(), mBottomNavigationView);
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    /**
     * Setup the firebase auth
     */
    private void setupFirebaseAuth() {
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

                    Intent i = new Intent(mContext, LoginActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
