package com.td.yassine.zekri.melomeet.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.MainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewGalleryProfilAdapter;
import com.td.yassine.zekri.melomeet.models.User;
import com.td.yassine.zekri.melomeet.utils.FilePaths;
import com.td.yassine.zekri.melomeet.utils.FirebaseMethods;
import com.td.yassine.zekri.melomeet.utils.GridSpacingItemDecoration;
import com.td.yassine.zekri.melomeet.utils.UniversalImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class ViewProfileFragment extends Fragment implements OnLikeListener {

    //constants
    private static final String TAG = "ViewProfileFragment";
    private static final int GALLERY_PICK = 366;

    //widgets
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

    private LikeButton mLikeButton;

    //variables
    private Context mContext;
    private User mUser;
    private IMainActivity mInterface;

    //firebase
    private FirebaseAuth mAuth;
    private ListenerRegistration mListenerRegistration;
    private FirebaseMethods mFirebaseMethods;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListenerRegistration != null) {
            mListenerRegistration.remove();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mListenerRegistration = db.collection(getString(R.string.collection_users)).document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Log.d(TAG, "onEvent: User Changed.");
                if (e != null) {
                    Log.e(TAG, "onEvent: listenError", e);
                    return;
                }
                mUser = documentSnapshot.toObject(User.class);
                updateProfileUi(mUser);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started.");
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        mContext = getActivity();

        mLikeButton = view.findViewById(R.id.heart_button);
        mLikeButton.setOnLikeListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseMethods = new FirebaseMethods(mContext, mAuth);

        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        checkIfFollowing();
        setupToolbar();
        initRecyclerViewGallery();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
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

                FilePaths filePaths = new FilePaths();
                final String uID = mAuth.getCurrentUser().getUid();
                Uri imageUri = result.getUri();
                File thumb_file = new File(imageUri.getPath());

                try {
                    Bitmap thumb_bitmap = new Compressor(mContext)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    String uploadPath = filePaths.FIREBASE_THUMB_PROFILE_IMAGE + "/" + uID + "/thumb_profile_image.jpeg";
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child(uploadPath);

                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .setContentLanguage("fr")
                            .build();
                    filePath.putBytes(thumb_byte, metadata).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUrl = task.getResult().toString();
                                HashMap<String, Object> updateProfileImage = new HashMap<>();
                                updateProfileImage.put("thumb_profile_image", downloadUrl);

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db.collection(getString(R.string.collection_users)).document(uID);
                                userRef.update(updateProfileImage)
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
                                Toast.makeText(mContext, "Error in uploading new image profile.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: error: " + result.getError().toString());
            }
        }
    }

    @OnClick(R.id.tv_edit_profile)
    public void tvEditProfileClick() {
        Log.d(TAG, "tvEditProfileClick: clicked");
        mInterface.inflateEditProfileFragment();
    }


    private void initRecyclerViewGallery() {
        RecyclerViewGalleryProfilAdapter adapter = new RecyclerViewGalleryProfilAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(5));
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
    }

    /**
     * Setting up the toolbar
     */
    private void setupToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
    }


    private void updateProfileUi(User user) {

        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle(getString(R.string.progress_dialog_loading_title));
        progressDialog.setMessage(getString(R.string.progress_dialog_loading_user_infos));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mUser.setNumber_following(user.getNumber_following());
        mUser.setNumber_followers(user.getNumber_followers());
        mUser.setNumber_posts(user.getNumber_posts());
        mUser.setFav_artist(user.getFav_artist());
        mUser.setFav_single(user.getFav_single());
        mUser.setUsername(user.getUsername());
        mUser.setThumb_profile_image(user.getThumb_profile_image());
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

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.melomeet_logo)
                .centerInside()
                .override(100, 100);
        Glide.with(mContext)
                .setDefaultRequestOptions(options)
                .load(mUser.getThumb_profile_image())
                .into(mImage_profile);

        progressDialog.dismiss();
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

//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(@NonNull Status status) {
//                                Intent i = new Intent(mContext, LoginActivity.class);
//                                startActivity(i);
//                                getActivity().finish();
//                            }
//                        }
//                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkIfFollowing() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        CollectionReference followingRef = db.collection(getString(R.string.collection_users))
                .document(userID)
                .collection(getString(R.string.collection_following));

        followingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.getString("userID").equals("nn4qxJWlSJWing1K4dqYHj3Spye2")) {
                            Log.d(TAG, "onComplete: following founded.");
                            mLikeButton.setLiked(true);
                            break;
                        } else {
                            mLikeButton.setLiked(false);
                        }
                    }
                } else {
                    Log.d(TAG, "onComplete: Error getting documents: " + task.getException());
                }
            }
        });
    }

    @Override
    public void liked(LikeButton likeButton) {
        Log.d(TAG, "liked: liked.");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        CollectionReference followingRef = db.collection(getString(R.string.collection_users))
                .document(userID)
                .collection(getString(R.string.collection_following));
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userID", "nn4qxJWlSJWing1K4dqYHj3Spye2");
        followingRef.add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    mFirebaseMethods.increaseFollowingUser(mUser);
                } else {
                    Log.d(TAG, "onComplete: error: " + task.getException());
                }
            }
        });
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        Log.d(TAG, "unLiked: unliked.");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        Query query = db.collection(getString(R.string.collection_users))
                .document(userID)
                .collection(getString(R.string.collection_following))
                .whereEqualTo("userID", "nn4qxJWlSJWing1K4dqYHj3Spye2");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        snapshot.getReference().delete();
                        mFirebaseMethods.decreaseFollowingUser(mUser);
                    }
                } else {
                    Log.d(TAG, "onComplete: error getting the documents : " + task.getException());
                }
            }
        });
    }
}
