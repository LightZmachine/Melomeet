package com.td.yassine.zekri.melomeet.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.td.yassine.zekri.melomeet.HomeActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.authentification.LoginActivity;
import com.td.yassine.zekri.melomeet.authentification.RegisterActivity;
import com.td.yassine.zekri.melomeet.model.User;

import java.util.Map;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private User mUser;

    public FirebaseMethods(Context context, FirebaseAuth auth) {
        this.mContext = context;
        this.mAuth = auth;
        this.mDb = FirebaseFirestore.getInstance();
        this.mUser = new User();
    }

    public void signInUserWithMail(final String email, final String password, final ProgressDialog progressDialog, final View view) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: sign In success.");
                            Intent i = new Intent(mContext, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(i);
                        } else {
                            Log.d(TAG, "onComplete: sign In failed.");
                            Snackbar.make(view, "Something wrong happened, try again.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, final ProgressDialog progressDialog, final View view) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressDialog.setTitle(mContext.getString(R.string.progress_dialog_login_user));
        progressDialog.setMessage(mContext.getString(R.string.progress_dialog_login_user_message));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Intent i = new Intent(mContext, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void createUserWithMail(final String email, final String password, final String username, final String name, final String firstname, final String birth_date, final ProgressDialog progressDialog, final View view) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: createUserWithEmail: success.");
                            saveNewUserInDatabase(email, username, name, firstname, birth_date, progressDialog, view);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: createUserWithEmail: failed: " + e.getMessage());
                        Snackbar.make(view, "Something wrong happened, try again.", Snackbar.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void saveNewUserInDatabase(String email, final String username, final String name, final String firstname, String birth_date, final ProgressDialog progressDialog, final View view) {

        String userID = mAuth.getCurrentUser().getUid();

        mUser.setEmail(email);
        mUser.setUsername(username);
        mUser.setBirth_date(birth_date);
        mUser.setId(userID);
        mUser.setName(name);
        mUser.setFirstname(firstname);

        DocumentReference docRef = this.mDb.collection(mContext.getString(R.string.collection_users)).document(userID);

        docRef.set(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: upload sucess, redirect to home Activity.");
                Snackbar.make(view, "Register success. Thanks, welcome " + username, Snackbar.LENGTH_SHORT).show();
                Intent i = new Intent(mContext, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(i);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: upload FAILED !");
                Snackbar.make(view, "Something wrong happened, try again.", Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}
