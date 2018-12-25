package com.td.yassine.zekri.melomeet.authentification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.td.yassine.zekri.melomeet.HomeActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.model.User;
import com.td.yassine.zekri.melomeet.utils.FirebaseMethods;
import com.td.yassine.zekri.melomeet.utils.StringManipulation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class RegisterActivity extends AppCompatActivity {

    //Constants
    private static final String TAG = "RegisterActivity";

    //Widgets
    @BindView(R.id.editText_birthdate)
    TextInputEditText mEditText_birthDate;
    @BindView(R.id.editText_email)
    TextInputLayout mEditText_email;
    @BindView(R.id.editText_username)
    TextInputLayout mEditText_username;
    @BindView(R.id.editText_firstname)
    TextInputLayout mEditText_firstname;
    @BindView(R.id.editText_name)
    TextInputLayout mEditText_name;
    @BindView(R.id.editText_password)
    TextInputLayout mEditText_password;
    @BindView(R.id.editText_confirmPassword)
    TextInputLayout mEditText_confirmPassword;
    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;

    private ProgressDialog mProgressDialog;

    //Variables
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private Context mContext;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        this.mContext = RegisterActivity.this;
        this.mFirebaseMethods = new FirebaseMethods(mContext, FirebaseAuth.getInstance());
        ButterKnife.bind(this);

        this.mProgressDialog = new ProgressDialog(mContext);

        initBirthDateListener();
    }


    private void initBirthDateListener() {
        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                mEditText_birthDate.setText(date);
            }
        };
    }

    @OnFocusChange(R.id.editText_birthdate)
    public void birthDateFocus(boolean hasFocus) {
        Log.d(TAG, "birthDateFocus: focused.");

        if (hasFocus) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    RegisterActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mOnDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    @OnClick(R.id.editText_birthdate)
    public void birthdateClick() {
        Log.d(TAG, "birthdateClick: Clicked.");

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                RegisterActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mOnDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @OnClick(R.id.btn_register)
    public void btnRegisterClick() {
        Log.d(TAG, "btnRegisterClick: clicked.");

        boolean cancel = false;
        View focusView = null;

        final String email = mEditText_email.getEditText().getText().toString().trim();
        final String username = mEditText_username.getEditText().getText().toString().trim();
        final String password = mEditText_password.getEditText().getText().toString().trim();
        final String confirm_password = mEditText_confirmPassword.getEditText().getText().toString().trim();
        final String birth_date = mEditText_birthDate.getText().toString().trim();
        final String name = mEditText_name.getEditText().getText().toString().trim();
        final String firstname = mEditText_firstname.getEditText().getText().toString().trim();

        mEditText_email.setError(null);
        mEditText_username.setError(null);
        mEditText_password.setError(null);
        mEditText_confirmPassword.setError(null);
        mEditText_birthDate.setError(null);
        mEditText_firstname.setError(null);
        mEditText_name.setError(null);

        if (TextUtils.isEmpty(email)) {
            mEditText_email.setError(getString(R.string.error_email));
            focusView = mEditText_email;
            cancel = true;
        } else if (!StringManipulation.isValidEmail(email)) {
            mEditText_email.setError(getString(R.string.error_email));
            focusView = mEditText_email;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mEditText_username.setError(getString(R.string.error_username));
            focusView = mEditText_username;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mEditText_name.setError(getString(R.string.error_name));
            focusView = mEditText_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstname)) {
            mEditText_firstname.setError(getString(R.string.error_firstname));
            focusView = mEditText_firstname;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mEditText_password.setError(getString(R.string.error_password));
            focusView = mEditText_password;
            cancel = true;
        } else if (!StringManipulation.isValidPassword(password)) {
            mEditText_password.setError(getString(R.string.error_password_length));
            focusView = mEditText_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirm_password)) {
            mEditText_confirmPassword.setError(getString(R.string.error_password_confirm));
            focusView = mEditText_confirmPassword;
            cancel = true;
        } else if (!password.equals(confirm_password)) {
            mEditText_confirmPassword.setError(getString(R.string.error_password_do_not_match));
            focusView = mEditText_confirmPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(birth_date)) {
            mEditText_birthDate.setError(getString(R.string.error_birth_date));
            focusView = mEditText_birthDate;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            this.mProgressDialog.setTitle(getString(R.string.progress_dialog_register_user));
            this.mProgressDialog.setMessage(getString(R.string.progress_dialog_register_user_message));
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.show();

            mFirebaseMethods.createUserWithMail(email, password, username, name, firstname, birth_date, mProgressDialog, mMainLayout);
        }
    }

}
