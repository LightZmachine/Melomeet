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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.MainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.models.User;
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
    private IMainActivity mInterface;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Log.d(TAG, "onCreateView: started.");
//      setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        mContext = getActivity();
        Bundle args = getArguments();
        if (args != null) {
            mUser = args.getParcelable(getString(R.string.bundle_object_user));
        }

        mProgressDialog = new ProgressDialog(mContext);
        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        setupToolbar();
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

        UniversalImageLoader.setImage(mUser.getThumb_profile_image(), mImageProfile, null, "");
    }

    @OnClick(R.id.iv_backArrow)
    public void backArrowClicked() {
        Log.d(TAG, "backArrowClicked: clicked.");
        mInterface.onBackPressed();
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
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }
}
