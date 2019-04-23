package com.td.yassine.zekri.melomeet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSpinnerAdapter extends ArrayAdapter<User> {

    private Context mContext;

    public UserSpinnerAdapter(@NonNull Context context, ArrayList<User> usersList) {
        super(context, 0, usersList);

        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_spinner_row, parent, false);
        }

        CircleImageView userImage = convertView.findViewById(R.id.image_profil);
        TextView tvUsername = convertView.findViewById(R.id.tv_username);

        User currentItem = getItem(position);
        if (currentItem != null) {

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.melomeet_logo)
                    .centerInside()
                    .override(100, 100);
            Glide.with(mContext)
                    .setDefaultRequestOptions(options)
                    .load(currentItem.getThumb_profile_image())
                    .into(userImage);
            tvUsername.setText(currentItem.getFirstname() + " " + currentItem.getName());
        }

        return convertView;
    }
}
