package com.td.yassine.zekri.melomeet.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.td.yassine.zekri.melomeet.HomeActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewGalleryProfilAdapter;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.model.User;
import com.td.yassine.zekri.melomeet.utils.BottomNavigationViewHelper;
import com.td.yassine.zekri.melomeet.utils.FirebaseMethods;
import com.td.yassine.zekri.melomeet.utils.GridSpacingItemDecoration;
import com.td.yassine.zekri.melomeet.utils.StringManipulation;
import com.td.yassine.zekri.melomeet.utils.UniversalImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class ViewProfileFragment extends Fragment {

    //constants
    private static final String TAG = "ViewProfileFragment";
    private static final int ACTIVITY_NUM = 1;
    private static final int GALLERY_PICK = 366;

    //widgets
    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;

    @BindView(R.id.tv_number_following)
    TextView mTv_number_following;
    @BindView(R.id.tv_number_followers)
    TextView mTv_number_followers;
    @BindView(R.id.tv_number_posts)
    TextView mTv_number_posts;
    @BindView(R.id.tv_status)
    TextView mTv_status;
    @BindView(R.id.tv_name_firstname)
    TextView mTv_name_firstname;
    @BindView(R.id.tv_fav_artist)
    TextView mTv_fav_artist;
    @BindView(R.id.tv_fav_single)
    TextView mTv_fav_single;
    @BindView(R.id.tv_description)
    TextView mTv_description;
    @BindView(R.id.image_profil)
    CircleImageView mImage_profile;

    //variables
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private User mUser;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDb;
    private ListenerRegistration mProfileInfoListener;
    private GoogleApiClient mGoogleApiClient;

    //firebase storage
    private FirebaseStorage mImageStorage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        setHasOptionsMenu(true);

        mContext = getActivity();
        mUser = new User();

        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(mContext);

        Log.d(TAG, "onCreateView: started.");

        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        setupBottomNavigationView();
        setupToolbar();
        setupFirebaseAuth();
        initRecyclerViewGallery();
        this.mImageStorage = FirebaseStorage.getInstance();
        this.mDb = FirebaseFirestore.getInstance();

        return view;
    }

    @OnClick(R.id.image_profil)
    public void ivImageProfileClick() {
        Intent intent = CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1)
                .setMinCropWindowSize(500, 500)
                .getIntent(mContext);
        startActivityForResult(intent, GALLERY_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                Log.d(TAG, "onActivityResult: SHOW PROGRESS ?!");
                progressDialog.setTitle(getString(R.string.progress_dialog_upload_profile_image));
                progressDialog.setMessage(getString(R.string.progress_dialog_upload_profile_image_message));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final String uID = mAuth.getCurrentUser().getUid();
                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());

                try {
                    Bitmap thumb_bitmap = new Compressor(mContext)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                    final StorageReference filePath = mImageStorage.getReference().child(getString(R.string.storage_profile_image)).child(uID + ".jpg");
                    final StorageReference thumb_filePath = mImageStorage.getReference()
                            .child(getString(R.string.storage_profile_image))
                            .child(getString(R.string.storage_thumbs_image))
                            .child(uID + ".jpg");


                    filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Working", Toast.LENGTH_SHORT).show();
                                final String download_url = task.getResult().toString();

                                thumb_filePath.putBytes(thumb_byte).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        return thumb_filePath.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> thumb_task) {
                                        String thumb_downloadUrl = thumb_task.getResult().toString();
                                        if (thumb_task.isSuccessful()) {

                                            HashMap<String, Object> updateMap = new HashMap<>();
                                            updateMap.put("image", download_url);
                                            updateMap.put("thumb_image", thumb_downloadUrl);

                                            DocumentReference userRef = mDb.collection(getString(R.string.collection_users)).document(uID);
                                            userRef.update(updateMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(mContext, "Success Uploading", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(mContext, "Upload Failed", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(mContext, "Error in uploading thumbnail", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(mContext, "Error in loading", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @OnClick(R.id.tv_edit_profile)
    public void tvEditProfileClick() {
        Log.d(TAG, "tvEditProfileClick: clicked");
        Fragment fragment = new EditProfileFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.bundle_object_user), mUser);
        fragment.setArguments(args);
        ft.replace(R.id.container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack("HEY");
        ft.commit();
    }


    private void initRecyclerViewGallery() {
        RecyclerViewGalleryProfilAdapter adapter = new RecyclerViewGalleryProfilAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(5));
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
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

    private void updateProfileUi(User user) {

        mUser.setNumber_following(user.getNumber_following());
        mUser.setNumber_followers(user.getNumber_followers());
        mUser.setNumber_posts(user.getNumber_posts());
        mUser.setFav_artist(user.getFav_artist());
        mUser.setFav_single(user.getFav_single());
        mUser.setImage(user.getImage());
        mUser.setUsername(user.getUsername());
        mUser.setName(user.getName());
        mUser.setFirstname(user.getFirstname());
        mUser.setDescription(user.getDescription());
        mUser.setStatus(user.getStatus());

        mTv_number_following.setText(String.valueOf(mUser.getNumber_following()));
        mTv_number_followers.setText(String.valueOf(mUser.getNumber_followers()));
        mTv_number_posts.setText(String.valueOf(mUser.getNumber_posts()));
        mTv_name_firstname.setText(mUser.getFirstname() + " " + mUser.getName());
        mTv_status.setText("Status: " + mUser.getStatus());
        mTv_fav_artist.setText(mUser.getFav_artist());
        mTv_fav_single.setText(mUser.getFav_single());
        mTv_description.setText(mUser.getDescription());

        mToolbar.setTitle(mUser.getUsername());
        UniversalImageLoader.setImage(mUser.getImage(), mImage_profile, null, "");

        this.mProgressDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        this.mProgressDialog.setTitle(getString(R.string.progress_dialog_profile_user));
        this.mProgressDialog.setMessage(getString(R.string.progress_dialog_profile_message));
        this.mProgressDialog.setCanceledOnTouchOutside(false);
        this.mProgressDialog.show();

        final String userID = mAuth.getCurrentUser().getUid();

        DocumentReference docRef = this.mDb.collection(mContext.getString(R.string.collection_users)).document(userID);

        mProfileInfoListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "onEvent: " + e.toString());
                    Intent i = new Intent(mContext, HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return;
                }
                updateProfileUi(documentSnapshot.toObject(User.class));
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (mProfileInfoListener != null) {
            mProfileInfoListener.remove();
        }
    }

    //Display the menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
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
                                Intent i = new Intent(mContext, LoginActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
