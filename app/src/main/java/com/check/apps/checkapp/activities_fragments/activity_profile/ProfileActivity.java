package com.check.apps.checkapp.activities_fragments.activity_profile;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.databinding.ActivityProfileBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;

public class ProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProfileBinding binding;
    private Preferences preferences;
    private UserModel userModel;



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initView();
    }


    private void initView() {
        preferences= Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.setUserModel(userModel);
    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void back() {

        finish();
    }

}
