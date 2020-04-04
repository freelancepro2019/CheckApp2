package com.check.apps.checkapp.activities_fragments.activity_patient_reservation;

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
import com.check.apps.checkapp.adapters.DoctorAppointmentAdapter;
import com.check.apps.checkapp.adapters.PatientReservationAdapter;
import com.check.apps.checkapp.databinding.ActivityPatientReservationBinding;
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

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class PatientReservationActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityPatientReservationBinding binding;
    private String lang = "en";
    private DatabaseReference dRef;
    private UserModel userModel;
    private Preferences preferences;
    private PatientReservationAdapter adapter;
    private List<AppointmentReserveModel> appointmentReserveModelList;
    private AppointmentReserveModel appointmentReserveModel;
    private List<AppointmentModel> appointmentModelList;
    private AlertDialog dialog;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_reservation);
        initView();
    }


    private void initView() {
        appointmentModelList = new ArrayList<>();
        appointmentReserveModelList = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PatientReservationAdapter(appointmentReserveModelList, this);
        binding.recView.setAdapter(adapter);

        getReservation(userModel.getId());


    }

    private void getReservation(String id) {

        dRef.child(Tags.TABLE_RESERVATIONS).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        binding.progBar.setVisibility(View.GONE);

                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                AppointmentReserveModel model = ds.getValue(AppointmentReserveModel.class);

                                appointmentReserveModelList.add(model);


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

    public void setItemDataDelete(int adapterPosition, AppointmentReserveModel appointmentReserveModel) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        dRef.child(Tags.TABLE_RESERVATIONS).child(appointmentReserveModel.getDoctor_id())
                .child(appointmentReserveModel.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    deletePatientAppointment(dialog, appointmentReserveModel, adapterPosition);
                }).addOnFailureListener(e -> {

            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void setItemDataUpdate(int adapterPosition, AppointmentReserveModel appointmentReserveModel) {
        this.appointmentReserveModel = appointmentReserveModel;
        if (appointmentModelList.size() > 0) {
            createAppointmentDialog(appointmentModelList);

        } else {
            getDoctorAppointment(appointmentReserveModel.getDoctor_id());

        }
    }

    private void getDoctorAppointment(String doctor_id) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        dRef.child(Tags.TABLE_USERS).child(doctor_id).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dialog.dismiss();
                        if (dataSnapshot.getValue() != null) {
                            UserModel model = dataSnapshot.getValue(UserModel.class);
                            if (model != null) {
                                if (model.getAppointmentModelList() != null && model.getAppointmentModelList().size() > 0) {
                                    appointmentModelList.addAll(model.getAppointmentModelList());
                                    createAppointmentDialog(model.getAppointmentModelList());
                                } else {
                                    Toast.makeText(PatientReservationActivity.this, "No Appointment available", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dialog.dismiss();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void createAppointmentDialog(List<AppointmentModel> appointmentModelList) {

        dialog = new AlertDialog.Builder(this)
                .create();


        DialogAppointmentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_appointment, null, false);

        binding.recViewAppointment.setLayoutManager(new LinearLayoutManager(this));
        DoctorAppointmentAdapter adapter = new DoctorAppointmentAdapter(appointmentModelList, this);
        binding.recViewAppointment.setAdapter(adapter);

        binding.btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                }
        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    public void update(AppointmentModel appointmentModel) {
        if (dialog != null) {
            dialog.dismiss();
        }

        appointmentReserveModel.setFrom_time(appointmentModel.getFrom_time());
        appointmentReserveModel.setTo_time(appointmentModel.getTo_time());
        appointmentReserveModel.setDay(appointmentModel.getDay());
        ProgressDialog dialog2 = Common.createProgressDialog(this, getString(R.string.wait));
        dialog2.setCancelable(false);
        dialog2.show();
        dRef.child(Tags.TABLE_RESERVATIONS)
                .child(appointmentReserveModel.getDoctor_id())
                .child(appointmentReserveModel.getId())
                .setValue(appointmentReserveModel)
                .addOnSuccessListener(aVoid -> {
                    updatePatientAppointment(dialog2,appointmentReserveModel);
                }).addOnFailureListener(e -> {
            dialog2.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updatePatientAppointment(ProgressDialog dialog2, AppointmentReserveModel appointmentReserveModel) {

        dRef.child(Tags.TABLE_RESERVATIONS)
                .child(appointmentReserveModel.getUser_id())
                .child(appointmentReserveModel.getId())
                .setValue(appointmentReserveModel)
                .addOnSuccessListener(aVoid -> {
                    dialog2.dismiss();
                    Toast.makeText(this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
            dialog2.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deletePatientAppointment(ProgressDialog dialog, AppointmentReserveModel appointmentReserveModel, int adapterPosition) {

        dRef.child(Tags.TABLE_RESERVATIONS).child(appointmentReserveModel.getUser_id())
                .child(appointmentReserveModel.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    appointmentReserveModelList.remove(adapterPosition);
                    adapter.notifyItemRemoved(adapterPosition);
                    if (appointmentReserveModelList.size() > 0) {
                        binding.tvNoReservation.setVisibility(View.GONE);
                    } else {
                        binding.tvNoReservation.setVisibility(View.VISIBLE);

                    }

                }).addOnFailureListener(e -> {

            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

}
