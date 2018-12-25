package com.td.yassine.zekri.melomeet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.utils.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecyclerViewGalleryProfilAdapter extends RecyclerView.Adapter<RecyclerViewGalleryProfilAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewGalleryProf";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_gallery_profil, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mImage_gallery.setImageResource(R.drawable.starcitizen_placeholder);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_gallery)
        SquareImageView mImage_gallery;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
