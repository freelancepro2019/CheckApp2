package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_patient_reservation.PatientReservationActivity;
import com.check.apps.checkapp.activities_fragments.activity_reserve_appointment.ReserveAppointmentActivity;
import com.check.apps.checkapp.databinding.AppointmentDoctorRowBinding;
import com.check.apps.checkapp.models.AppointmentModel;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppointmentModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity activity;

    public DoctorAppointmentAdapter(List<AppointmentModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (AppCompatActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        AppointmentDoctorRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.appointment_doctor_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));


        myHolder.itemView.setOnClickListener(view -> {

            if (activity instanceof  ReserveAppointmentActivity)
            {
                ReserveAppointmentActivity reserveAppointmentActivity = (ReserveAppointmentActivity) activity;
                reserveAppointmentActivity.setAppointmentData(list.get(myHolder.getAdapterPosition()));

            }else if (activity instanceof PatientReservationActivity)
            {
                PatientReservationActivity patientReservationActivity = (PatientReservationActivity) activity;
                patientReservationActivity.update(list.get(myHolder.getAdapterPosition()));
            }

            //notifyDataSetChanged();

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public AppointmentDoctorRowBinding binding;

        public MyHolder(@NonNull AppointmentDoctorRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
