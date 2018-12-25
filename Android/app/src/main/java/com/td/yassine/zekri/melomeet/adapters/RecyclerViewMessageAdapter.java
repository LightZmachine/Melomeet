package com.td.yassine.zekri.melomeet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.messages.ChatFragment;
import com.td.yassine.zekri.melomeet.messages.MessagesActivity;
import com.td.yassine.zekri.melomeet.utils.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewMessageAdapter extends RecyclerView.Adapter<RecyclerViewMessageAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewGalleryProf";

    private Context mContext;

    public RecyclerViewMessageAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mImage_profil.setImageResource(R.drawable.test_pic_merveil);
        holder.mTv_date.setText("16:30");
        holder.mTv_Message.setText("Hello World.");
        holder.mTv_nomProfil.setText("Merveil Nicador DILON");

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: HEY mother fucker");
                Fragment fragment = new ChatFragment();
                switchContent(R.id.container, fragment);
            }
        });


    }


    public void switchContent(int id, Fragment fragment) {
        if (mContext == null)
            return;
        if (mContext instanceof MessagesActivity) {
            MessagesActivity mainActivity = (MessagesActivity) mContext;
            Fragment frag = fragment;
            mainActivity.switchContent(id, frag);
        }

    }

    @Override
    public int getItemCount() {
        return 6;
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
