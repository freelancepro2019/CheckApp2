package com.check.apps.checkapp.activities_fragments.activity_change_password;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.databinding.ActivityChangePasswordBinding;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.share.Common;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private Preferences preferences;
    private UserModel userModel;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"en"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }

    public void resetPassword(View view) {
        if (isValidate()) {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            FirebaseUser firebaseUser = mAuth.getCurrentUser();

            if (firebaseUser != null) {
                String email = binding.edtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();
                String new_password = binding.edtRePassword.getText().toString().trim();


                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                firebaseUser.reauthenticate(credential)
                        .addOnSuccessListener(aVoid -> {

                            firebaseUser.updatePassword(new_password)
                                    .addOnSuccessListener(aVoid1 -> {

                                        userModel.setPassword(new_password);


                                        updateUserData(userModel,dialog);
                                    }).addOnFailureListener(e -> {
                                dialog.dismiss();
                                if (e.getMessage() != null) {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    if (e.getMessage() != null) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


    }


    private void updateUserData(UserModel userModel, ProgressDialog dialog) {

        dRef.child(Tags.TABLE_USERS).child(userModel.getId())
                .setValue(userModel)
                .addOnSuccessListener(aVoid -> {
                    preferences.create_update_userData(ChangePasswordActivity.this,userModel);
                    dialog.dismiss();
                    Toast.makeText(this, R.string.suc, Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidate() {

        String email = binding.edtEmail.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        String new_password = binding.edtRePassword.getText().toString().trim();

        if (!email.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length() >= 6 &&
                new_password.length() >= 6
        ) {

            Common.CloseKeyBoard(this, binding.edtEmail);
            return true;

        } else {
            if (email.isEmpty()) {
                Toast.makeText(this, R.string.email_req, Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, R.string.inv_email, Toast.LENGTH_SHORT).show();

            }
            if (password.isEmpty()) {
                Toast.makeText(this, R.string.pass_req, Toast.LENGTH_SHORT).show();

            } else if (password.length() < 6) {
                Toast.makeText(this, R.string.pass_to_short, Toast.LENGTH_SHORT).show();

            }

            if (new_password.isEmpty()) {
                Toast.makeText(this, R.string.new_pas_req, Toast.LENGTH_SHORT).show();

            } else if (new_password.length() < 6) {
                Toast.makeText(this, R.string.pass_to_short, Toast.LENGTH_SHORT).show();

            }
            return false;
        }

    }

}
