package com.check.apps.checkapp.activities_fragments.activity_login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_doctor_home.HomeDoctorActivity;
import com.check.apps.checkapp.activities_fragments.activity_fingerprint.FingerPrintActivity;
import com.check.apps.checkapp.activities_fragments.activity_main.MainActivity;
import com.check.apps.checkapp.activities_fragments.activity_nurse_home.NurseHomeActivity;
import com.check.apps.checkapp.activities_fragments.activity_patient_home.HomePatientActivity;
import com.check.apps.checkapp.activities_fragments.activity_sign_up.SignUpActivity;
import com.check.apps.checkapp.activities_fragments.activity_technician_home.TechnicianHomeActivity;
import com.check.apps.checkapp.databinding.ActivityLoginBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.LoginModel;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.share.Common;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements Listeners.LoginListener {
    private ActivityLoginBinding binding;
    private Preferences preferences;
    private LoginModel loginModel;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private UserModel userModel = null;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }

    private void initView() {
        preferences = Preferences.newInstance();
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME);
        loginModel = new LoginModel();
        binding.setModel(loginModel);
        binding.setLoginListener(this);
        binding.btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void checkDataLogin() {

        if (loginModel.isDataValid(this)) {
            Common.CloseKeyBoard(this, binding.edtEmail);
            login();
        }
    }

    private void login() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        mAuth.signInWithEmailAndPassword(loginModel.getEmail(), loginModel.getPassword())
                .addOnSuccessListener(authResult -> {

                    if (authResult.getUser() != null) {
                        String user_id = authResult.getUser().getUid();
                        getUserDataById(dialog, user_id);
                    }

                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage() != null) {
                Common.CreateDialogAlert(this, e.getMessage());
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getUserDataById(ProgressDialog dialog, String user_id) {

        dRef.child(Tags.TABLE_USERS).child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                dialog.dismiss();
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);

                    if (model != null) {
                        LoginActivity.this.userModel = model;
                        if (model.isFingerprint()) {
                            navigateToFingerprintActivity();
                        } else {
                            preferences.create_update_userData(LoginActivity.this, userModel);

                            if (userModel.getUser_type() == Tags.patient) {
                                navigateToHomePatientActivity();

                            } else if (userModel.getUser_type() == Tags.doctor) {
                                navigateToHomeDoctorActivity();
                            } else if (userModel.getUser_type() == Tags.technician) {
                                navigateToHomeTechnicianActivity();
                            } else if (userModel.getUser_type() == Tags.nurse) {
                                navigateToHomeNurseActivity();
                            }

                        }
                    }

                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });

    }

    private void navigateToFingerprintActivity() {

        Intent intent = new Intent(this, FingerPrintActivity.class);
        startActivityForResult(intent, 100);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (userModel != null) {
                preferences.create_update_userData(this, userModel);
                if (userModel.getUser_type() == Tags.patient) {
                    navigateToHomePatientActivity();

                } else if (userModel.getUser_type() == Tags.doctor) {
                    navigateToHomeDoctorActivity();

                } else if (userModel.getUser_type() == Tags.technician) {
                    navigateToHomeTechnicianActivity();

                } else if (userModel.getUser_type() == Tags.nurse) {
                    navigateToHomeNurseActivity();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
