package com.check.apps.checkapp.activities_fragments.activity_appointment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.adapters.AppointmentAdapter;
import com.check.apps.checkapp.adapters.SpinnerDaysAdapter;
import com.check.apps.checkapp.databinding.ActivityAppointmentBinding;
import com.check.apps.checkapp.interfaces.Listeners;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.AppointmentModel;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class AppointmentActivity extends AppCompatActivity implements Listeners.BackListener, TimePickerDialog.OnTimeSetListener {
    private ActivityAppointmentBinding binding;
    private String lang = "en";
    private List<String> departmentModelList;
    private List<String> daysList;
    private SpinnerDaysAdapter spinnerDaysAdapter,spinnerDepartmentAdapter;
    private TimePickerDialog timePickerDialog;
    private int selected = 0;
    private List<AppointmentModel> appointmentModelList;
    private AppointmentAdapter appointmentAdapter;
    private String fromTime="",toTime="",day="";
    private String department="";


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"en"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment);
        initView();
    }



    private void initView()
    {

        appointmentModelList = new ArrayList<>();
        daysList = new ArrayList<>();
        departmentModelList = new ArrayList<>();
        binding.setBackListener(this);
        binding.setLang(lang);

        createTimePickerDialog();


        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(appointmentModelList,this);
        binding.recView.setAdapter(appointmentAdapter);

        departmentModelList.add("Departments");
        departmentModelList.add("Cardiology");
        departmentModelList.add("Pediatrics");
        departmentModelList.add("Hepatology");
        departmentModelList.add("Dermatology");
        departmentModelList.add("Neurology");
        departmentModelList.add("Ophthalmology");
        departmentModelList.add("Urology");
        departmentModelList.add("Obstetrics And Gynaecology");


        spinnerDepartmentAdapter = new SpinnerDaysAdapter(this,departmentModelList);
        binding.spinner.setAdapter(spinnerDepartmentAdapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0)
                {
                    department ="";
                }else {
                    department = departmentModelList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        daysList.add("Day");
        daysList.add("Saturday");
        daysList.add("Sunday");
        daysList.add("Monday");
        daysList.add("Tuesday");
        daysList.add("Wednesday");
        daysList.add("Thursday");
        daysList.add("Friday");

        spinnerDaysAdapter = new SpinnerDaysAdapter(this,daysList);
        binding.spinnerDay.setAdapter(spinnerDaysAdapter);

        binding.spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0)
                {
                    day ="";
                }else {
                    day = daysList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.tvFrom.setOnClickListener(view -> {

            selected = 1;

            try {
                timePickerDialog.show(getFragmentManager(),"");
            }catch (Exception e){}
        });

        binding.tvTo.setOnClickListener(view -> {

            selected = 2;

            try {
                timePickerDialog.show(getFragmentManager(),"");
            }catch (Exception e){}
        });


        binding.btnAddAppointment.setOnClickListener(view -> {


            if (!day.isEmpty()&&!fromTime.isEmpty()&&!toTime.isEmpty())
            {
                binding.tvFrom.setError(null);
                binding.tvTo.setError(null);

                AppointmentModel model = new AppointmentModel(day,fromTime,toTime);


                if (!isAppointmentAddedBefore(model))
                {
                    appointmentModelList.add(model);
                    appointmentAdapter.notifyItemInserted(appointmentModelList.size()-1);
                }else {

                    Toast.makeText(this, "Appointment is exist", Toast.LENGTH_SHORT).show();
                }


            }else
                {
                    if (day.isEmpty())
                    {
                        Toast.makeText(this, "Choose Day", Toast.LENGTH_SHORT).show();
                    }

                    if (fromTime.isEmpty())
                    {
                        binding.tvFrom.setError(getString(R.string.field_req));
                    }else
                        {
                            binding.tvFrom.setError(null);

                        }


                    if (toTime.isEmpty())
                    {
                        binding.tvTo.setError(getString(R.string.field_req));
                    }else
                    {
                        binding.tvTo.setError(null);

                    }
                }

        });

        binding.imageAdd.setOnClickListener(view -> {
            if (!department.isEmpty()&&appointmentModelList.size()>0)
            {
                Intent intent = getIntent();
                intent.putExtra("department",department);
                intent.putExtra("appointment", (Serializable) appointmentModelList);
                setResult(RESULT_OK,intent);
                finish();

            }else
                {

                    if (department.isEmpty())
                    {
                        Toast.makeText(this, "Choose Department", Toast.LENGTH_SHORT).show();
                    }


                    if (appointmentModelList.size()==0)
                    {
                        Toast.makeText(this, "Choose Appointments", Toast.LENGTH_SHORT).show();
                    }
                }
        });

    }


    private boolean isAppointmentAddedBefore(AppointmentModel appointmentModel)
    {




        for (AppointmentModel model :appointmentModelList)
        {

            if (model.getDay().equals(appointmentModel.getDay())&&
                    model.getFrom_time().equals(appointmentModel.getFrom_time())&&
                    model.getTo_time().equals(appointmentModel.getTo_time())
            ){
                return true;
            }else
                {
                    return false;
                }
        }

        return false;
    }

    private void createTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.setAccentColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        timePickerDialog.setCancelColor(ActivityCompat.getColor(this, R.color.gray4));
        timePickerDialog.setOkColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        timePickerDialog.setOkText(getString(R.string.select));
        timePickerDialog.setCancelText(getString(R.string.cancel));
        timePickerDialog.setLocale(new Locale(lang));
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);


    }


    @Override
    public void back() {
        finish();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String time = dateFormat.format(new Date(calendar.getTimeInMillis()));

        if (selected==1)
        {
            fromTime = time;
            binding.tvFrom.setText(time);
        }else
            {
                toTime = time;
                binding.tvTo.setText(time);
            }

    }

    public void deleteItem(int adapterPosition) {

        if (appointmentModelList.size()>0)
        {
            appointmentModelList.remove(adapterPosition);
            appointmentAdapter.notifyItemRemoved(adapterPosition);
        }
    }
}
