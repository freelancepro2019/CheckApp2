package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_appointment.AppointmentActivity;
import com.check.apps.checkapp.databinding.AppointmentRowBinding;
import com.check.apps.checkapp.models.AppointmentModel;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppointmentModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppointmentActivity activity;

    public AppointmentAdapter(List<AppointmentModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (AppointmentActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        AppointmentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.appointment_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {

            activity.deleteItem(myHolder.getAdapterPosition());

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public AppointmentRowBinding binding;

        public MyHolder(@NonNull AppointmentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
