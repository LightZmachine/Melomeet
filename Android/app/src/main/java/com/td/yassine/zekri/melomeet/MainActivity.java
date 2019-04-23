package com.td.yassine.zekri.melomeet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.map.MapFragment;
import com.td.yassine.zekri.melomeet.match.MatchFragment;
import com.td.yassine.zekri.melomeet.messages.ChatFragment;
import com.td.yassine.zekri.melomeet.messages.ViewMessageFragment;
import com.td.yassine.zekri.melomeet.models.ChatMessage;
import com.td.yassine.zekri.melomeet.models.FragmentTag;
import com.td.yassine.zekri.melomeet.models.Post;
import com.td.yassine.zekri.melomeet.models.User;
import com.td.yassine.zekri.melomeet.posts.AddPostFragment;
import com.td.yassine.zekri.melomeet.posts.FullScreenImageFragment;
import com.td.yassine.zekri.melomeet.profile.EditProfileFragment;
import com.td.yassine.zekri.melomeet.profile.ViewProfileFragment;
import com.td.yassine.zekri.melomeet.utils.UniversalImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BottomNavigationViewEx.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        IMainActivity {

    //constants
    private static final String TAG = "MainActivity";
    private static final int HOME_FRAGMENT = 0;
    private static final int PROFILE_FRAGMENT = 1;
    private static final int MAP_FRAGMENT = 2;
    private static final int MATCH_FRAGMENT = 3;
    private static final int MESSAGES_FRAGMENT = 4;
    public static ArrayList<String> mFragmentsTags = new ArrayList<>();
    public static ArrayList<FragmentTag> mFragments = new ArrayList<>();
    public FirebaseAuth mAuth;
    public FirebaseFirestore mDb;
    //widgets
    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNavigationViewEx;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerlayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    //vars
    private Bundle mBundle;
    private Context mContext;
    private User mUser;
    private ProgressDialog mProgressDialog;
    private int mExitCount = 0;
    private ArrayList<Post> mAllPosts = new ArrayList<>();
    //firebase
    private FirebaseUser mCurrentUser = null;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private ListenerRegistration mProfileInfoListener;
    //fragments
    private HomeFragment mHomeFragment;
    private ViewProfileFragment mViewProfileFragment;
    private MapFragment mMapFragment;
    private MatchFragment mMatchFragment;
    private ViewMessageFragment mViewMessageFragment;
    private EditProfileFragment mEditProfileFragment;
    private AgreementFragment mAgreementFragment;
    private AddPostFragment mAddPostFragment;
    private FullScreenImageFragment mFullScreenImageFragment;
    private ChatFragment mChatFragment;
    private int cnt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starting MainActivity.");

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mContext = MainActivity.this;
        mBundle = getIntent().getExtras();

        initBottomNavigationView();
        setNavigationViewListener();
        initImageLoader();
        init();
        initProgressDialog();
        initFirebaseInstances();
    }

    private void setNavigationViewListener() {
        Log.d(TAG, "setNavigationViewListener: initializing navigation drawer onclicklistener.");
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(getString(R.string.progress_dialog_loading_title));
        mProgressDialog.setMessage(getString(R.string.progress_dialog_loading_user_infos));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void initFirebaseInstances() {
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged: true");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: user is signed in.");
                    mCurrentUser = user;
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                            .build();
                    mGoogleApiClient.connect();

                    DocumentReference docRef = mDb.collection(getString(R.string.collection_users)).document(mCurrentUser.getUid());

                    mProfileInfoListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "onEvent: " + e.toString());
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                            Log.d(TAG, "onEvent: user data retrieved.");
                            mProgressDialog.dismiss();
                            mUser = documentSnapshot.toObject(User.class);
                        }
                    });
                } else {
                    Log.d(TAG, "onAuthStateChanged: user is signed out.");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    public void init() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mHomeFragment, getString(R.string.tag_fragment_home));
            transaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_home));
            mFragments.add(new FragmentTag(mHomeFragment, getString(R.string.tag_fragment_home)));
        } else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_home));
            mFragmentsTags.add(getString(R.string.tag_fragment_home));
        }
        setFragmentVisibilities(getString(R.string.tag_fragment_home));
    }

    private void initBottomNavigationView() {
        mBottomNavigationViewEx.enableAnimation(false);
        mBottomNavigationViewEx.enableItemShiftingMode(false);
        mBottomNavigationViewEx.enableShiftingMode(false);
        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(this);
    }

    private void setFragmentVisibilities(String fragmentTag) {
        if (fragmentTag.equals(getString(R.string.tag_fragment_home)) ||
                fragmentTag.equals(getString(R.string.tag_fragment_view_profile)) ||
                fragmentTag.equals(getString(R.string.tag_fragment_map)) ||
                fragmentTag.equals(getString(R.string.tag_fragment_match)) ||
                fragmentTag.equals(getString(R.string.tag_fragment_view_messages))) {
            showBottomNavigation();
        } else {
            hideBottomNavigation();
        }

        for (int i = 0; i < mFragments.size(); i++) {
            if (fragmentTag.equals(mFragments.get(i).getTag())) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(mFragments.get(i).getFragment());
                transaction.commit();
            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(mFragments.get(i).getFragment());
                transaction.commit();
            }
        }
        setNavigationIcon(fragmentTag);
    }

    private void hideBottomNavigation() {
        if (mBottomNavigationViewEx != null) {
            mBottomNavigationViewEx.setVisibility(View.GONE);
        }
    }

    private void showBottomNavigation() {
        if (mBottomNavigationViewEx != null) {
            mBottomNavigationViewEx.setVisibility(View.VISIBLE);
        }
    }

    private void setNavigationIcon(String fragmentTag) {
        Menu menu = mBottomNavigationViewEx.getMenu();
        MenuItem menuItem;
        if (fragmentTag.equals(getString(R.string.tag_fragment_home))) {
            Log.d(TAG, "setNavigationIcon: home fragment is visible");
            menuItem = menu.getItem(HOME_FRAGMENT);
            menuItem.setChecked(true);
        } else if (fragmentTag.equals(getString(R.string.tag_fragment_view_profile))) {
            Log.d(TAG, "setNavigationIcon: profile fragment is visible");
            menuItem = menu.getItem(PROFILE_FRAGMENT);
            menuItem.setChecked(true);
        } else if (fragmentTag.equals(getString(R.string.tag_fragment_map))) {
            Log.d(TAG, "setNavigationIcon: map fragment is visible");
            menuItem = menu.getItem(MAP_FRAGMENT);
            menuItem.setChecked(true);
        } else if (fragmentTag.equals(getString(R.string.tag_fragment_match))) {
            Log.d(TAG, "setNavigationIcon: match fragment is visible");
            menuItem = menu.getItem(MATCH_FRAGMENT);
            menuItem.setChecked(true);
        } else if (fragmentTag.equals(getString(R.string.tag_fragment_view_messages))) {
            Log.d(TAG, "setNavigationIcon: messages fragment is visible");
            menuItem = menu.getItem(MESSAGES_FRAGMENT);
            menuItem.setChecked(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (mProfileInfoListener != null) {
            mProfileInfoListener.remove();
        }
    }

    @Override
    public void inflateEditProfileFragment() {
        if (mEditProfileFragment == null) {
            mEditProfileFragment = new EditProfileFragment();
            Bundle bundle_user = new Bundle();
            bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
            mEditProfileFragment.setArguments(bundle_user);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mEditProfileFragment, getString(R.string.tag_fragment_edit_profile));
            transaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_edit_profile));
            mFragments.add(new FragmentTag(mEditProfileFragment, getString(R.string.tag_fragment_edit_profile)));
        } else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_edit_profile));
            mFragmentsTags.add(getString(R.string.tag_fragment_edit_profile));
        }
        setFragmentVisibilities(getString(R.string.tag_fragment_edit_profile));
    }

    @Override
    public void inflateAddPostFragment() {
        if (mAddPostFragment == null) {
            mAddPostFragment = new AddPostFragment();
            Bundle bundle_user = new Bundle();
            bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
            mAddPostFragment.setArguments(bundle_user);
            bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mAddPostFragment, getString(R.string.tag_fragment_add_post));
            transaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_add_post));
            mFragments.add(new FragmentTag(mAddPostFragment, getString(R.string.tag_fragment_add_post)));
        } else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_add_post));
            mFragmentsTags.add(getString(R.string.tag_fragment_add_post));
        }
        setFragmentVisibilities(getString(R.string.tag_fragment_add_post));
    }

    @Override
    public void inflateFullScreenImageFragment(Object imageResource) {
        hideStatusBar();

        if (mFullScreenImageFragment == null) {
            mFullScreenImageFragment = new FullScreenImageFragment();
            mFullScreenImageFragment.setImageResource(imageResource);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mFullScreenImageFragment, getString(R.string.tag_fragment_full_screen_image));
            transaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_full_screen_image));
            mFragments.add(new FragmentTag(mFullScreenImageFragment, getString(R.string.tag_fragment_full_screen_image)));
        } else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_full_screen_image));
            mFragmentsTags.add(getString(R.string.tag_fragment_full_screen_image));
        }

        for (int i = 0; i < mFragments.size(); i++) {
            if (getString(R.string.tag_fragment_full_screen_image).equals(mFragments.get(i).getTag())) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                FullScreenImageFragment mFullScreenImageFragment = (FullScreenImageFragment) mFragments.get(i).getFragment();
                mFullScreenImageFragment.setImageResource(imageResource);
                transaction.show(mFullScreenImageFragment);
                transaction.commit();
            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(mFragments.get(i).getFragment());
                transaction.commit();
            }
        }
    }

    @Override
    public void inflateChatFragment(String receiverID, String discussionID, String username) {
        if (mChatFragment == null) {
            mChatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.bundle_object_user), mUser);
            bundle.putString(getString(R.string.bundle_receiver_id), receiverID);
            bundle.putString(getString(R.string.bundle_discussion_id), discussionID);
            bundle.putString(getString(R.string.bundle_username), username);
            mChatFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mChatFragment, getString(R.string.tag_fragment_chat));
            transaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_chat));
            mFragments.add(new FragmentTag(mChatFragment, getString(R.string.tag_fragment_chat)));
        } else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_chat));
            mFragmentsTags.add(getString(R.string.tag_fragment_chat));
        }
        setFragmentVisibilities(getString(R.string.tag_fragment_chat));
    }

    private void hideStatusBar() {
        // Hide Status Bar
        View decorView = getWindow().getDecorView();
        // Hide Status Bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showStatusBar() {
        View decorView = getWindow().getDecorView();
        // Show Status Bar.
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onBackPressed() {
        showStatusBar();
        int backStackCount = mFragmentsTags.size();
        if (backStackCount > 1) {
            String topFragmentTag = mFragmentsTags.get(backStackCount - 1);
            String newTopFragmentTag = mFragmentsTags.get(backStackCount - 2);
            setFragmentVisibilities(newTopFragmentTag);
            mFragmentsTags.remove(topFragmentTag);
            mExitCount = 0;
        } else if (backStackCount == 1) {
            mExitCount++;
            Toast.makeText(mContext, "One more click to leave the app.", Toast.LENGTH_SHORT).show();
        }

        if (mExitCount >= 2) super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home: {
                Log.d(TAG, "onNavigationItemSelected: home.");
                mFragmentsTags.clear();
                mFragmentsTags = new ArrayList<>();
                init();
                break;
            }
            case R.id.settings: {
                Log.d(TAG, "onNavigationItemSelected: settings.");
                // Add Settings Fragment etc.
                Toast.makeText(mContext, "Navigating to Settings ...", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.agreement: {
                Log.d(TAG, "onNavigationItemSelected: agreement.");
                if (mAgreementFragment == null) {
                    mAgreementFragment = new AgreementFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, mAgreementFragment, getString(R.string.tag_fragment_agreement));
                    transaction.commit();
                    mFragmentsTags.add(getString(R.string.tag_fragment_agreement));
                    mFragments.add(new FragmentTag(mAgreementFragment, getString(R.string.tag_fragment_agreement)));
                } else {
                    mFragmentsTags.remove(getString(R.string.tag_fragment_agreement));
                    mFragmentsTags.add(getString(R.string.tag_fragment_agreement));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_agreement));
                break;
            }
            case R.id.nav_home: {
                Log.d(TAG, "onNavigationItemSelected: home.");
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    Bundle bundle_user = new Bundle();
                    bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
                    mHomeFragment.setArguments(bundle_user);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, mHomeFragment, getString(R.string.tag_fragment_home));
                    transaction.commit();
                    mFragmentsTags.add(getString(R.string.tag_fragment_home));
                    mFragments.add(new FragmentTag(mHomeFragment, getString(R.string.tag_fragment_home)));
                } else {
                    mFragmentsTags.remove(getString(R.string.tag_fragment_home));
                    mFragmentsTags.add(getString(R.string.tag_fragment_home));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_home));
                break;
            }
            case R.id.nav_profil: {
                Log.d(TAG, "onNavigationItemSelected: profile.");
                if (mViewProfileFragment == null) {
                    mViewProfileFragment = new ViewProfileFragment();
                    Bundle bundle_user = new Bundle();
                    bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
                    mViewProfileFragment.setArguments(bundle_user);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, mViewProfileFragment, getString(R.string.tag_fragment_view_profile));
                    transaction.commit();
                    mFragmentsTags.add(getString(R.string.tag_fragment_view_profile));
                    mFragments.add(new FragmentTag(mViewProfileFragment, getString(R.string.tag_fragment_view_profile)));
                } else {
                    mFragmentsTags.remove(getString(R.string.tag_fragment_view_profile));
                    mFragmentsTags.add(getString(R.string.tag_fragment_view_profile));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_view_profile));
                break;
            }
            case R.id.nav_map: {
                Log.d(TAG, "onNavigationItemSelected: map.");
                if (mMapFragment == null) {
                    mMapFragment = new MapFragment();
                    Bundle bundle_user = new Bundle();
                    bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
                    mMapFragment.setArguments(bundle_user);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, mMapFragment, getString(R.string.tag_fragment_map));
                    transaction.commit();
                    mFragmentsTags.add(getString(R.string.tag_fragment_map));
                    mFragments.add(new FragmentTag(mMapFragment, getString(R.string.tag_fragment_map)));
                } else {
                    mFragmentsTags.remove(getString(R.string.tag_fragment_map));
                    mFragmentsTags.add(getString(R.string.tag_fragment_map));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_map));
                break;
            }
            case R.id.nav_match: {
                Log.d(TAG, "onNavigationItemSelected: match.");
                if (mMatchFragment == null) {
                    mMatchFragment = new MatchFragment();
                    Bundle bundle_user = new Bundle();
                    bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
                    mMatchFragment.setArguments(bundle_user);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, mMatchFragment, getString(R.string.tag_fragment_match));
                    transaction.commit();
                    mFragmentsTags.add(getString(R.string.tag_fragment_match));
                    mFragments.add(new FragmentTag(mMatchFragment, getString(R.string.tag_fragment_match)));
                } else {
                    mFragmentsTags.remove(getString(R.string.tag_fragment_match));
                    mFragmentsTags.add(getString(R.string.tag_fragment_match));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_match));
                break;
            }
            case R.id.nav_messages: {
                Log.d(TAG, "onNavigationItemSelected: messages.");
                if (mViewMessageFragment == null) {
                    mViewMessageFragment = new ViewMessageFragment();
                    Bundle bundle_user = new Bundle();
                    bundle_user.putParcelable(getString(R.string.bundle_object_user), mUser);
                    mViewMessageFragment.setArguments(bundle_user);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, mViewMessageFragment, getString(R.string.tag_fragment_view_messages));
                    transaction.commit();
                    mFragmentsTags.add(getString(R.string.tag_fragment_view_messages));
                    mFragments.add(new FragmentTag(mViewMessageFragment, getString(R.string.tag_fragment_view_messages)));
                } else {
                    mFragmentsTags.remove(getString(R.string.tag_fragment_view_messages));
                    mFragmentsTags.add(getString(R.string.tag_fragment_view_messages));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_view_messages));
                break;
            }
        }
        mDrawerlayout.closeDrawer(GravityCompat.START);
        return false;
    }
}

