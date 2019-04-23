package com.td.yassine.zekri.melomeet.posts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.utils.SelectableAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class AttachmentRecyclerViewAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AttachmentRecyclerViewA";
    private ArrayList<String> mImages;
    private Context mContext;
    private IsAttachmentSelected mIsAttachmentsSelected;
    private IMainActivity mIMainActivity;

    public AttachmentRecyclerViewAdapter(Context context, ArrayList<String> images, IsAttachmentSelected isAttachmentSelected) {
        mContext = context;
        mImages = images;
        mIsAttachmentsSelected = isAttachmentSelected;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_attachment_list_item, parent, false);
        return new ViewHolder(view, mIsAttachmentsSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: binded.");
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.melomeet_logo)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop();

        Glide.with(mContext)
                .setDefaultRequestOptions(options)
                .load(mImages.get(position))
                .thumbnail(0.1f)
                .into(((ViewHolder) holder).image);

        ((ViewHolder) holder).attachmentOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mIMainActivity = (IMainActivity) mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.attachment_overlay)
        View attachmentOverlay;

        IsAttachmentSelected isAttachmentSelected;

        public ViewHolder(View itemView, IsAttachmentSelected isAttachmentSelected) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.isAttachmentSelected = isAttachmentSelected;
        }


        @OnClick(R.id.image)
        public void onClickImage(View view) {
            Log.d(TAG, "onClickImage: clicked.");
            if (getSelectedItemCount() > 0) {
                toggleSelection(getAdapterPosition());
                if (getSelectedItemCount() == 0) {
                    isAttachmentSelected.isSelected(false);
                }
            } else {
                mIMainActivity.inflateFullScreenImageFragment(mImages.get(getAdapterPosition()));
            }
        }

        @OnLongClick(R.id.image)
        public boolean onLongClickImage(View view) {
            Log.d(TAG, "onLongClickImage: longClicked.");
            toggleSelection(getAdapterPosition());

            if (getSelectedItemCount() > 0) {
                isAttachmentSelected.isSelected(true);
            }

            return false;
        }
    }

    public interface IsAttachmentSelected {
        void isSelected(boolean isSelected);
    }
}
