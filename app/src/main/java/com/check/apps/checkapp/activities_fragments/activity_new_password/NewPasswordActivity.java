package com.check.apps.checkapp.activities_fragments.activity_new_password;

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
import com.check.apps.checkapp.activities_fragments.activity_doctor_home.HomeDoctorActivity;
import com.check.apps.checkapp.activities_fragments.activity_nurse_home.NurseHomeActivity;
import com.check.apps.checkapp.activities_fragments.activity_patient_home.HomePatientActivity;
import com.check.apps.checkapp.activities_fragments.activity_technician_home.TechnicianHomeActivity;
import com.check.apps.checkapp.databinding.ActivityChangePasswordBinding;
import com.check.apps.checkapp.databinding.ActivityNewPasswordBinding;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class NewPasswordActivity extends AppCompatActivity {
    private ActivityNewPasswordBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private Preferences preferences;
    private UserModel userModel;
    private String email;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_password);
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDataFromIntent();
        initView();
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            email = intent.getStringExtra("email");

        }

    }

    public void resetPassword(View view) {
        if (isValidate()) {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();


            String password = binding.edtPassword.getText().toString().trim();

            Log.e("emmmmmail", email);
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                userModel.setPassword(password);

                                Toast.makeText(NewPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();

                                // updateUserData(userModel,dialog);
                            } else {
                                Log.e("passsssword", "Failed to send reset email!");

                                dialog.dismiss();
                                Toast.makeText(NewPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


    }


    private void updateUserData(UserModel userModel, ProgressDialog dialog) {
        Log.e("ccccccccccc", "dddddd");
        dRef.child(Tags.TABLE_USERS).child(userModel.getId())
                .setValue(userModel)
                .addOnSuccessListener(aVoid -> {
                    preferences.create_update_userData(NewPasswordActivity.this, userModel);
                    dialog.dismiss();
                    Toast.makeText(this, R.string.suc, Toast.LENGTH_SHORT).show();
                    if (userModel.getUser_type() == Tags.patient) {
                        navigateToHomePatientActivity();

                    } else if (userModel.getUser_type() == Tags.doctor) {
                        navigateToHomeDoctorActivity();
                    } else if (userModel.getUser_type() == Tags.technician) {
                        navigateToHomeTechnicianActivity();
                    } else if (userModel.getUser_type() == Tags.nurse) {
                        navigateToHomeNurseActivity();
                    }
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

        String password = binding.edtPassword.getText().toString().trim();
        String re_password = binding.edtRePassword.getText().toString().trim();

        if (
                password.length() >= 10 &&
                        re_password.length() >= 10
        ) {

            Common.CloseKeyBoard(this, binding.edtPassword);
            return true;

        } else {

            if (password.isEmpty()) {
                Toast.makeText(this, R.string.pass_req, Toast.LENGTH_SHORT).show();

            } else if (password.length() < 10) {
                Toast.makeText(this, R.string.pass_to_short, Toast.LENGTH_SHORT).show();

            }

            if (re_password.isEmpty()) {
                Toast.makeText(this, R.string.new_pas_req, Toast.LENGTH_SHORT).show();

            } else if (re_password.length() < 10) {
                Toast.makeText(this, R.string.pass_to_short, Toast.LENGTH_SHORT).show();

            } else if (!password.equals(re_password)) {
                Toast.makeText(this, R.string.pass_not_eq, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

    }

    private void navigateToHomePatientActivity() {

        Intent intent = new Intent(this, HomePatientActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeDoctorActivity() {
        Intent intent = new Intent(this, HomeDoctorActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeNurseActivity() {
        Intent intent = new Intent(this, NurseHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeTechnicianActivity() {
        Intent intent = new Intent(this, TechnicianHomeActivity.class);
        startActivity(intent);
        finish();
    }
}
