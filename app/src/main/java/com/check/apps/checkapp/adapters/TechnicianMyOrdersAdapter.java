package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_technician_orders.TechnicianOrdersActivity;
import com.check.apps.checkapp.databinding.TechnicianMyOrderRowBinding;
import com.check.apps.checkapp.models.TechnicianReportModel;

import java.util.List;

public class TechnicianMyOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TechnicianReportModel> list;
    private Context context;
    private LayoutInflater inflater;
    private TechnicianOrdersActivity activity;

    public TechnicianMyOrdersAdapter(List<TechnicianReportModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (TechnicianOrdersActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        TechnicianMyOrderRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.technician_my_order_row, parent, false);
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
        public TechnicianMyOrderRowBinding binding;

        public MyHolder(@NonNull TechnicianMyOrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
