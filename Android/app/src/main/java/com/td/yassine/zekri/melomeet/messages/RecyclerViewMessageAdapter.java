package com.td.yassine.zekri.melomeet.messages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.models.ChatMessage;
import com.td.yassine.zekri.melomeet.models.Discussion;
import com.td.yassine.zekri.melomeet.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewMessageAdapter extends RecyclerView.Adapter<RecyclerViewMessageAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewGalleryProf";

    private Context mContext;
    private IMainActivity mInterface;
    private ArrayList<Discussion> mDiscussionsList;
    private FirebaseFirestore mDb;
    private FirebaseAuth mFirebaseAuth;
    private ChatMessage mCurrentChatMessage;
    private String mUsername = "";


    public RecyclerViewMessageAdapter(Context context, ArrayList<Discussion> discussions) {
        mContext = context;
        mDiscussionsList = discussions;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mInterface = (IMainActivity) mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        mDb = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        mDb.collection(mContext.getString(R.string.collection_users))
                .document(mFirebaseAuth.getCurrentUser().getUid())
                .collection(mContext.getString(R.string.collection_discussions))
                .document(mDiscussionsList.get(position).getDiscussionID())
                .collection(mContext.getString(R.string.collection_messages))
                .orderBy("date_created", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()) {
                                if (snapshot.getType() == DocumentChange.Type.ADDED || snapshot.getType() == DocumentChange.Type.MODIFIED) {
                                    mCurrentChatMessage = snapshot.getDocument().toObject(ChatMessage.class);
                                }
                            }

                            mDb.collection(mContext.getString(R.string.collection_users))
                                    .document(mCurrentChatMessage.getAuthorID())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        User author = task.getResult().toObject(User.class);
                                        boolean isItMe = mFirebaseAuth.getCurrentUser().getUid().equals(author.getId());
                                        if (!isItMe) {
                                            String imgURL = author.getThumb_profile_image();
                                            RequestOptions options = new RequestOptions()
                                                    .placeholder(R.drawable.melomeet_logo)
                                                    .centerInside()
                                                    .override(100, 100);
                                            Glide.with(mContext)
                                                    .setDefaultRequestOptions(options)
                                                    .load(imgURL)
                                                    .into(holder.mImage_profil);

                                            holder.mTv_nomProfil.setText(mCurrentChatMessage.getAuthor());
                                            mUsername = mCurrentChatMessage.getAuthor();
                                        } else {
                                            mDb.collection(mContext.getString(R.string.collection_users))
                                                    .document(mCurrentChatMessage.getReceiverID())
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        User receiverUser = task.getResult().toObject(User.class);
                                                        String imgURL = receiverUser.getThumb_profile_image();
                                                        RequestOptions options = new RequestOptions()
                                                                .placeholder(R.drawable.melomeet_logo)
                                                                .centerInside()
                                                                .override(100, 100);
                                                        Glide.with(mContext)
                                                                .setDefaultRequestOptions(options)
                                                                .load(imgURL)
                                                                .into(holder.mImage_profil);

                                                        holder.mTv_nomProfil.setText(receiverUser.getFirstname() + " " + receiverUser.getName());
                                                        mUsername = receiverUser.getFirstname() + " " + receiverUser.getName();
                                                    } else {
                                                        Log.d(TAG, "onComplete: error: " + task.getException());
                                                    }
                                                }
                                            });
                                        }

                                        if (mCurrentChatMessage.getDate_created() != null) { // Sometimes timestamp might be null at the creation due to latency
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(mCurrentChatMessage.getDate_created());
                                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                            int min = calendar.get(Calendar.MINUTE);
                                            String currentTime = String.format("%02d:%02d", hours, min);
                                            holder.mTv_date.setText(currentTime);
                                        } else {
                                            holder.mTv_date.setText("Now");
                                        }

                                        holder.mTv_Message.setText(mCurrentChatMessage.getMessage());

                                        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String receiverID = mCurrentChatMessage.getReceiverID();
                                                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(receiverID)) {
                                                    receiverID = mCurrentChatMessage.getAuthorID();
                                                }

                                                Log.d(TAG, "onClick: receiverID: " + receiverID);
                                                String discussionID = mCurrentChatMessage.getDiscussionID();
                                                mInterface.inflateChatFragment(receiverID, discussionID, mUsername);
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "onComplete: error: " + task.getException());
                                    }
                                }
                            });
                        }
                    }
                });


    }

    @Override
    public int getItemCount() {
        return mDiscussionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profil)
        CircleImageView mImage_profil;
        @BindView(R.id.tv_date)
        TextView mTv_date;
        @BindView(R.id.nom_profil)
        TextView mTv_nomProfil;
        @BindView(R.id.tv_message)
        TextView mTv_Message;
        @BindView(R.id.relLayout1)
        RelativeLayout mRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
