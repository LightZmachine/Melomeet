package com.td.yassine.zekri.melomeet.messages;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.td.yassine.zekri.melomeet.IMainActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.adapters.UserSpinnerAdapter;
import com.td.yassine.zekri.melomeet.models.ChatMessage;
import com.td.yassine.zekri.melomeet.models.Discussion;
import com.td.yassine.zekri.melomeet.models.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewChatDialog extends DialogFragment {

    //constants
    private static final String TAG = "NewChatDialog";
    //vars
    private ArrayList<User> mUsersList;
    private UserSpinnerAdapter mAdapter;
    private Context mContext;
    private User mReceiver;
    private User mCurrentUser;
    private IMainActivity mInterface;

    private FirebaseFirestore mDB;
    //widgets
    @BindView(R.id.spinner_receiver)
    Spinner mSpinnerUsers;
    @BindView(R.id.editText_message)
    EditText mEditText_message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_chat, container, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();
        mDB = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentUser = bundle.getParcelable(getString(R.string.bundle_object_user));
            initSpinner();
        } else {
            Log.d(TAG, "onCreateView: error getting bundle.");
        }
        return view;
    }

    private void initSpinner() {
        mUsersList = new ArrayList<>();
        mDB.collection(getString(R.string.collection_users)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        mUsersList.add(snapshot.toObject(User.class));
                    }
                    mAdapter = new UserSpinnerAdapter(mContext, mUsersList);
                    mSpinnerUsers.setAdapter(mAdapter);
                    mSpinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            mReceiver = (User) adapterView.getItemAtPosition(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: error getting users list: " + task.getException());
                }
            }
        });
    }

    @OnClick(R.id.tv_sendMsg)
    public void tvSendMsgClicked(View view) {
        Log.d(TAG, "tvSendMsgClicked: sending a new Message.");

        final String message = mEditText_message.getText().toString().trim();

        if (message.equals("")) {
            mEditText_message.setError("Write something before sending a new message, thanks.");
            mEditText_message.requestFocus();
            return;
        }

        final ChatMessage newMessage = new ChatMessage();
        newMessage.setMessage(message);
        newMessage.setAuthor(mCurrentUser.getFirstname() + " " + mCurrentUser.getName());
        newMessage.setAuthorID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newMessage.setReceiverID(mReceiver.getId());

        final CollectionReference ref = mDB.collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(getString(R.string.collection_discussions));
        final String newDiscussionID = ref.document().getId();

        final Discussion newDiscussion = new Discussion();
        newDiscussion.setDiscussionID(newDiscussionID);

        newMessage.setDiscussionID(newDiscussion.getDiscussionID());

        ref.document(newDiscussion.getDiscussionID()).set(newDiscussion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ref.document(newDiscussion.getDiscussionID())
                            .collection(getString(R.string.collection_messages))
                            .add(newMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                mDB.collection(getString(R.string.collection_users))
                                        .document(newMessage.getReceiverID())
                                        .collection(getString(R.string.collection_discussions))
                                        .document(newDiscussion.getDiscussionID())
                                        .set(newDiscussion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mDB.collection(getString(R.string.collection_users))
                                                    .document(newMessage.getReceiverID())
                                                    .collection(getString(R.string.collection_discussions))
                                                    .document(newDiscussion.getDiscussionID())
                                                    .collection(getString(R.string.collection_messages))
                                                    .add(newMessage)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                mEditText_message.setText("");
                                                                mUsersList.clear();
                                                                mAdapter.notifyDataSetChanged();
                                                                getDialog().dismiss();
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
    public void onStart() {
        super.onStart();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }
}
