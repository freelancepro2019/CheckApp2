package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_all_technician.AllTechnicianActivity;
import com.check.apps.checkapp.databinding.TechnicianRowBinding;
import com.check.apps.checkapp.models.UserModel;

import java.util.List;

public class TechnicianAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AllTechnicianActivity activity;
    public TechnicianAdapter(List<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (AllTechnicianActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        TechnicianRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.technician_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {

            UserModel model = list.get(myHolder.getAdapterPosition());
            activity.setItemData(model);
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TechnicianRowBinding binding;

        public MyHolder(@NonNull TechnicianRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
