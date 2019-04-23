package com.td.yassine.zekri.melomeet.messages;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.adapters.RecyclerViewChatAdapter;
import com.td.yassine.zekri.melomeet.models.ChatMessage;
import com.td.yassine.zekri.melomeet.models.User;
import com.td.yassine.zekri.melomeet.utils.UtilsMethods;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatFragment extends Fragment {

    //constants
    private static final String TAG = "ChatFragment";
    //widgets
    @BindView(R.id.editText_message)
    EditText mEditTextMsg;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_username)
    TextView mUsername;
    //variables
    private Context mContext;
    private IMainActivity mInterface;
    private User mUser;
    private ArrayList<ChatMessage> mMessages;
    private String mDiscussionID;
    private String mReceiverID;
    private RecyclerViewChatAdapter mAdapter;
    private ListenerRegistration mListenerRegistration;
    private FirebaseFirestore mDB;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListenerRegistration != null) {
            mListenerRegistration.remove();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started.");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        mContext = getActivity();
        mDB = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mUser = bundle.getParcelable(getString(R.string.bundle_object_user));
            mReceiverID = bundle.getString(getString(R.string.bundle_receiver_id));
            mDiscussionID = bundle.getString(getString(R.string.bundle_discussion_id));
            mUsername.setText(bundle.getString(getString(R.string.bundle_username)));
        }

        mMessages = new ArrayList<>();
        initRecyclerView();

        mListenerRegistration = mDB.collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(getString(R.string.collection_discussions))
                .document(mDiscussionID)
                .collection(getString(R.string.collection_messages))
                .orderBy("date_created", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "onEvent: error: " + e.getMessage());
                            return;
                        }
                        getAllMessages(queryDocumentSnapshots);
                    }
                });

        return view;
    }

    private void getAllMessages(QuerySnapshot querySnapshot) {
        if (!querySnapshot.isEmpty()) {
            for (DocumentChange snapshot : querySnapshot.getDocumentChanges()) {
                if (snapshot.getType() == DocumentChange.Type.ADDED) {
                    mMessages.add(snapshot.getDocument().toObject(ChatMessage.class));
                    mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    private void initRecyclerView() {
        mAdapter = new RecyclerViewChatAdapter(getActivity(), mMessages);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @OnClick(R.id.iv_backArrow)
    public void ivBackArrowClicked(View view) {
        Log.d(TAG, "ivBackArrowClicked: clicked.");
        mInterface.onBackPressed();
    }

    @OnClick(R.id.iv_sendMsg)
    public void sendMsgClicked(View view) {
        Log.d(TAG, "sendMsgClicked: clicked.");

        String message = mEditTextMsg.getText().toString().trim();

        if (message.equals("")) {
            mEditTextMsg.setError("Write something before sending a msg, thanks.");
            mEditTextMsg.requestFocus();
            return;
        }

        UtilsMethods.hideSoftKeyboard(getActivity());
        mEditTextMsg.setText("");

        ChatMessage newMessage = new ChatMessage();
        newMessage.setMessage(message);
        newMessage.setAuthor(mUser.getFirstname() + " " + mUser.getName());
        newMessage.setAuthorID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newMessage.setReceiverID(mReceiverID);
        newMessage.setDiscussionID(mDiscussionID);

        addNewMessage(newMessage);
    }

    private void addNewMessage(final ChatMessage message) {
        mDB.collection(getString(R.string.collection_users))
                .document(message.getAuthorID())
                .collection(getString(R.string.collection_discussions))
                .document(message.getDiscussionID())
                .collection(getString(R.string.collection_messages))
                .add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            mDB.collection(getString(R.string.collection_users))
                                    .document(message.getReceiverID())
                                    .collection(getString(R.string.collection_discussions))
                                    .document(message.getDiscussionID())
                                    .collection(getString(R.string.collection_messages))
                                    .add(message)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {

                                            } else {
                                                Log.d(TAG, "onComplete: error: " + task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Log.d(TAG, "onComplete: error: " + task.getException());
                        }
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }
}
