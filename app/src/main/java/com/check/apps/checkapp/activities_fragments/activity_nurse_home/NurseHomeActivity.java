package com.check.apps.checkapp.activities_fragments.activity_nurse_home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_change_password.ChangePasswordActivity;
import com.check.apps.checkapp.activities_fragments.activity_doctor_my_reservation.DoctorMyReservationActivity;
import com.check.apps.checkapp.activities_fragments.activity_login.LoginActivity;
import com.check.apps.checkapp.activities_fragments.activity_nurse_doctor.NurseDoctorActivity;
import com.check.apps.checkapp.activities_fragments.activity_profile.ProfileActivity;
import com.check.apps.checkapp.activities_fragments.activity_update_profile.UpdateProfileActivity;
import com.check.apps.checkapp.databinding.ActivityNurseHomeBinding;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class NurseHomeActivity extends AppCompatActivity {
    private ActivityNurseHomeBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private FirebaseAuth mAuth;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"en"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nurse_home);
        initView();
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.setUserModel(userModel);

        binding.cardPersonalInfo.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
        binding.cardPersonalInfoUpdate.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateProfileActivity.class);
            startActivity(intent);
        });
        binding.cardReservation.setOnClickListener(view -> {
            Intent intent = new Intent(this, DoctorMyReservationActivity.class);
            startActivity(intent);
        });

        binding.cardViewDoctor.setOnClickListener(view -> {
            Intent intent = new Intent(this, NurseDoctorActivity.class);
            startActivity(intent);
        });
        binding.cardChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        binding.cardLogout.setOnClickListener(view -> {
            mAuth.signOut();
            preferences.clear(this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


    }
}
