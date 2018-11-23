package com.brunogtavares.worldlandmarks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_status) TextView mStatusTextView;
    @BindView(R.id.tv_detail) TextView mDetailTextView;
    @BindView(R.id.et_fieldEmail) EditText mEmailField;
    @BindView(R.id.et_fieldPassword) EditText mPasswordField;

    @BindView(R.id.bt_verifyEmailButton) Button mVerifyEmailButton;

    @BindView(R.id.ll_emailPasswordButtons) LinearLayout mEmailPasswordButtons;
    @BindView(R.id.ll_emailPasswordFields) LinearLayout mEmailPasswordFields;
    @BindView(R.id.ll_signedInButtons) LinearLayout mSignedInButtons;

    private FirebaseAuth mAuth;


    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        ButterKnife.bind(this);

        // Buttons
        findViewById(R.id.bt_emailSignInButton).setOnClickListener(this);
        findViewById(R.id.bt_emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.bt_signOutButton).setOnClickListener(this);
        mVerifyEmailButton.setOnClickListener(this);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) goToMainActivity();

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Timber.d("createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d( "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.w( "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.bt_emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.bt_signOutButton) {
            signOut();
        } else if (i == R.id.bt_verifyEmailButton) {
            sendEmailVerification();
        }
    }

    private void updateUI(FirebaseUser user) {

        hideProgressDialog();

        if(user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mEmailPasswordButtons.setVisibility(View.GONE);
            mEmailPasswordFields.setVisibility(View.GONE);
            mSignedInButtons.setVisibility(View.VISIBLE);

            mVerifyEmailButton.setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            mEmailPasswordButtons.setVisibility(View.VISIBLE);
            mEmailPasswordFields.setVisibility(View.VISIBLE);
            mSignedInButtons.setVisibility(View.GONE);
        }
    }

    private void signIn(String email, String password) {
        Timber.d( "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.d("signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void goToMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        mVerifyEmailButton.setEnabled(false);

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // Re-enable button
                        mVerifyEmailButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Timber.e("sendEmailVerification", task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showProgressDialog() {
        // TODO Improve Progress bar!
        // TODO Check for rotation!
        if (mProgressBar == null) {
            mProgressBar = new ProgressBar(this);
            mProgressBar.setIndeterminate(true);
        }

        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressDialog() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }
    }


}
