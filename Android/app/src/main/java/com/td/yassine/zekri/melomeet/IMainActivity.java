package com.td.yassine.zekri.melomeet;

import com.td.yassine.zekri.melomeet.models.Post;

import java.util.ArrayList;

public interface IMainActivity {

    void inflateEditProfileFragment();

    void inflateAddPostFragment();

    void inflateFullScreenImageFragment(Object imageResource);

    void inflateChatFragment(String receiverID, String discussionID, String username);

    void onBackPressed();
}
