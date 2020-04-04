package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_all_technician.AllTechnicianActivity;
import com.check.apps.checkapp.activities_fragments.activity_reserve_appointment.ReserveAppointmentActivity;
import com.check.apps.checkapp.databinding.UserRowBinding;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.tags.Tags;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserModel> list;
    private Context context;
    private LayoutInflater inflater;
    private int [] resource = {R.drawable.circle_color1,R.drawable.circle_color2,R.drawable.circle_color3,R.drawable.circle_color4,R.drawable.circle_color5,R.drawable.circle_color6};
    private AppCompatActivity appCompatActivity;
    public UserAdapter(List<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        appCompatActivity = (AppCompatActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        UserRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        int pos = position%list.size();
        UserModel userModel = list.get(position);
        myHolder.binding.icon.setBackgroundResource(resource[pos]);
        myHolder.binding.setModel(list.get(position));
        if (userModel.getUser_type()== Tags.doctor)
        {
            myHolder.binding.icon.setImageResource(R.drawable.ic_doctor);
        }else if (userModel.getUser_type()== Tags.technician)
        {
            myHolder.binding.icon.setImageResource(R.drawable.ic_lab);

        }
        else if (userModel.getUser_type()== Tags.nurse)
        {
            myHolder.binding.icon.setImageResource(R.drawable.ic_nurse);

        }

        myHolder.itemView.setOnClickListener(view -> {

            if (appCompatActivity instanceof ReserveAppointmentActivity)
            {
                UserModel model = list.get(myHolder.getAdapterPosition());
                ReserveAppointmentActivity activity = (ReserveAppointmentActivity) appCompatActivity;
                activity.setItemDoctorData(model);
            }else if (appCompatActivity instanceof AllTechnicianActivity)
            {
                UserModel model = list.get(myHolder.getAdapterPosition());
                AllTechnicianActivity activity = (AllTechnicianActivity) appCompatActivity;
                activity.setItemData(model);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public UserRowBinding binding;

        public MyHolder(@NonNull UserRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
