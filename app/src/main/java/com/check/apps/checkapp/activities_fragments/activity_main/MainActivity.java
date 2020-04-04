package com.check.apps.checkapp.activities_fragments.activity_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_about.AboutAppActivity;
import com.check.apps.checkapp.activities_fragments.activity_available_doctors.AvailableDoctorsActivity;
import com.check.apps.checkapp.activities_fragments.activity_doctor_home.HomeDoctorActivity;
import com.check.apps.checkapp.activities_fragments.activity_login.LoginActivity;
import com.check.apps.checkapp.activities_fragments.activity_nurse_home.NurseHomeActivity;
import com.check.apps.checkapp.activities_fragments.activity_patient_home.HomePatientActivity;
import com.check.apps.checkapp.activities_fragments.activity_technician_home.TechnicianHomeActivity;
import com.check.apps.checkapp.databinding.ActivityMainBinding;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.tags.Tags;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
    }

    private void initView() {

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);

        binding.cardSignInSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.cardService.setOnClickListener(view -> {
            Intent intent = new Intent(this, AboutAppActivity.class);
            intent.putExtra("type",2);
            startActivity(intent);
        });

        binding.cardAvailableDoctor.setOnClickListener(view -> {
            Intent intent = new Intent(this, AvailableDoctorsActivity.class);
            startActivity(intent);
        });


        if (preferences.getSession(this).equals(Tags.session_login))
        {
            if (userModel!=null)
            {
                if (userModel.getUser_type()==Tags.patient)
                {
                    navigateToHomePatientActivity();
                }else if (userModel.getUser_type()==Tags.doctor)
                {
                    navigateToHomeDoctorActivity();
                }else if (userModel.getUser_type()==Tags.nurse)
                {
                    navigateToHomeNurseActivity();
                }else if (userModel.getUser_type()==Tags.technician)
                {
                    navigateToHomeTechnicianActivity();
                }
            }

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
