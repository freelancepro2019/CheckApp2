package com.check.apps.checkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_sign_up.SignUpActivity;
import com.check.apps.checkapp.databinding.CountriesRowBinding;
import com.check.apps.checkapp.models.CountryModel;

import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CountryModel> list;
    private Context context;
    private LayoutInflater inflater;
    private SignUpActivity activity;
    public CountriesAdapter(List<CountryModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (SignUpActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        CountriesRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.countries_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {
            activity.setItemData(list.get(myHolder.getAdapterPosition()));

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public CountriesRowBinding binding;

        public MyHolder(@NonNull CountriesRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
