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
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.models.ChatMessage;
import com.td.yassine.zekri.melomeet.models.Discussion;
import com.td.yassine.zekri.melomeet.models.User;

import java.util.ArrayList;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ViewMessageFragment extends Fragment {

    //constants
    private static final String TAG = "ViewMessageFragment";

    //widgets
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    //variables
    private Context mContext;
    private User mUser;
    private Bundle mBundle;
    private IMainActivity mInterface;
    private ArrayList<Discussion> mDiscussionsList;
    private RecyclerViewMessageAdapter mRecyclerViewMessageAdapter;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_message, container, false);
        setHasOptionsMenu(true);

        Log.d(TAG, "onCreateView: started.");

        ButterKnife.bind(this, view);
        mContext = getActivity();

        mBundle = getArguments();
        if (mBundle != null) {
            mUser = mBundle.getParcelable(mContext.getString(R.string.bundle_object_user));
        }
        mDiscussionsList = new ArrayList<>();
        setupFirebaseAuth();
        initRecyclerViewMessage();
        getDiscussions();

        return view;
    }


    private void getDiscussions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(getString(R.string.collection_discussions))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot querySnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentChange doc : querySnapshot.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    mDiscussionsList.add(doc.getDocument().toObject(Discussion.class));
                                    mRecyclerViewMessageAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }

    private void initRecyclerViewMessage() {
        mRecyclerViewMessageAdapter = new RecyclerViewMessageAdapter(getActivity(), mDiscussionsList);
        mRecyclerView.setAdapter(mRecyclerViewMessageAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }


    /**
     * Setup the firebase auth
     */
    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged: true.");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in.
                    Log.d(TAG, "onAuthStateChanged: signedIn");
                } else {
                    //User is signed out.
                    Log.d(TAG, "onAuthStateChanged: user isn't signed in. Going to login activity.");

                    Intent i = new Intent(mContext, LoginActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        };
    }

//    public void updateMessagesList(ArrayList<ChatMessage> messages) {
//        if (mMessagesList != null) {
//            if (mMessagesList.size() > 0) {
//                mMessagesList.clear();
//            }
//        }
//        if (messages != null) {
//            if (messages.size() > 0) {
//                mMessagesList.addAll(messages);
//                mRecyclerViewMessageAdapter.notifyDataSetChanged();
//            }
//        }
//    }

    @OnClick(R.id.btn_addMsg)
    public void btnAddMsgClick(View view) {
        Log.d(TAG, "btnAddMsgClick: clicked.");

        NewChatDialog dialog = new NewChatDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.bundle_object_user), mUser);
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), getString(R.string.tag_dialog_new_chat));
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }
}