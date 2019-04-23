package com.td.yassine.zekri.melomeet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AgreementFragment extends Fragment {

    //constants
    private static final String TAG = "AgreementFragment";

    //widgets
    @BindView(R.id.fragment_heading)
    TextView mFragmentHeading;

    //vars
    private IMainActivity mInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agreement, container, false);
        ButterKnife.bind(this, view);
        
        initToolbar();

        return view;
    }

    private void initToolbar() {
        Log.d(TAG, "initToolbar: initializing toolbar.");
        mFragmentHeading.setText(getString(R.string.tag_fragment_agreement));
    }

    @OnClick(R.id.back_arrow)
    public void backArrowClicked(View view) {
        Log.d(TAG, "backArrowClicked: true.");
        mInterface.onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }
}
