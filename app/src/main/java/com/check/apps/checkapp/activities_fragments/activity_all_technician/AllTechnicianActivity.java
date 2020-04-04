package com.check.apps.checkapp.activities_fragments.activity_all_technician;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.adapters.TechnicianAdapter;
import com.check.apps.checkapp.databinding.ActivityAllTechnicianBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.AppointmentReserveModel;
import com.check.apps.checkapp.models.TechnicianReportModel;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.share.Common;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class AllTechnicianActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityAllTechnicianBinding binding;
    private String lang = "en";
    private DatabaseReference dRef;
    private List<UserModel> userModelList;
    private TechnicianAdapter adapter;
    private AppointmentReserveModel appointmentReserveModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_technician);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("data"))
        {
            appointmentReserveModel = (AppointmentReserveModel) intent.getSerializableExtra("data");
        }
    }


    private void initView()
    {
        userModelList = new ArrayList<>();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TechnicianAdapter(userModelList,this);
        binding.recView.setAdapter(adapter);


        getTechnician();

    }

    private void getTechnician() {
        dRef.child(Tags.TABLE_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);
                userModelList.clear();

                if (dataSnapshot.getValue()!=null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        UserModel userModel = ds.getValue(UserModel.class);

                        if (userModel!=null)
                        {
                            if (userModel.getUser_type()==Tags.technician)
                            {
                                if (userModel.isAvailable()){

                                    userModelList.add(userModel);
                                }
                            }
                        }
                    }


                    if (userModelList.size()>0)
                    {

                        binding.tvNoTech.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    }else
                    {
                        binding.tvNoTech.setVisibility(View.VISIBLE);

                    }

                }else
                {
                    binding.tvNoTech.setVisibility(View.VISIBLE);
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

    public void setItemData(UserModel model) {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        String id = dRef.child(model.getId()).push().getKey();
        String time = new SimpleDateFormat("dd/MMM/yyyy hh:mm aa",Locale.ENGLISH).format(new Date(Calendar.getInstance().getTimeInMillis()));
        TechnicianReportModel reportModel = new TechnicianReportModel(id,model.getId(),model.getName(),appointmentReserveModel.getDoctor_id(),appointmentReserveModel.getDoctor_name(),appointmentReserveModel.getUser_id(),appointmentReserveModel.getUser_name(),"",time);

        dRef.child(Tags.TABLE_TECHNICIAN_REPORTS).child(model.getId()).child(id)
                .setValue(reportModel)
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    Toast.makeText(this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    if (e.getMessage()!=null)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }else
                        {
                            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                });
    }
}
