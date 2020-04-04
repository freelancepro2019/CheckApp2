package com.check.apps.checkapp.activities_fragments.activity_report;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.adapters.ReportAdapter;
import com.check.apps.checkapp.databinding.ActivityReportBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.ReportModel;
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

public class ReportActivity extends AppCompatActivity implements Listeners.BackListener  {
    private ActivityReportBinding binding;
    private String lang = "en";
    private DatabaseReference dRef;
    private List<ReportModel> reportModelList;
    private Preferences preferences;
    private UserModel userModel;
    private ReportAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        initView();

    }


    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        reportModelList = new ArrayList<>();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_REPORTS);
        binding.setLang(lang);
        binding.setBackListener(this);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(reportModelList,this);
        binding.recView.setAdapter(adapter);

        getReports();


    }


    private void getReports()
    {
        dRef.child(userModel.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        binding.progBar.setVisibility(View.GONE);
                        if (dataSnapshot.getValue()!=null)
                        {

                            for (DataSnapshot ds:dataSnapshot.getChildren())
                            {
                                ReportModel model = ds.getValue(ReportModel.class);
                                if (model!=null)
                                {
                                    reportModelList.add(model);
                                }
                            }

                            if (reportModelList.size()>0)
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


}
