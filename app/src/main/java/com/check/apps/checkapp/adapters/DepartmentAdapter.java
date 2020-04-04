package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_reserve_appointment.ReserveAppointmentActivity;
import com.check.apps.checkapp.databinding.DepartmentRowBinding;

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> list;
    private Context context;
    private LayoutInflater inflater;
    private ReserveAppointmentActivity activity;
    private int selected_pos = 0;

    public DepartmentAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (ReserveAppointmentActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        DepartmentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.department_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setTitle(list.get(position));

        if (position==selected_pos)
        {
            myHolder.binding.tvName.setTextColor(ContextCompat.getColor(context,R.color.white));
            myHolder.binding.card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
        }else
            {
                myHolder.binding.tvName.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
                myHolder.binding.card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white));

            }


        myHolder.itemView.setOnClickListener(view -> {

            selected_pos = myHolder.getAdapterPosition();
            activity.setItemData(selected_pos);
            notifyDataSetChanged();

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public DepartmentRowBinding binding;

        public MyHolder(@NonNull DepartmentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
