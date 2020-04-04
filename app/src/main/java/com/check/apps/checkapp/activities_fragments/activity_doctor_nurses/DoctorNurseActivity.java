package com.check.apps.checkapp.activities_fragments.activity_doctor_nurses;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.databinding.ActivityDoctorNurseBinding;
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

public class DoctorNurseActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityDoctorNurseBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private DatabaseReference dRef;
    private boolean isSearch = false;
    private UserModel nurseModel = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_nurse);
        initView();
    }

    private void initView() {

        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        binding.setUserModel(userModel);
        binding.setBackListener(this);

        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        if (!userModel.getNurse_id().isEmpty()) {
            binding.setNurseName(userModel.getNurse_name());
            binding.card.setVisibility(View.VISIBLE);
            binding.consSearch.setVisibility(View.GONE);
            binding.imageAction.setImageResource(R.drawable.ic_delete);
            binding.imageAction.setVisibility(View.VISIBLE);
        } else {
            getCurrentUserDataById(userModel.getId());
        }

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    binding.btnSearch.setVisibility(View.GONE);
                } else {
                    binding.btnSearch.setVisibility(View.VISIBLE);

                }
            }
        });
        binding.btnSearch.setOnClickListener(view -> {
            String email = binding.edtSearch.getText().toString().trim();
            if (!email.isEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    searchByEmail(email);
                } else {
                    binding.edtSearch.setError(getString(R.string.inv_email));

                }
            } else {
                binding.edtSearch.setError(getString(R.string.field_req));
            }

        });

        binding.imageAction.setOnClickListener(view -> {

            if (isSearch) {
                addNurseToDoctor();
            } else {
                getNurseUserDataById(userModel.getNurse_id());
            }

        });
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
                        if (!model.getNurse_id().isEmpty()) {
                            userModel = model;
                            preferences.create_update_userData(DoctorNurseActivity.this, userModel);
                            binding.setNurseName(userModel.getNurse_name());
                            binding.card.setVisibility(View.VISIBLE);
                            binding.imageAction.setImageResource(R.drawable.ic_delete);
                            binding.imageAction.setVisibility(View.VISIBLE);
                            binding.tvNoNurse.setVisibility(View.GONE);
                            binding.consSearch.setVisibility(View.GONE);
                        } else {
                            binding.card.setVisibility(View.GONE);
                            binding.tvNoNurse.setVisibility(View.VISIBLE);
                            binding.consSearch.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    binding.card.setVisibility(View.GONE);
                    binding.tvNoNurse.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                binding.progBar.setVisibility(View.GONE);
            }
        });

    }

    private void getNurseUserDataById(String id) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        dRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.e("1","1");
                    UserModel model = dataSnapshot.getValue(UserModel.class);

                    if (model != null) {

                        nurseModel = model;

                        deleteNurseFromDoctor(dialog);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchByEmail(String email) {
        binding.progBar.setVisibility(View.VISIBLE);
        binding.card.setVisibility(View.GONE);
        binding.tvNoNurse.setVisibility(View.GONE);
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);
                boolean isFound = false;
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserModel model = ds.getValue(UserModel.class);
                        if (model != null) {
                            if (model.getUser_type() == Tags.nurse && model.getEmail().equals(email) && model.isAvailable()) {
                                binding.setNurseName(model.getName());
                                binding.card.setVisibility(View.VISIBLE);
                                binding.imageAction.setImageResource(R.drawable.ic_plus);
                                isSearch = true;
                                nurseModel = model;
                                isFound = true;
                                break;
                            }
                        }
                    }

                    if (!isFound) {
                        binding.tvNoNurse.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.tvNoNurse.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addDoctorToNurse(ProgressDialog dialog) {
        nurseModel.setDoctor_id(userModel.getId());
        nurseModel.setDoctor_name(userModel.getDoctor_name());
        dRef.child(nurseModel.getId()).setValue(nurseModel)
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    binding.consSearch.setVisibility(View.GONE);
                    binding.imageAction.setImageResource(R.drawable.ic_delete);
                    binding.imageAction.setVisibility(View.VISIBLE);
                    isSearch = false;
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addNurseToDoctor() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        userModel.setNurse_id(nurseModel.getNurse_id());
        userModel.setNurse_name(nurseModel.getNurse_name());
        dRef.child(userModel.getId())
                .setValue(userModel)
                .addOnSuccessListener(aVoid -> {
                    preferences.create_update_userData(this, userModel);
                    addDoctorToNurse(dialog);
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDoctorFromNurse(ProgressDialog dialog) {
        nurseModel.setDoctor_id("");
        nurseModel.setDoctor_name("");

        dRef.child(nurseModel.getId())
                .setValue(nurseModel)
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    binding.card.setVisibility(View.GONE);
                    binding.consSearch.setVisibility(View.VISIBLE);
                    binding.tvNoNurse.setVisibility(View.VISIBLE);

                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNurseFromDoctor(ProgressDialog dialog) {
        userModel.setNurse_id("");
        userModel.setNurse_name("");

        dRef.child(userModel.getId())
                .setValue(userModel)
                .addOnSuccessListener(aVoid -> {
                    Log.e("2","2");

                    preferences.create_update_userData(this, userModel);
                    deleteDoctorFromNurse(dialog);
                }).addOnFailureListener(e -> {

            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void back() {
        finish();
    }
}
