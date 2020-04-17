package com.check.apps.checkapp.activities_fragments.activity_forget_password;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_login.LoginActivity;
import com.check.apps.checkapp.activities_fragments.activity_new_password.NewPasswordActivity;
import com.check.apps.checkapp.databinding.ActivityChangePasswordBinding;
import com.check.apps.checkapp.databinding.ActivityForgetPasswordBinding;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.share.Common;
import com.check.apps.checkapp.tags.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.paperdb.Paper;

public class ForgetPasswordActivity extends AppCompatActivity {
    private ActivityForgetPasswordBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private Preferences preferences;
    private UserModel userModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }


    private boolean isValidate() {

        String email = binding.edtEmail.getText().toString().trim();


        if (!email.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {

            Common.CloseKeyBoard(this, binding.edtEmail);
            return true;

        } else {
            if (email.isEmpty()) {
                Toast.makeText(this, R.string.email_req, Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, R.string.inv_email, Toast.LENGTH_SHORT).show();

            }

            return false;
        }

    }

    public void getEmails(View view) {
        if (isValidate()) {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String email="";
                        String uEmail="";
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            UserModel userModel = ds.getValue(UserModel.class);


                            if (userModel != null) {

                                email = binding.edtEmail.getText().toString().trim();

                                uEmail = userModel.getEmail();
                                Log.e("email", email);
                                Log.e("uemail", uEmail);
                                if (email.equals(uEmail)) {
                                   /* FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ForgetPasswordActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ForgetPasswordActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });*/
                                    Intent intent = new Intent(ForgetPasswordActivity.this, NewPasswordActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                }else {
                                    dialog.dismiss();
                                }

                            } else {
                                dialog.dismiss();
                                Toast.makeText(ForgetPasswordActivity.this, "Email not recorded in DB", Toast.LENGTH_SHORT).show();
                            }
                        }



                    } else {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
