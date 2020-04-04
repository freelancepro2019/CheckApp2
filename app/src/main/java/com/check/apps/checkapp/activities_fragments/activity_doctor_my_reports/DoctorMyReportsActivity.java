package com.check.apps.checkapp.activities_fragments.activity_doctor_my_reports;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.adapters.DoctorMyReportsAdapter;
import com.check.apps.checkapp.databinding.ActivityDoctorMyReportsBinding;
import com.check.apps.checkapp.databinding.DialogDoctorMyReportBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.TechnicianReportModel;
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

public class DoctorMyReportsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityDoctorMyReportsBinding binding;
    private String lang = "en";
    private DatabaseReference dRef;
    private List<TechnicianReportModel> technicianReportModelList;
    private DoctorMyReportsAdapter adapter;
    private UserModel userModel;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_my_reports);
        initView();
    }




    private void initView()
    {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);

        technicianReportModelList = new ArrayList<>();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorMyReportsAdapter(technicianReportModelList,this);
        binding.recView.setAdapter(adapter);


        getReports();

    }

    private void getReports() {
        dRef.child(Tags.TABLE_TECHNICIAN_REPORTS).child(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);
                technicianReportModelList.clear();

                if (dataSnapshot.getValue()!=null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        TechnicianReportModel model = ds.getValue(TechnicianReportModel.class);

                        if (model!=null)
                        {
                           technicianReportModelList.add(model);
                        }
                    }


                    if (technicianReportModelList.size()>0)
                    {

                        binding.tvNoReports.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    }else
                    {
                        binding.tvNoReports.setVisibility(View.VISIBLE);

                    }

                }else
                {
                    binding.tvNoReports.setVisibility(View.VISIBLE);
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

    public void setItemData(TechnicianReportModel model) {

        createReportDialogAlert(model);

    }


    private void createReportDialogAlert(TechnicianReportModel model) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogDoctorMyReportBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_doctor_my_report, null, false);

        binding.setModel(model);
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss()

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }
}
