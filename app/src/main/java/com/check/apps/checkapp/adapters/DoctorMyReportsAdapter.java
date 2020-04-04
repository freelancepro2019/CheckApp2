package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_doctor_my_reports.DoctorMyReportsActivity;
import com.check.apps.checkapp.databinding.DoctorMyReportRowBinding;
import com.check.apps.checkapp.models.TechnicianReportModel;

import java.util.List;

public class DoctorMyReportsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TechnicianReportModel> list;
    private Context context;
    private LayoutInflater inflater;
    private DoctorMyReportsActivity activity;

    public DoctorMyReportsAdapter(List<TechnicianReportModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (DoctorMyReportsActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        DoctorMyReportRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.doctor_my_report_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {

            TechnicianReportModel model = list.get(holder.getAdapterPosition());
            activity.setItemData(model);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public DoctorMyReportRowBinding binding;

        public MyHolder(@NonNull DoctorMyReportRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
