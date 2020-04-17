package com.check.apps.checkapp.models;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.tags.Tags;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpModel extends BaseObservable implements Serializable {

    private String name;
    private String email;
    private String phone_code;
    private String phone;
    private int gender;
    private String age;
    private String birth_date;
    private String city;
    private int user_type;
    private String password;
    private String re_password;
    private String blood_type;
    private boolean isFingerprint;
    private boolean isAcceptTerms;
    private boolean isAvailable;
    private String department;
    private List<AppointmentModel> appointmentModelList;

    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_age = new ObservableField<>();
    public ObservableField<String> error_city = new ObservableField<>();

    public ObservableField<String> error_password = new ObservableField<>();
    public ObservableField<String> error_re_password = new ObservableField<>();

    public boolean isDataValid(Context context) {

        Log.e("name",name+"_");
        Log.e("email",email+"_");
        Log.e("phone_code",phone_code+"_");
        Log.e("phone",phone+"_");
        Log.e("gender",gender+"_");
        Log.e("birth_date",birth_date+"_");
        Log.e("user_type",user_type+"_");
        Log.e("blood_type",blood_type+"_");
        Log.e("age",age+"_");
        Log.e("city",city+"_");
        Log.e("password",password+"_");
        Log.e("valid",isValidPassword(password)+"_");



        if (!name.trim().isEmpty() &&
                !email.trim().isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
                !phone_code.trim().isEmpty() &&
                !phone.trim().isEmpty() &&
                gender != 0 &&
                !birth_date.isEmpty() &&
                user_type != 0 &&
                !blood_type.trim().isEmpty()&&
                !age.trim().isEmpty()&&
                !city.trim().isEmpty()&&
                password.length() >= 10 &&
                isValidPassword(password) &&
                re_password.equals(password) &&
                isAcceptTerms

        ) {
            error_name.set(null);
            error_email.set(null);
            error_phone.set(null);
            error_password.set(null);
            error_re_password.set(null);
            error_age.set(null);
            error_city.set(null);

            if (user_type == Tags.doctor) {
                if (!department.isEmpty()&& appointmentModelList != null && appointmentModelList.size() > 0) {
                    return true;
                } else {

                    if (department.isEmpty()) {
                        Toast.makeText(context, R.string.ch_dept, Toast.LENGTH_SHORT).show();
                    }

                    if (appointmentModelList == null) {
                        Toast.makeText(context, R.string.ch_appoint, Toast.LENGTH_SHORT).show();

                    } else if (appointmentModelList.size() == 0) {
                        Toast.makeText(context, R.string.ch_appoint, Toast.LENGTH_SHORT).show();

                    }
                    return false;
                }
            }

            return true;
        } else {

            if (name.trim().isEmpty()) {
                error_name.set(context.getString(R.string.field_req));
            } else {
                error_name.set(null);

            }

            if (email.trim().isEmpty()) {
                error_email.set(context.getString(R.string.field_req));
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                error_email.set(context.getString(R.string.inv_email));

            } else {
                error_email.set(null);

            }

            if (phone.trim().isEmpty()) {
                error_phone.set(context.getString(R.string.field_req));
            } else {
                error_phone.set(null);

            }


            if (age.trim().isEmpty()) {
                error_age.set(context.getString(R.string.field_req));
            } else {
                error_age.set(null);

            }

            if (city.trim().isEmpty()) {
                error_city.set(context.getString(R.string.field_req));
            } else {
                error_city.set(null);

            }

            if (birth_date.trim().isEmpty()) {

                Toast.makeText(context, R.string.ch_birth_date, Toast.LENGTH_SHORT).show();

            }

            if (blood_type.trim().isEmpty()) {

                Toast.makeText(context, R.string.ch_blood, Toast.LENGTH_SHORT).show();

            }

            if (gender == 0) {
                Toast.makeText(context, context.getString(R.string.ch_gender), Toast.LENGTH_SHORT).show();
            }

            if (user_type == 0) {
                Toast.makeText(context, R.string.ch_user_type, Toast.LENGTH_SHORT).show();
            }
            if (!isAcceptTerms) {
                Toast.makeText(context, context.getString(R.string.accept_terms_and_conditions), Toast.LENGTH_SHORT).show();
            }

            if (password.trim().isEmpty()) {
                error_password.set(context.getString(R.string.field_req));
            } else if (password.trim().length() < 10) {
                error_password.set(context.getString(R.string.pass_short));

            } else if(!isValidPassword(password))
            {
                error_password.set(context.getString(R.string.inv_pass));

            }
            else {
                error_password.set(null);

            }

            if (re_password.isEmpty()) {
                error_re_password.set(context.getString(R.string.field_req));

            } else if (!password.equals(re_password)) {
                error_re_password.set(context.getString(R.string.pas_not_match));

            } else {

                error_re_password.set(null);
            }

            return false;
        }
    }

    public SignUpModel() {
        setName("");
        setEmail("");
        setPhone("");
        setPhone_code("");
        setAcceptTerms(false);
        setAppointmentModelList(null);
        setBirth_date("");
        setAge("");
        setCity("");
        setBlood_type("");
        setGender(0);
        setFingerprint(false);
        setPassword("");
        setRe_password("");
        setUser_type(0);
        setDepartment("");
        setAvailable(true);
    }
    private boolean isValidPassword( String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);

    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }

    @Bindable
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    @Bindable
    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);

    }

    @Bindable
    public String getRe_password() {
        return re_password;
    }

    public void setRe_password(String re_password) {
        this.re_password = re_password;
        notifyPropertyChanged(BR.re_password);

    }

    public boolean isFingerprint() {
        return isFingerprint;
    }

    public void setFingerprint(boolean fingerprint) {
        isFingerprint = fingerprint;
    }

    public boolean isAcceptTerms() {
        return isAcceptTerms;
    }

    public void setAcceptTerms(boolean acceptTerms) {
        isAcceptTerms = acceptTerms;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<AppointmentModel> getAppointmentModelList() {
        return appointmentModelList;
    }

    public void setAppointmentModelList(List<AppointmentModel> appointmentModelList) {
        this.appointmentModelList = appointmentModelList;
    }

   @Bindable
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    @Bindable
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        notifyPropertyChanged(BR.city);


    }

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
