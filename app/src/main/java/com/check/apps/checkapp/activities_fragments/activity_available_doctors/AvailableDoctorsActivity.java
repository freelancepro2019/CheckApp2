package com.check.apps.checkapp.activities_fragments.activity_available_doctors;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.adapters.UserAdapter;
import com.check.apps.checkapp.databinding.ActivityAvailableDoctorsBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class AvailableDoctorsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityAvailableDoctorsBinding binding;
    private String lang = "en";
    private DatabaseReference dRef;
    private List<UserModel> userModelList;
    private UserAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_available_doctors);
        initView();
    }


    private void initView()
    {
        userModelList = new ArrayList<>();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userModelList,this);
        binding.recView.setAdapter(adapter);


        getDoctors();

    }

    private void getDoctors() {
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            if (userModel.getUser_type()==Tags.doctor||userModel.getUser_type()==Tags.technician)
                            {
                                if (userModel.isAvailable()){

                                    userModelList.add(userModel);
                                }
                            }
                        }
                    }


                    if (userModelList.size()>0)
                    {

                        binding.tvNoDoctors.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    }else
                        {
                            binding.tvNoDoctors.setVisibility(View.VISIBLE);

                        }

                }else
                    {
                        binding.tvNoDoctors.setVisibility(View.VISIBLE);
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
