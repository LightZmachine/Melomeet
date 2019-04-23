package com.td.yassine.zekri.melomeet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.models.Post;
import com.td.yassine.zekri.melomeet.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewHomeFeedAdapter extends RecyclerView.Adapter<RecyclerViewHomeFeedAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewHomeFeedAda";

    private Context mContext;
    private ArrayList<Post> mPosts;
    private FirebaseFirestore mDB;
    private FirebaseAuth mFirebaseAuth;

    public RecyclerViewHomeFeedAdapter(Context context, ArrayList<Post> posts) {
        this.mContext = context;
        this.mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_home_feedback, parent, false);
        ViewHolder holder = new ViewHolder(view);
        mDB = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            //getting user infos related to the post
            mDB.collection(mContext.getString(R.string.collection_users))
                    .document(mPosts.get(position).getUser_id())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                mPosts.get(position).setUser(task.getResult().toObject(User.class));
                                if (mPosts.get(position).isHasAttachments()) {
                                    // getting attachments related to the post
                                    final ArrayList<String> attachmentsUrl = new ArrayList<>();
                                    mDB.collection(mContext.getString(R.string.collection_posts))
                                            .document(mPosts.get(position).getPost_id())
                                            .collection(mContext.getString(R.string.collection_attachments))
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                        for (DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()) {
                                                            attachmentsUrl.add(snapshot.getDocument().getString("url"));
                                                        }
                                                        mPosts.get(position).setImgUrls(attachmentsUrl);

                                                        RequestOptions options = new RequestOptions()
                                                                .placeholder(R.drawable.melomeet_logo)
                                                                .centerCrop();

                                                        // content image
                                                        Glide.with(mContext)
                                                                .setDefaultRequestOptions(options)
                                                                .load(mPosts.get(position).getImgUrls().get(0))
                                                                .into(holder.mImage_post);
                                                    }
                                                }
                                            });
                                }
                                RequestOptions options = new RequestOptions()
                                        .placeholder(R.drawable.melomeet_logo)
                                        .centerCrop();

                                // profile image
                                Glide.with(mContext)
                                        .setDefaultRequestOptions(options)
                                        .load(mPosts.get(position).getUser().getThumb_profile_image())
                                        .thumbnail(0.1f)
                                        .into(holder.mImage_profil);

                                holder.mNom_profil.setText(mPosts.get(position).getUser().getFirstname() + " " + mPosts.get(position).getUser().getName());
                                holder.mDesc_profil.setText(mPosts.get(position).getTitle());
                                holder.mDesc_post.setText(mPosts.get(position).getContent());

                                holder.mLikes_post.setText("14 J'aime");
                                holder.mComments_post.setText("11 commentaires");
                            } else {
                                Log.d(TAG, "onComplete: error: " + task.getException());
                            }
                        }
                    });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profil)
        CircleImageView mImage_profil;
        @BindView(R.id.nom_profil)
        TextView mNom_profil;
        @BindView(R.id.desc_profil)
        TextView mDesc_profil;
        @BindView(R.id.desc_post)
        TextView mDesc_post;
        @BindView(R.id.img_post)
        ImageView mImage_post;
        @BindView(R.id.likes_post)
        TextView mLikes_post;
        @BindView(R.id.comments_post)
        TextView mComments_post;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
