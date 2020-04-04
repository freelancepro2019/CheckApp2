package com.check.apps.checkapp.activities_fragments.activity_update_profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.databinding.ActivityUpdateProfileBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.share.Common;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Locale;

import io.paperdb.Paper;

public class UpdateProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityUpdateProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private DatabaseReference dRef;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        initView();
    }


    private void initView() {
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setUserModel(userModel);

        binding.btnUpdate.setOnClickListener(view -> {
            checkData();
        });


    }

    private void checkData() {
        String name = binding.edtName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String age = binding.edtAge.getText().toString().trim();
        String DOB = binding.edtDOB.getText().toString().trim();
        String city = binding.edtCity.getText().toString().trim();
        String sex = binding.edtSex.getText().toString().trim();
        String bGroup = binding.edtBGroup.getText().toString().trim();

        if (!name.isEmpty() &&
                !email.isEmpty() &&
                !phone.isEmpty() &&
                !age.isEmpty() &&
                !DOB.isEmpty() &&
                !city.isEmpty() &&
                !sex.isEmpty() &&
                !bGroup.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            binding.edtName.setError(null);
            binding.edtEmail.setError(null);
            binding.edtPhone.setError(null);
            binding.edtAge.setError(null);
            binding.edtDOB.setError(null);
            binding.edtCity.setError(null);
            binding.edtSex.setError(null);
            binding.edtBGroup.setError(null);

            Common.CloseKeyBoard(this, binding.edtName);

            userModel.setName(name);
            userModel.setEmail(email);
            userModel.setPhone(phone);
            userModel.setAge(age);
            userModel.setBirth_date(DOB);
            userModel.setCity(city);
            if (sex.equals("male")){
                userModel.setGender(1);
            }else if(sex.equals("female")) {
                userModel.setGender(2);

            }else {
                userModel.setGender(2);

            }
            userModel.setBlood_type(bGroup);
            update(userModel);
        } else {

            if (name.isEmpty()) {
                binding.edtName.setError(getString(R.string.field_req));
            } else {
                binding.edtName.setError(null);

            }


            if (email.isEmpty()) {
                binding.edtEmail.setError(getString(R.string.field_req));
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.setError(getString(R.string.inv_email));

            } else {
                binding.edtEmail.setError(null);

            }

            if (phone.isEmpty()) {
                binding.edtPhone.setError(getString(R.string.field_req));
            } else {
                binding.edtPhone.setError(null);

            }

            if (age.isEmpty()) {
                binding.edtAge.setError(getString(R.string.field_req));
            } else {
                binding.edtAge.setError(null);

            }


            if (DOB.isEmpty()) {
                binding.edtDOB.setError(getString(R.string.field_req));
            } else {
                binding.edtDOB.setError(null);

            }


            if (city.isEmpty()) {
                binding.edtCity.setError(getString(R.string.field_req));
            } else {
                binding.edtCity.setError(null);

            }

            if (sex.isEmpty()) {
                binding.edtSex.setError(getString(R.string.field_req));
            } else {
                binding.edtSex.setError(null);

            }

            if (bGroup.isEmpty()) {
                binding.edtBGroup.setError(getString(R.string.field_req));
            } else {
                binding.edtBGroup.setError(null);

            }
        }
    }

    private void update(UserModel userModel) {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();

        dRef.child(userModel.getId())
                .setValue(userModel)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful())
                    {
                        Toast.makeText(this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                        preferences.create_update_userData(this,userModel);
                        binding.setUserModel(userModel);
                        finish();

                    }
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void back() {
        finish();
    }

}
