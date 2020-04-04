package com.check.apps.checkapp.activities_fragments.activity_doctor_my_reservation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_doctor_today_appointments.ActivityDoctorTodayAppointmentsActivity;
import com.check.apps.checkapp.databinding.ActivityDoctorMyReservationBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.share.Common;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class DoctorMyReservationActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityDoctorMyReservationBinding binding;
    private UserModel userModel;
    private Preferences preferences;
    private DatabaseReference dRef;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_my_reservation);
        initView();
    }

    private void initView()
    {
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.setBackListener(this);


        binding.cardToday.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActivityDoctorTodayAppointmentsActivity.class);
            intent.putExtra("type",1);
            startActivity(intent);
        });

        binding.cardAllAppointment.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActivityDoctorTodayAppointmentsActivity.class);
            intent.putExtra("type", 2);
            startActivity(intent);
        });

        binding.cardFinishedAppointments.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActivityDoctorTodayAppointmentsActivity.class);
            intent.putExtra("type", 3);
            startActivity(intent);
        });

        if (userModel.getUser_type()== Tags.doctor)
        {
            binding.llCards.setVisibility(View.VISIBLE);
            if (userModel.getNurse_id()!=null&&!userModel.getNurse_id().isEmpty())
            {
                binding.cardAllAppointment.setVisibility(View.GONE);
            }else
                {
                    getCurrentUserDataById(userModel.getId());
                }
        }else if (userModel.getUser_type()== Tags.nurse)
        {
            binding.cardToday.setVisibility(View.GONE);

            if (userModel.getDoctor_id()!=null&&!userModel.getDoctor_id().isEmpty())
            {
                binding.cardAllAppointment.setVisibility(View.VISIBLE);
                binding.cardFinishedAppointments.setVisibility(View.VISIBLE);
                binding.llCards.setVisibility(View.VISIBLE);


            }else
                {
                    getCurrentUserDataById(userModel.getId());
                }
        }
    }

    private void getCurrentUserDataById(String id)
    {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        dRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);

                    if (model != null) {

                        if (userModel.getUser_type()==Tags.doctor)
                        {
                            if (!model.getNurse_id().isEmpty()) {
                                userModel = model;
                                preferences.create_update_userData(DoctorMyReservationActivity.this, userModel);
                                binding.cardAllAppointment.setVisibility(View.GONE);

                            } else {

                                binding.cardAllAppointment.setVisibility(View.VISIBLE);

                            }

                        }else if (userModel.getUser_type()==Tags.nurse)
                            {
                                if (!model.getDoctor_id().isEmpty()) {
                                    userModel = model;
                                    preferences.create_update_userData(DoctorMyReservationActivity.this, userModel);
                                    binding.cardToday.setVisibility(View.GONE);
                                    binding.tvNoDoctor.setVisibility(View.GONE);

                                } else {

                                    binding.cardAllAppointment.setVisibility(View.GONE);
                                    binding.cardFinishedAppointments.setVisibility(View.GONE);
                                    binding.tvNoDoctor.setVisibility(View.VISIBLE);
                                }
                                binding.llCards.setVisibility(View.VISIBLE);

                            }


                    }

                } else {

                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.dismiss();
            }
        });

    }


    @Override
    public void back() {
        finish();
    }

}

