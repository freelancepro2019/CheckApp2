package com.check.apps.checkapp.activities_fragments.activity_doctor_patients;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_all_technician.AllTechnicianActivity;
import com.check.apps.checkapp.adapters.DoctorPatientAdapter;
import com.check.apps.checkapp.databinding.ActivityDoctorPatientsBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.AppointmentReserveModel;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class DoctorPatientsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityDoctorPatientsBinding binding;
    private String lang = "en";
    private DatabaseReference dRef;
    private UserModel userModel;
    private Preferences preferences;
    private DoctorPatientAdapter adapter;
    private List<AppointmentReserveModel> appointmentReserveModelList;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_patients);
        initView();
    }




    private void initView() {
        appointmentReserveModelList = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_RESERVATIONS);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorPatientAdapter(appointmentReserveModelList, this);
        binding.recView.setAdapter(adapter);


        getReservation(userModel.getDoctor_id());


    }

    private void getReservation(String id) {

        dRef.child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        binding.progBar.setVisibility(View.GONE);

                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                AppointmentReserveModel model = ds.getValue(AppointmentReserveModel.class);

                                //// today
                                if (model != null && model.getStatus() == Tags.reserve_accepted) {
                                    appointmentReserveModelList.add(model);
                                }


                            }

                            if (appointmentReserveModelList.size() > 0) {
                                binding.tvNoReservation.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();

                            } else {
                                binding.tvNoReservation.setVisibility(View.VISIBLE);

                            }

                        } else {
                            binding.tvNoReservation.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    @Override
    public void back() {
        finish();
    }

    public void setItemData(int adapterPosition, AppointmentReserveModel appointmentReserveModel) {

        Intent intent = new Intent(this, AllTechnicianActivity.class);
        intent.putExtra("data",appointmentReserveModel);
        startActivity(intent);

    }



}
