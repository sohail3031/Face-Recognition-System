package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordMobilePinActivity extends AppCompatActivity {

    TextView title, mobileOTPDescription;
    Button verifyOTP;
    ProgressBar mobilePinProgressBar;
    PinView otpPin;
    ImageView mobileOTPBack;

    private static final String TAG = "MyTag";
    private String verificationId;
    private FirebaseAuth mAuth;
    private static int SPLASH_TIME_OUT = 2000;

    String phoneNumber, otpCode, finalNumber, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_mobile_pin);

        title = findViewById(R.id.mobileOTPText);
        mobileOTPDescription = findViewById(R.id.mobileOTPDescription);
        verifyOTP = findViewById(R.id.verifyOTP);
        mobilePinProgressBar = findViewById(R.id.mobilePinProgressBar);
        otpPin = findViewById(R.id.otpPin);
        mobileOTPBack = findViewById(R.id.mobileOTPBack);

        otpCode = otpPin.getText().toString();

        Bundle extras = getIntent().getExtras();
        phoneNumber = extras.getString("phoneNumber");
        email = extras.getString("email");
        Log.d(TAG, "onCreate: got phone: " + phoneNumber);

        mobileOTPDescription.setText("OTP(one time password) has been send on your number: " + phoneNumber);

        finalNumber = phoneNumber;
        Log.d(TAG, "onCreate: final number: " + finalNumber);

        mAuth = FirebaseAuth.getInstance();

        sendVerificationCode(finalNumber);

        mobileOTPBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPasswordMobilePinActivity.this);
                alertDialog.setTitle("Are You Sure?");
                alertDialog.setMessage("You want to cancel the process?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ForgotPasswordMobilePinActivity.this, LoginActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(title, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordMobilePinActivity.this, pairs);
                        startActivity(intent, options.toBundle());
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otpPin.getText().toString();

                if (otpCode.isEmpty() || otpCode.length() < 6) {
                    otpPin.setError("Enter a valid OTP pin");
                    otpPin.requestFocus();
                    return;
                }

                mobilePinProgressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mobilePinProgressBar.setVisibility(View.VISIBLE);

//                        Intent intent = new Intent(ForgotPasswordMobilePinActivity.this, ResetNewPasswordActivity.class);
                        Intent intent = new Intent(ForgotPasswordMobilePinActivity.this, DummyLoginActivity.class);

                        intent.putExtra("email", email);
                        intent.putExtra("password1", "");
                        intent.putExtra("password2", "");

                        Pair[] pairs = new Pair[1];

                        pairs[0] = new Pair<View, String>(title, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordMobilePinActivity.this, pairs);
                        startActivity(intent, options.toBundle());
                    }
                }, SPLASH_TIME_OUT);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//        signInWithCrediential(credential);
        nextPageFunction();
    }

    private void nextPageFunction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mobilePinProgressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(ForgotPasswordMobilePinActivity.this, DummyLoginActivity.class);

                intent.putExtra("email", email);
                intent.putExtra("password1", "");
                intent.putExtra("password2", "");

                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View, String>(title, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordMobilePinActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        }, SPLASH_TIME_OUT);

        mobilePinProgressBar.setVisibility(View.GONE);
    }

//    private void signInWithCrediential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    Intent intent = new Intent(ForgotPasswordMobilePinActivity.this, ResetNewPasswordActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                }
//                else{
//                    Toast.makeText(ForgotPasswordMobilePinActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }

    private void sendVerificationCode(String number){
        mobilePinProgressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(finalNumber, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otpPin.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(ForgotPasswordMobilePinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}
