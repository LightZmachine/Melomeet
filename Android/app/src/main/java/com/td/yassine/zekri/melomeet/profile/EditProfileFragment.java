package com.td.yassine.zekri.melomeet.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewGalleryProfilAdapter;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.model.User;
import com.td.yassine.zekri.melomeet.utils.BottomNavigationViewHelper;
import com.td.yassine.zekri.melomeet.utils.GridSpacingItemDecoration;
import com.td.yassine.zekri.melomeet.utils.UniversalImageLoader;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment {

    //constants
    private static final String TAG = "EditProfileFragment";

    //widgets
    @BindView(R.id.main_layout)
    RelativeLayout mMainLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.text_input_name)
    TextInputLayout mTextInputLayout_name;
    @BindView(R.id.text_input_firstname)
    TextInputLayout mTextInputLayout_firstname;
    @BindView(R.id.text_input_status)
    TextInputLayout mTextInputLayout_status;
    @BindView(R.id.text_input_description)
    TextInputLayout mTextInputLayout_description;
    @BindView(R.id.text_input_fav_artist)
    TextInputLayout mTextInputLayout_fav_artist;
    @BindView(R.id.text_input_fav_single)
    TextInputLayout mTextInputLayout_fav_single;
    @BindView(R.id.image_profil)
    CircleImageView mImageProfile;

    //variables
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private User mUser;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Log.d(TAG, "onCreateView: started.");
//        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        mContext = getActivity();
        mUser = User.getInstance();

        mProgressDialog = new ProgressDialog(mContext);
        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        setupToolbar();
        setupFirebaseAuth();
        init();
        return view;
    }

    private void init() {
        // personnal information
        mTextInputLayout_name.getEditText().setText(mUser.getName());
        mTextInputLayout_firstname.getEditText().setText(mUser.getFirstname());
        mTextInputLayout_status.getEditText().setText(mUser.getStatus());
        mTextInputLayout_description.getEditText().setText(mUser.getDescription());

        // about music
        mTextInputLayout_fav_artist.getEditText().setText(mUser.getFav_artist());
        mTextInputLayout_fav_single.getEditText().setText(mUser.getFav_single());

        UniversalImageLoader.setImage(mUser.getImage(), mImageProfile, null, "");
    }

    @OnClick(R.id.iv_backArrow)
    public void backArrowClicked() {
        Log.d(TAG, "backArrowClicked: clicked.");
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.iv_checked)
    public void ivCheckedClicked() {
        Log.d(TAG, "ivCheckedClicked: saving changes.");

        final String name = mTextInputLayout_name.getEditText().getText().toString().trim();
        final String firstname = mTextInputLayout_firstname.getEditText().getText().toString().trim();
        final String status = mTextInputLayout_status.getEditText().getText().toString().trim();
        final String description = mTextInputLayout_description.getEditText().getText().toString().trim();
        final String fav_artist = mTextInputLayout_fav_artist.getEditText().getText().toString().trim();
        final String fav_single = mTextInputLayout_fav_single.getEditText().getText().toString().trim();


        if (!hasValidationErrors(name, firstname, status, description, fav_artist, fav_single)) {
            this.mProgressDialog.setTitle(getString(R.string.progress_dialog_update_profile_user));
            this.mProgressDialog.setMessage(getString(R.string.progress_dialog_update_profile_message));
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.show();

            HashMap<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("firstname", firstname);
            data.put("status", status);
            data.put("description", description);
            data.put("fav_artist", fav_artist);
            data.put("fav_single", fav_single);

            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference docRef = FirebaseFirestore.getInstance().collection(getString(R.string.collection_users)).document(uID);
            docRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: updateSuccess!");
                    mProgressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mMainLayout, "Update successful !", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
                    snackbar.show();
                    getFragmentManager().popBackStack();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mMainLayout, "An error happened, try again.", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                    snackbar.show();
                }
            });
        }

    }

    private boolean hasValidationErrors(String name, String firstname, String status, String description, String fav_artist, String fav_single) {
        View view = null;
        boolean cancel = false;
        if (TextUtils.isEmpty(name)) {
            mTextInputLayout_name.setError(getString(R.string.error_name));
            view = mTextInputLayout_name.getEditText();
            cancel = true;
        }
        if (TextUtils.isEmpty(firstname)) {
            mTextInputLayout_firstname.setError(getString(R.string.error_name));
            view = mTextInputLayout_firstname.getEditText();
            cancel = true;
        }

        if (cancel) {
            view.requestFocus();
        }
        return cancel;
    }

    /**
     * Setting up the toolbar
     */
    private void setupToolbar() {
        ((ProfileActivity) getActivity()).setSupportActionBar(mToolbar);
        ((ProfileActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
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

    //Display the menu
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
//        menuInflater.inflate(R.menu.menu, menu);
//        super.onCreateOptionsMenu(menu, menuInflater);
//    }
//
//    //Click item menu listener
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item_sign_out:
//                Log.d(TAG, "onOptionsItemSelected: signOut clicked.");
//                mAuth.signOut();
//
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(@NonNull Status status) {
//                                Intent i = new Intent(mContext, LoginActivity.class);
//                                startActivity(i);
//                                finish();
//                            }
//                        }
//                );
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
