package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_patient_reservation.PatientReservationActivity;
import com.check.apps.checkapp.databinding.PatientReservationRowBinding;
import com.check.apps.checkapp.models.AppointmentReserveModel;

import java.util.List;

public class PatientReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppointmentReserveModel> list;
    private Context context;
    private LayoutInflater inflater;
    private PatientReservationActivity activity;

    public PatientReservationAdapter(List<AppointmentReserveModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (PatientReservationActivity) context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        PatientReservationRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.patient_reservation_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));


        myHolder.binding.btnDelete.setOnClickListener(view -> {

            activity.setItemDataDelete(myHolder.getAdapterPosition(),list.get(myHolder.getAdapterPosition()));



        });

        myHolder.binding.btnUpdate.setOnClickListener(view -> {

            activity.setItemDataUpdate(myHolder.getAdapterPosition(),list.get(myHolder.getAdapterPosition()));



        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public PatientReservationRowBinding binding;

        public MyHolder(@NonNull PatientReservationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
