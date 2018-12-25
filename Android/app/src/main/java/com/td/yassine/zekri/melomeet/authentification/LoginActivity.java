package com.td.yassine.zekri.melomeet.authentification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.td.yassine.zekri.melomeet.HomeActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.utils.FirebaseMethods;
import com.td.yassine.zekri.melomeet.utils.UtilsMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    @BindView(R.id.google_sign_in_button)
    Button mGoogleSignInButton;

    @BindView(R.id.editText_email)
    EditText mEditText_email;
    @BindView(R.id.editText_password)
    EditText mEditText_password;
    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;

    private ProgressDialog mProgressDialog;

    //Variables
    private Context mContext;
    private FirebaseMethods mFirebaseMethods;
    //Firebase
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.mContext = LoginActivity.this;

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseMethods = new FirebaseMethods(mContext, mAuth);
        this.mProgressDialog = new ProgressDialog(mContext);

        //User press enter on editText password.
        mEditText_password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (keycode == KeyEvent.KEYCODE_ENTER)) {
                    UtilsMethods.hideKeyboardFrom(LoginActivity.this, getCurrentFocus());
                    onTvSignInClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mFirebaseMethods.firebaseAuthWithGoogle(account, mProgressDialog, mMainLayout);
            } catch (ApiException e) {
                Log.w(TAG, "onActivityResult: Google sign in failed.", e);
            }
        }
    }


    @OnClick(R.id.google_sign_in_button)
    public void onGoogleSignInButtonClick() {
        signInWithGoogle();
    }

    @OnClick(R.id.tv_register)
    public void onTvRegisterClick() {
        Log.d(TAG, "onTvRegisterClick: moving to register activity.");
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.tv_signin)
    public void onTvSignInClick() {

        boolean cancel = false;
        View focusView = null;
        String email = mEditText_email.getText().toString().trim();
        String password = mEditText_password.getText().toString().trim();

        mEditText_email.setError(null);
        mEditText_password.setError(null);

        if (TextUtils.isEmpty(email)) {
            mEditText_email.setError(getString(R.string.error_email));
            focusView = mEditText_email;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mEditText_password.setError(getString(R.string.error_password));
            focusView = mEditText_password;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            this.mProgressDialog.setTitle(getString(R.string.progress_dialog_login_user));
            this.mProgressDialog.setMessage(getString(R.string.progress_dialog_login_user_message));
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.show();

            mFirebaseMethods.signInUserWithMail(email, password, mProgressDialog, mMainLayout);
        }
    }

}
