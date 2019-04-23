package com.td.yassine.zekri.melomeet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewHomeFeedAdapter;
import com.td.yassine.zekri.melomeet.models.Post;
import com.td.yassine.zekri.melomeet.models.User;
import com.td.yassine.zekri.melomeet.posts.AddPostFragment;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //constants
    private static final String TAG = "HomeFragment";

    //Widgets
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.main_feed)
    RelativeLayout mMain_feed;
    @BindView(R.id.logo_home_middle)
    ImageView mImageView_home_middle;

    //vars
    private Context mContext;
    private User mUser;
    private Bundle mBundle;
    private IMainActivity mInterface;
    private ArrayList<Post> mAllPosts;
    private RecyclerViewHomeFeedAdapter mViewHomeFeedAdapter;
    private FirebaseFirestore mDB;

    private DocumentSnapshot mLastvisible;
    private boolean isFirstPageLoaded = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_feed, container, false);

        ButterKnife.bind(this, view);
        mContext = getActivity();

        mBundle = getArguments();
        if (mBundle != null) {
            mUser = mBundle.getParcelable(getString(R.string.bundle_object_user));
        }

        mAllPosts = new ArrayList<>();
        mDB = FirebaseFirestore.getInstance();

        mSwipeRefreshLayout.setOnRefreshListener(this);
        initRecyclerViewFeed();
        getPosts();

        return view;
    }

    private void getPosts() {
        mDB.collection(getString(R.string.collection_posts))
                .orderBy("date_created", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            if (isFirstPageLoaded) {
                                mLastvisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                mAllPosts.clear();
                            }

                            for (DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()) {
                                if (snapshot.getType() == DocumentChange.Type.ADDED) {
                                    if (isFirstPageLoaded) {
                                        mAllPosts.add(snapshot.getDocument().toObject(Post.class));
                                    } else {
                                        mAllPosts.add(0, snapshot.getDocument().toObject(Post.class));
                                    }
                                    mViewHomeFeedAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        isFirstPageLoaded = false;
                    }
                });
    }

    private void getMorePosts() {
        mDB.collection(getString(R.string.collection_posts))
                .orderBy("date_created", Query.Direction.DESCENDING)
                .startAfter(mLastvisible)
                .limit(3)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            mLastvisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                            for (DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()) {
                                if (snapshot.getType() == DocumentChange.Type.ADDED) {
                                    mAllPosts.add(snapshot.getDocument().toObject(Post.class));
                                    mViewHomeFeedAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }


    private void initRecyclerViewFeed() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mViewHomeFeedAdapter = new RecyclerViewHomeFeedAdapter(getActivity(), mAllPosts);
        mRecyclerView.setAdapter(mViewHomeFeedAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if (reachedBottom) {
                    getMorePosts();
                }
            }
        });
    }

    @OnTouch(R.id.logo_home_middle)
    public boolean logo_middleTouch(final View view, MotionEvent event) {

        Log.d(TAG, "logo_middleTouch: true.");
        final Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        final Animation rotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate);


        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(700);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(700);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                mMain_feed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeOut);
        animation.addAnimation(rotate);
        animation.addAnimation(shake);

        view.startAnimation(animation);
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }

    @OnClick(R.id.btn_add_post)
    public void btnAddPostClick() {
        Log.d(TAG, "btnAddPostClick: navigating to add Post Fragment.");
        mInterface.inflateAddPostFragment();
    }

    @Override
    public void onRefresh() {
        // call get post again here if not realtime
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
