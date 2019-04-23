package com.td.yassine.zekri.melomeet.posts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.models.Attachment;
import com.td.yassine.zekri.melomeet.models.Post;
import com.td.yassine.zekri.melomeet.models.User;
import com.td.yassine.zekri.melomeet.utils.FilePaths;
import com.td.yassine.zekri.melomeet.utils.FileSearch;
import com.td.yassine.zekri.melomeet.utils.HorizontalSpacingItemDecorator;
import com.td.yassine.zekri.melomeet.utils.Permissions;
import com.td.yassine.zekri.melomeet.utils.RotateBitmap;
import com.td.yassine.zekri.melomeet.utils.SectionsPagerAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddPostFragment extends Fragment implements AttachmentRecyclerViewAdapter.IsAttachmentSelected {

    //constants
    private static final String TAG = "AddPostFragment";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final int RECYCLERVIEW_HORIZONTAL_SPACING = 10;
    private static final int GALLERY_REQUEST_CODE = 2455;
    private static final int CAMERA_REQUEST_CODE = 6548;

    // Widgets
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.linLayoutAddAttachments)
    LinearLayout mLinLayoutAddAttachments;
    @BindView(R.id.iv_delete_img)
    ImageView mIvDeleteImg;
    @BindView(R.id.editText_title)
    EditText mEditText_title;
    @BindView(R.id.editText_content)
    EditText mEditText_content;

    //variables
    private Context mContext;
    private IMainActivity mInterface;
    private ArrayList<String> mAttachments = new ArrayList<>();
    private AttachmentRecyclerViewAdapter mAttachmentRecyclerViewAdapter;
    private User mUser;
    private int mUploadCount = 0;

    //firebase
    private Bundle mBundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        Log.d(TAG, "onCreateView: started.");
        ButterKnife.bind(this, view);

        mContext = getActivity();
        mBundle = getArguments();
        if (mBundle != null) {
            mUser = mBundle.getParcelable(getString(R.string.bundle_object_user));
        }

        initRecyclerView();

        return view;
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        HorizontalSpacingItemDecorator itemDecorator = new HorizontalSpacingItemDecorator(RECYCLERVIEW_HORIZONTAL_SPACING);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAttachmentRecyclerViewAdapter = new AttachmentRecyclerViewAdapter(getContext(), mAttachments, this);
        mRecyclerView.setAdapter(mAttachmentRecyclerViewAdapter);
    }

    /**
     * verifiy all the permissions passed to the array
     *
     * @param permissions
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(getActivity(), permissions, VERIFY_PERMISSIONS_REQUEST);
    }

    /**
     * Check an array of permissions
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     *
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(getActivity(), permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    @OnClick(R.id.iv_checked)
    public void ivCheckedClicked(View view) {
        Log.d(TAG, "ivCheckedClicked: clicked.");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String title = mEditText_title.getText().toString().trim();
        String content = mEditText_content.getText().toString().trim();

        if (title.equals("")) {
            Toast.makeText(mContext, "Write a title please.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.equals("")) {
            Toast.makeText(mContext, "Write a content.", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle(getString(R.string.progress_dialog_creating_a_new_post));
        progressDialog.setMessage(getString(R.string.progress_dialog_creating_a_new_post_message));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newPostRef = db.collection(getString(R.string.collection_posts)).document();
        final Post post = new Post();

        post.setTitle(title);
        post.setContent(content);
        post.setUser_id(user_id);
        post.setPost_id(newPostRef.getId());
        if (mAttachments.size() > 0) {
            post.setHasAttachments(true);
        } else {
            post.setHasAttachments(false);
        }

        newPostRef.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    int number_posts = mUser.getNumber_posts() + 1;
                    db.collection(getString(R.string.collection_users)).document(post.getUser_id()).update("number_posts", number_posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (mAttachments.size() > 0) {
                                    Log.d(TAG, "onComplete: attachments to add.");
                                    for (String attachment : mAttachments) {
                                        addAttachmentToPostInDatabase(post, progressDialog, attachment);
                                    }
                                } else {
                                    Log.d(TAG, "onComplete: no attachments.");
                                    mEditText_title.setText("");
                                    mEditText_content.setText("");
                                    progressDialog.dismiss();
                                    mInterface.onBackPressed();
                                }
                            } else {
                                Log.d(TAG, "onComplete: error: " + task.getException());
                            }
                        }
                    });
                } else {
                    Snackbar.make(getActivity().getCurrentFocus().getRootView(), "Failed to create a new post.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addAttachmentToPostInDatabase(final Post post, final ProgressDialog progressDialog, String imgUrl) {
        FilePaths filePaths = new FilePaths();
        final SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        String uploadPath = filePaths.FIREBASE_POST_IMAGE_STORAGE + "/" + post.getPost_id() + "/" + FileSearch.getFileName(Uri.parse(imgUrl), getActivity()) + "_" + format;
        final String imageName = FileSearch.getFileName(Uri.parse(imgUrl), getActivity()) + "_" + format;

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uploadPath);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setContentLanguage("fr")
                .build();

        UploadTask uploadTask = storageReference.putFile(Uri.parse(imgUrl), metadata);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri firebaseURL = task.getResult();
                    setNewPostAttachment(firebaseURL.toString(), imageName, post.getPost_id(), progressDialog);
                } else {
                    Toast.makeText(mContext, "error while uploading image.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setNewPostAttachment(final String downloadUrl, String filename, String postID, final ProgressDialog progressDialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newAttachmentRef = db.collection(getString(R.string.collection_posts))
                .document(postID)
                .collection(getString(R.string.collection_attachments))
                .document();

        Attachment attachment = new Attachment();
        attachment.setName(filename);
        attachment.setUrl(downloadUrl);
        attachment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        newAttachmentRef.set(attachment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: new attachment attached to the post.");
                    mUploadCount++;
                    if (mUploadCount == mAttachments.size()) {
                        mUploadCount = 0;
                        mEditText_title.setText("");
                        mEditText_content.setText("");
                        mAttachments.clear();
                        mAttachmentRecyclerViewAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        mInterface.onBackPressed();
                    }
                } else {
                    Log.d(TAG, "onComplete: upload failed: " + task.getException());
                }
            }
        });
    }

    @OnClick(R.id.iv_camera)
    public void ivCameraClicked() {
        Log.d(TAG, "ivCameraClicked: clicked.");
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            //Permissions OK


        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    @OnClick(R.id.iv_gallery)
    public void ivGalleryClicked() {
        Log.d(TAG, "ivGalleryClicked: clicked.");
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            //Permissions OK
            Log.d(TAG, "ivGalleryClicked: accessing phone's memory.");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    @OnClick(R.id.iv_backArrow)
    public void ivBackArrowClicked() {
        Log.d(TAG, "ivBackArrowClicked: clicked.");
        mInterface.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image: " + selectedImageUri);

            /**
             * Vérification de l'orientation de l'image pour firebase
             */
            try {
                RotateBitmap rotateBitmap = new RotateBitmap();
                Bitmap bitmap = rotateBitmap.HandleSamplingAndRotationBitmap(mContext, selectedImageUri);
                selectedImageUri = rotateBitmap.getImageUri(mContext, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mAttachments.add(selectedImageUri.toString());
            mAttachmentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @OnClick(R.id.iv_delete_img)
    public void ivDeleteImgCLicked(View view) {
        Log.d(TAG, "ivDeleleteImgCLicked: clicked.");
        removeAttachments();
    }

    private void removeAttachments() {
        List<Integer> selectedAttachments = mAttachmentRecyclerViewAdapter.getSelectedItems();
        for (int i : selectedAttachments) {
            final String url = mAttachments.get(i);
            mAttachments.remove(url);
            mAttachmentRecyclerViewAdapter.notifyDataSetChanged();
        }
        mAttachmentRecyclerViewAdapter.clearSelection();
        isSelected(false);
    }

    @Override
    public void isSelected(boolean isSelected) {
        if (isSelected) {
            removeAttachmentsMode();
        } else {
            addAttachmentMode();
        }
    }

    private void addAttachmentMode() {
        mLinLayoutAddAttachments.setVisibility(View.VISIBLE);
        mIvDeleteImg.setVisibility(View.GONE);
    }

    private void removeAttachmentsMode() {
        mLinLayoutAddAttachments.setVisibility(View.GONE);
        mIvDeleteImg.setVisibility(View.VISIBLE);
    }
}