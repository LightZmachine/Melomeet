package com.td.yassine.zekri.melomeet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.td.yassine.zekri.melomeet.R;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {

    private String mAppend;
    private ArrayList<String> imgURLS;
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;

    public GridImageAdapter(@NonNull Context context, int resource, String append, ArrayList<String> imgUrls) {
        super(context, resource, imgUrls);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
        mAppend = append;
        imgURLS = imgUrls;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imgURL = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgURL, holder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar mProgressBar;
    }
}
