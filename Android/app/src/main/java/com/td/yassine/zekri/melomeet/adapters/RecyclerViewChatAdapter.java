package com.td.yassine.zekri.melomeet.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.models.ChatMessage;
import com.td.yassine.zekri.melomeet.models.User;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewChatAdapter extends RecyclerView.Adapter<RecyclerViewChatAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewGalleryProf";

    private Context mContext;
    private ArrayList<ChatMessage> mMessages;

    public RecyclerViewChatAdapter(Context context, ArrayList<ChatMessage> messages) {
        mContext = context;
        mMessages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_chat_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isItMe = mMessages.get(position).getAuthorID().equals(currentUserID);
        setChatRowAppearance(isItMe, holder);
        if (mMessages.get(position).getDate_created() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mMessages.get(position).getDate_created());
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            String currentTime = String.format("%02d:%02d", hours, min);
            holder.mTv_date.setText(currentTime);
        } else {
            holder.mTv_date.setText("Now");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(mContext.getString(R.string.collection_users))
                .document(mMessages.get(position).getAuthorID())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User author = task.getResult().toObject(User.class);
                    String imgUrl = author.getThumb_profile_image();
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.melomeet_logo)
                            .centerInside()
                            .override(100, 100);
                    Glide.with(mContext)
                            .setDefaultRequestOptions(options)
                            .load(imgUrl)
                            .into(holder.mImage_profil);
                } else {
                    Log.d(TAG, "onComplete: error: " + task.getException());
                }
            }
        });

        holder.mTv_Message.setText(mMessages.get(position).getMessage());
    }

    private void setChatRowAppearance(boolean isItMe, ViewHolder holder) {
        if (isItMe) {
            holder.mRelLayout.setPadding(0, 0, 200, 0);
            holder.mTv_Message.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.mRelLayout.setPadding(200, 0, 0, 0);
            holder.mTv_Message.setBackgroundResource(R.drawable.bubble1);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relLayout)
        RelativeLayout mRelLayout;
        @BindView(R.id.image_profil)
        CircleImageView mImage_profil;
        @BindView(R.id.tv_date)
        TextView mTv_date;
        @BindView(R.id.tv_message)
        TextView mTv_Message;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
