package com.check.apps.checkapp.activities_fragments.activity_reserve_appointment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.adapters.DepartmentAdapter;
import com.check.apps.checkapp.adapters.DoctorAppointmentAdapter;
import com.check.apps.checkapp.adapters.UserAdapter;
import com.check.apps.checkapp.databinding.ActivityReserveAppointmentBinding;
import com.check.apps.checkapp.databinding.DialogAppointmentBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.AppointmentModel;
import com.check.apps.checkapp.models.AppointmentReserveModel;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
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

public class ReserveAppointmentActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityReserveAppointmentBinding binding;
    private String lang = "en";
    private DatabaseReference dRef,dRefAppointment;
    private List<String> departmentList;
    private DepartmentAdapter departmentAdapter;
    private List<UserModel> userModelList;
    private UserAdapter adapter;
    private int selected_pos = 0;
    private AlertDialog dialog;
    private UserModel selectedDoctorModel = null;
    private Preferences preferences;
    private UserModel userModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reserve_appointment);
        initView();
    }


    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        userModelList = new ArrayList<>();
        departmentList = new ArrayList<>();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        dRefAppointment = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_RESERVATIONS);

        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        departmentList.add("All");
        departmentList.add("Cardiology");
        departmentList.add("Pediatrics");
        departmentList.add("Hepatology");
        departmentList.add("Dermatology");
        departmentList.add("Neurology");
        departmentList.add("Ophthalmology");
        departmentList.add("Urology");
        departmentList.add("Obstetrics And Gynaecology");


        binding.recViewDepartment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        departmentAdapter = new DepartmentAdapter(departmentList, this);
        binding.recViewDepartment.setAdapter(departmentAdapter);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userModelList, this);
        binding.recView.setAdapter(adapter);

        getDoctorsByDepartment("All");

    }

    private void getDoctorsByDepartment(String department) {
        binding.tvNoDoctors.setVisibility(View.GONE);
        binding.progBar.setVisibility(View.VISIBLE);
        userModelList.clear();
        adapter.notifyDataSetChanged();

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);

                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserModel model = ds.getValue(UserModel.class);


                        if (model != null && (model.getUser_type() == Tags.doctor || model.getUser_type() == Tags.technician) && model.isAvailable()) {
                            if (department.equals("All")) {
                                userModelList.add(model);
                            } else {
                                if (model.getDepartment().equals(department)) {
                                    userModelList.add(model);

                                }
                            }
                        }
                    }

                    if (userModelList.size() > 0) {
                        binding.tvNoDoctors.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    } else {
                        binding.tvNoDoctors.setVisibility(View.VISIBLE);

                    }

                } else {
                    binding.tvNoDoctors.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setItemData(int pos) {

        if (selected_pos != pos) {
            selected_pos = pos;
            getDoctorsByDepartment(departmentList.get(pos));
        }
    }

    public void setItemDoctorData(UserModel model) {
        if (model.getAppointmentModelList() != null && model.getAppointmentModelList().size() > 0) {
            CreateAppointmentDialogAlert(model);
        } else {
            Toast.makeText(this, R.string.no_apppint_aval, Toast.LENGTH_SHORT).show();
        }

    }

    public void setAppointmentData(AppointmentModel appointmentModel) {
        if (dialog != null) {
            dialog.dismiss();
        }

        reserve(appointmentModel);

    }


    private void CreateAppointmentDialogAlert(UserModel userModel) {

        this.selectedDoctorModel = userModel;

        dialog = new AlertDialog.Builder(this)
                .create();


        DialogAppointmentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_appointment, null, false);

        binding.setModel(userModel);

        binding.recViewAppointment.setLayoutManager(new LinearLayoutManager(this));
        DoctorAppointmentAdapter adapter = new DoctorAppointmentAdapter(userModel.getAppointmentModelList(), this);
        binding.recViewAppointment.setAdapter(adapter);

        binding.btnCancel.setOnClickListener(v -> {
                    this.selectedDoctorModel = null;
                    dialog.dismiss();
                }
        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }


    private void reserve(AppointmentModel appointmentModel)
    {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();

        String id = dRefAppointment.child(selectedDoctorModel.getId()).push().getKey();
        String time = new SimpleDateFormat("dd/MMM/yyyy hh:mm aa",Locale.ENGLISH).format(new Date(Calendar.getInstance().getTimeInMillis()));
        AppointmentReserveModel model = new AppointmentReserveModel(id,time,appointmentModel.getDay(),appointmentModel.getFrom_time(),appointmentModel.getTo_time(),userModel.getId(),userModel.getName(),selectedDoctorModel.getId(),selectedDoctorModel.getName(),"",0);

        dRefAppointment.child(selectedDoctorModel.getId()).child(id)
                .setValue(model)
                .addOnSuccessListener(aVoid -> {

                    addAppointmentToUser(dialog,model);


                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    if (e.getMessage()!=null)
                    {
                        Common.CreateDialogAlert(this,e.getMessage());
                    }else
                        {
                            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                });


    }

    private void addAppointmentToUser(ProgressDialog dialog, AppointmentReserveModel model) {

        dRefAppointment.child(userModel.getId()).child(model.getId())
                .setValue(model)
                .addOnSuccessListener(aVoid -> {

                    dialog.dismiss();
                    Toast.makeText(this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                    finish();

                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage()!=null)
            {
                Common.CreateDialogAlert(this,e.getMessage());
            }else
            {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void back() {
        finish();
    }


}

