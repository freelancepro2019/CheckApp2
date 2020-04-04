package com.check.apps.checkapp.activities_fragments.activity_doctor_today_appointments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.check.apps.checkapp.adapters.DoctorReservationAdapter;
import com.check.apps.checkapp.databinding.ActivityDoctorTodayAppointmentsBinding;
import com.check.apps.checkapp.databinding.DialogReportBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.AppointmentReserveModel;
import com.check.apps.checkapp.models.ReportModel;
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

public class ActivityDoctorTodayAppointmentsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityDoctorTodayAppointmentsBinding binding;
    private String lang = "en";
    private DatabaseReference dRef;
    private DatabaseReference dRefReport;
    private DatabaseReference dRefAppointment;

    private UserModel userModel;
    private Preferences preferences;
    private DoctorReservationAdapter adapter;
    private List<AppointmentReserveModel> appointmentReserveModelList;
    private int reservation_type;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_today_appointments);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            reservation_type = intent.getIntExtra("type", 1);

        }
    }


    private void initView() {
        appointmentReserveModelList = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_RESERVATIONS);
        dRefReport = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_REPORTS);
        dRefAppointment = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_RESERVATIONS);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorReservationAdapter(appointmentReserveModelList, this);
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
                                if (reservation_type == 1) {
                                    if (model != null && model.getStatus() == Tags.reserve_accepted) {
                                        appointmentReserveModelList.add(model);
                                    }
                                    /// all reservations
                                } else if (reservation_type == 2) {
                                    if (model != null && model.getStatus() != Tags.reserve_finished) {
                                        appointmentReserveModelList.add(model);

                                    }
                                    /// finished reservations
                                } else if (reservation_type == 3) {
                                    if (model != null && model.getStatus() == Tags.reserve_finished) {
                                        appointmentReserveModelList.add(model);

                                    }
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


        if (appointmentReserveModel.getStatus() == Tags.reserve_new) {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.show();
            appointmentReserveModel.setStatus(Tags.reserve_accepted);

            dRef.child(userModel.getDoctor_id()).child(appointmentReserveModel.getId())
                    .setValue(appointmentReserveModel)
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

                if (e.getMessage() != null) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            });

        } else if (appointmentReserveModel.getStatus() == Tags.reserve_accepted) {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.show();
            appointmentReserveModel.setStatus(Tags.reserve_finished);

            dRef.child(userModel.getDoctor_id()).child(appointmentReserveModel.getId())
                    .setValue(appointmentReserveModel)
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

                if (e.getMessage() != null) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            });

        } else if (appointmentReserveModel.getStatus() == Tags.reserve_finished) {
            createReportDialogAlert(adapterPosition, appointmentReserveModel);
        }


    }


    private void createReportDialogAlert(int pos, AppointmentReserveModel model) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogReportBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_report, null, false);

        binding.btnSend.setOnClickListener(v ->
                {
                    String report = binding.edtReport.getText().toString().trim();
                    if (!report.isEmpty()) {
                        dialog.dismiss();
                        Common.CloseKeyBoard(this, binding.edtReport);
                        binding.edtReport.setError(null);
                        addReport(report, pos, model);
                    } else {
                        binding.edtReport.setError(getString(R.string.field_req));

                    }
                }

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void addReport(String report, int pos, AppointmentReserveModel model) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        String id = dRef.child(model.getUser_id()).push().getKey();
        ReportModel reportModel = new ReportModel(id, model.getUser_id(), model.getUser_name(), model.getDoctor_id(), model.getDoctor_name(), report);
        dRefReport.child(model.getUser_id()).child(id)
                .setValue(reportModel)
                .addOnSuccessListener(aVoid -> {
                    deleteReservation(pos, model, dialog);

                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteReservation(int pos, AppointmentReserveModel model, ProgressDialog dialog) {

        dRefAppointment.child(model.getDoctor_id()).child(model.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    deletePatientReservation(pos,model,dialog);
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    if (e.getMessage() != null) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deletePatientReservation(int pos, AppointmentReserveModel model, ProgressDialog dialog) {


        dRefAppointment.child(model.getUser_id()).child(model.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    appointmentReserveModelList.remove(pos);
                    adapter.notifyItemRemoved(pos);
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
