package com.check.apps.checkapp.activities_fragments.activity_send_feedback;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.adapters.FeedbackAdapter;
import com.check.apps.checkapp.databinding.ActivitySendFeedbackBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.FeedbackModel;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
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

public class SendFeedbackActivity extends AppCompatActivity implements Listeners.BackListener  {
   private ActivitySendFeedbackBinding binding;
   private String lang = "en";
   private DatabaseReference dRef;
   private List<FeedbackModel> feedbackModelList;
   private Preferences preferences;
   private UserModel userModel;
   private FeedbackAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_feedback);
        initView();

    }


    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        feedbackModelList = new ArrayList<>();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_FEEDBACK);
        binding.setLang(lang);
        binding.setBackListener(this);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FeedbackAdapter(feedbackModelList,this);
        binding.recView.setAdapter(adapter);

        binding.btnSend.setOnClickListener(view -> {

            String feedback = binding.edtFeedback.getText().toString().trim();
            if (!feedback.isEmpty())
            {
                sendFeedback(feedback);
            }

        });
        getFeedback();


    }

    private void sendFeedback(String feedback) {

        Calendar calendar = Calendar.getInstance();
        String time = new SimpleDateFormat("dd/MMM/yyyy hh:mm aa", Locale.ENGLISH).format(new Date(calendar.getTimeInMillis()));
        String id = dRef.child(userModel.getId()).push().getKey();
        FeedbackModel model = new FeedbackModel(id,userModel.getName(),userModel.getId(),feedback,time);

        dRef.child(userModel.getId()).child(id)
                .setValue(model)
                .addOnSuccessListener(aVoid -> {

                    feedbackModelList.add(model);
                    adapter.notifyItemInserted(feedbackModelList.size()-1);
                    binding.tvNoFeedback.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {

                    if (e.getMessage()!=null)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFeedback()
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
                                FeedbackModel model = ds.getValue(FeedbackModel.class);
                                if (model!=null)
                                {
                                    feedbackModelList.add(model);
                                }
                            }

                            if (feedbackModelList.size()>0)
                            {
                                binding.tvNoFeedback.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }else
                                {
                                    binding.tvNoFeedback.setVisibility(View.VISIBLE);

                                }

                        }else
                            {
                                binding.tvNoFeedback.setVisibility(View.VISIBLE);

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
