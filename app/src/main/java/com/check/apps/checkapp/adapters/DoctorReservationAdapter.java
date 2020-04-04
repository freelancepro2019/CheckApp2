package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_doctor_today_appointments.ActivityDoctorTodayAppointmentsActivity;
import com.check.apps.checkapp.databinding.ReservationRowBinding;
import com.check.apps.checkapp.models.AppointmentReserveModel;

import java.util.List;

public class DoctorReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppointmentReserveModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity appCompatActivity;

    public DoctorReservationAdapter(List<AppointmentReserveModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        appCompatActivity = (AppCompatActivity) context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ReservationRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.reservation_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));


        myHolder.binding.btnAccept.setOnClickListener(view -> {

            if (appCompatActivity instanceof ActivityDoctorTodayAppointmentsActivity)
            {
                ActivityDoctorTodayAppointmentsActivity activity = (ActivityDoctorTodayAppointmentsActivity) appCompatActivity;
                activity.setItemData(myHolder.getAdapterPosition(),list.get(myHolder.getAdapterPosition()));

            }


        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ReservationRowBinding binding;

        public MyHolder(@NonNull ReservationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
