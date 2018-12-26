package com.td.yassine.zekri.melomeet.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewMessageAdapter;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.utils.BottomNavigationViewHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatFragment extends Fragment {

    //constants
    private static final String TAG = "ChatFragment";

    //variables
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        mContext = getActivity();

        Log.d(TAG, "onCreateView: started.");

        return view;
    }


}
