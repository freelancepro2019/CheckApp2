package com.check.apps.checkapp.activities_fragments.activity_verification_code;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.activities_fragments.activity_doctor_home.HomeDoctorActivity;
import com.check.apps.checkapp.activities_fragments.activity_nurse_home.NurseHomeActivity;
import com.check.apps.checkapp.activities_fragments.activity_patient_home.HomePatientActivity;
import com.check.apps.checkapp.activities_fragments.activity_technician_home.TechnicianHomeActivity;
import com.check.apps.checkapp.databinding.ActivityVerificationCodeBinding;
import com.check.apps.checkapp.databinding.DialogAlertBinding;
import com.check.apps.checkapp.language.LanguageHelper;
import com.check.apps.checkapp.models.AESUtils;
import com.check.apps.checkapp.models.SignUpModel;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.share.Common;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class VerificationCodeActivity extends AppCompatActivity {
    private ActivityVerificationCodeBinding binding;

    private boolean canResend = true;
    private CountDownTimer countDownTimer;
    private String lang = "en";
    private Preferences preferences;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private ProgressDialog dialog;
    private String verificationId="";
    private SignUpModel signUpModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_code);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {

           signUpModel = (SignUpModel) intent.getSerializableExtra("data");


        }
    }

    private void initView() {

        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        preferences = Preferences.newInstance();

        binding.btnConfirm.setOnClickListener(v -> checkData());

        binding.btnResend.setOnClickListener(v -> {

            if (canResend) {
               /* dialog = Common.createProgressDialog(this, getString(R.string.wait));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();*/
                sendSMSCode();
                startCounter();

            }
        });
        sendSMSCode();
        startCounter();
    }

    private void checkData() {
        String code = binding.edtCode.getText().toString().trim();
        if (!TextUtils.isEmpty(code)) {

            Common.CloseKeyBoard(this, binding.edtCode);
            checkValidCode(code);

        } else {
            binding.edtCode.setError(getString(R.string.field_req));
        }
    }

    private void sendSMSCode() {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                if (!verificationId.isEmpty())
                {
                    String smsCode = phoneAuthCredential.getSmsCode();
                    checkValidCode(smsCode);
                }else
                    {
                        Toast.makeText(VerificationCodeActivity.this, "Server error", Toast.LENGTH_SHORT).show();                    }


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (dialog!=null)
                {
                    dialog.dismiss();
                }
                if (e.getMessage() != null) {
                    createDialogAlert(e.getMessage());
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.e("tt","ttt");
                VerificationCodeActivity.this.verificationId = verificationId;

            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                signUpModel.getPhone_code()+signUpModel.getPhone(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );
    }

    private void checkValidCode(String smsCode) {

        if(!verificationId.isEmpty()&&smsCode!=null&&!smsCode.isEmpty())
        {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,smsCode);
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {

                        createAccount();


                    }).addOnFailureListener(e -> {

                if (e.getMessage()!=null)
                {
                    Common.CreateDialogAlert(this,e.getMessage());
                }else
                {
                    Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            });
        }else
            {
                createAccount();
            }


    }

    private void createAccount() {

        dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        mAuth.createUserWithEmailAndPassword(signUpModel.getEmail(),signUpModel.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String userId = task.getResult().getUser().getUid();

                        addUserToDatabase(dialog,userId);

                    }
                }).addOnFailureListener(e -> {
                    if (dialog!=null)
                    {
                        dialog.dismiss();
                    }
            if (e.getMessage() != null) {
                Common.CreateDialogAlert(VerificationCodeActivity.this, e.getMessage());
            }
        });

    }

    private void addUserToDatabase(ProgressDialog dialog, String userId) {

        String encyreptedPassword="";
        try {
             encyreptedPassword = AESUtils.encrypt(signUpModel.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserModel userModel;

        if (signUpModel.getUser_type()==Tags.doctor||signUpModel.getUser_type()==Tags.technician)
        {
          userModel =  new UserModel(userId,"","",userId,signUpModel.getName(),signUpModel.getUser_type(),signUpModel.getName(),signUpModel.getEmail(),signUpModel.getPhone_code(),signUpModel.getPhone(),signUpModel.getGender(),signUpModel.getBlood_type(),signUpModel.getAge(),signUpModel.getBirth_date(),signUpModel.getCity(),encyreptedPassword,signUpModel.isFingerprint(),signUpModel.getDepartment(),signUpModel.isAvailable(),signUpModel.getAppointmentModelList());
        }else if (signUpModel.getUser_type() == Tags.nurse)
            {
                userModel =  new UserModel(userId,userId,signUpModel.getName(),"","",signUpModel.getUser_type(),signUpModel.getName(),signUpModel.getEmail(),signUpModel.getPhone_code(),signUpModel.getPhone(),signUpModel.getGender(),signUpModel.getBlood_type(),signUpModel.getAge(),signUpModel.getBirth_date(),signUpModel.getCity(),encyreptedPassword,signUpModel.isFingerprint(),signUpModel.getDepartment(),signUpModel.isAvailable(),signUpModel.getAppointmentModelList());

            }else
                {
                    userModel =  new UserModel(userId,"","","","",signUpModel.getUser_type(),signUpModel.getName(),signUpModel.getEmail(),signUpModel.getPhone_code(),signUpModel.getPhone(),signUpModel.getGender(),signUpModel.getBlood_type(),signUpModel.getAge(),signUpModel.getBirth_date(),signUpModel.getCity(),encyreptedPassword,signUpModel.isFingerprint(),signUpModel.getDepartment(),signUpModel.isAvailable(),signUpModel.getAppointmentModelList());

                }


        dRef.child(userId)
                .setValue(userModel)
                .addOnSuccessListener(aVoid -> {

                    dialog.dismiss();
                    preferences.create_update_userData(VerificationCodeActivity.this,userModel);

                    if (signUpModel.getUser_type()==Tags.patient)
                    {
                        navigateToHomePatientActivity();

                    }else if (signUpModel.getUser_type()==Tags.doctor)
                    {
                        navigateToHomeDoctorActivity();
                    }
                    else if (signUpModel.getUser_type()==Tags.technician)
                    {
                        navigateToHomeTechnicianActivity();
                    }else if (signUpModel.getUser_type()==Tags.nurse)
                    {
                        navigateToHomeNurseActivity();
                    }



                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    if (e.getMessage()!=null)
                    {
                        Common.CreateDialogAlert(VerificationCodeActivity.this,e.getMessage());
                    }else
                        {
                            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                });


    }

    private void startCounter() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                canResend = false;

                int AllSeconds = (int) (millisUntilFinished / 1000);
                int seconds = AllSeconds % 60;
                binding.btnResend.setText("00:" + seconds);
            }

            @Override
            public void onFinish() {
                canResend = true;
                binding.btnResend.setText(getString(R.string.resend));
            }
        }.start();
    }

    private void createDialogAlert(String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogAlertBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_alert, null, false);

        binding.tvMsg.setText(msg);
        binding.btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();

                }

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }


    private void navigateToHomePatientActivity() {

        Intent intent = new Intent(this, HomePatientActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToHomeDoctorActivity() {
        Intent intent = new Intent(this, HomeDoctorActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeNurseActivity() {
        Intent intent = new Intent(this, NurseHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeTechnicianActivity() {
        Intent intent = new Intent(this, TechnicianHomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

