package com.td.yassine.zekri.melomeet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.td.yassine.zekri.melomeet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewHomeFeedAdapter extends RecyclerView.Adapter<RecyclerViewHomeFeedAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewHomeFeedAda";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_home_feedback, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mImage_profil.setImageResource(R.drawable.test_pic_merveil);
        holder.mNom_profil.setText("Merveil Nicador DILON");
        holder.mDesc_profil.setText("I like turtles.");
        holder.mDesc_post.setText(R.string.lorem_low);
        holder.mImage_post.setImageResource(R.drawable.starcitizen_placeholder);
        holder.mLikes_post.setText("14 J'aime");
        holder.mComments_post.setText("11 commentaires");

    }

    @Override
    public int getItemCount() {
        return 8;
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
