package com.check_app.apps.check.activities_fragments.activity_about;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.check_app.apps.check.R;
import com.check_app.apps.check.databinding.ActivityAboutAppBinding;
import com.check_app.apps.check.interfaces.Listeners;
import com.check_app.apps.check.language.LanguageHelper;
import com.check_app.apps.check.tags.Tags;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import io.paperdb.Paper;

public class AboutAppActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityAboutAppBinding binding;
    private String lang;
    private int type;
    private DatabaseReference dRef;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_app);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("type"))
        {
            type = intent.getIntExtra("type",0);

        }
    }


    private void initView()
    {
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_SETTINGS);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        if (type==1)
        {
            binding.setTitle(getString(R.string.terms_and_conditions));
        }else if (type ==2)
        {
            binding.setTitle(getString(R.string.about_app));

        }


        getAppData();

    }

    private void getAppData() {
        DatabaseReference databaseReference ;
        if (type==1)
        {
            if (lang.equals("ar"))
            {
                databaseReference = dRef.child(Tags.TABLE_TERMS).child("ar_terms");

            }else {
                databaseReference = dRef.child(Tags.TABLE_TERMS).child("en_terms");

            }
        }else {

            if (lang.equals("ar"))
            {
                databaseReference = dRef.child(Tags.TABLE_ABOUT).child("ar_about");

            }else {
                databaseReference = dRef.child(Tags.TABLE_ABOUT).child("en_about");

            }

        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);

                if (dataSnapshot.getValue()!=null)
                {
                    binding.setContent(dataSnapshot.getValue().toString());
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
