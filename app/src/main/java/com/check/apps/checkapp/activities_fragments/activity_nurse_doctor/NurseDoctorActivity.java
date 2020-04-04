package com.check.apps.checkapp.activities_fragments.activity_nurse_doctor;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.databinding.ActivityNurseDoctorBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class NurseDoctorActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityNurseDoctorBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private DatabaseReference dRef;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nurse_doctor);
        initView();
    }

    private void initView() {

        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.setUserModel(userModel);
        binding.setBackListener(this);

        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        if (!userModel.getDoctor_id().isEmpty()) {
            binding.setDoctorName(userModel.getDoctor_name());
            binding.card.setVisibility(View.VISIBLE);
        } else {
            getCurrentUserDataById(userModel.getId());
        }


    }


    private void getCurrentUserDataById(String id) {
        binding.progBar.setVisibility(View.VISIBLE);
        dRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);

                    if (model != null) {
                        if (!model.getDoctor_id().isEmpty()) {
                            userModel = model;
                            preferences.create_update_userData(NurseDoctorActivity.this, userModel);
                            binding.setDoctorName(userModel.getDoctor_name());
                            binding.card.setVisibility(View.VISIBLE);

                        } else {
                            binding.card.setVisibility(View.GONE);
                            binding.tvNoDoctor.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    binding.card.setVisibility(View.GONE);
                    binding.tvNoDoctor.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                binding.progBar.setVisibility(View.GONE);
            }
        });

    }




    @Override
    public void back() {
        finish();
    }
}
