package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    EditText forgotEmail;
    Button sendEmail, loginButton;
    FirebaseAuth firebaseAuth;
    TextView notifyText, title;
    Switch switch1, switch2;
    ImageView forgotBackButtonShow;
    DatabaseReference notifyDatabase;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    public static final String TAG = "MyTag";
    private static int SPLASH_TIME_OUT = 3000;

    boolean checkLogin = false;
    boolean validateEmailAddress = false;
    boolean checkNumber = false;

    String email1, email2, finalEmail, value, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_forgot_password);

        forgotEmail = findViewById(R.id.forgotEmail);
        sendEmail = findViewById(R.id.forgotEmailButton);
        loginButton = findViewById(R.id.forgotBackButton);
        notifyText = (TextView) findViewById(R.id.afterDisplay);
        switch1 = findViewById(R.id.openWifiButton);
        switch2 = findViewById(R.id.openMobileData);
        forgotBackButtonShow = findViewById(R.id.forgotBackButtonShow);
        title = findViewById(R.id.forgotText);
        progressBar = findViewById(R.id.forgotProgressBar);

        Bundle extras = getIntent().getExtras();
        email2 = extras.getString("email");
        forgotEmail.setText(email2);
        Log.d(TAG, "onCreate: email2" + email2);

//        email1 = forgotEmail.getText().toString();
//        Log.d(TAG, "onCreate: email1" + email1);

        firebaseAuth = FirebaseAuth.getInstance();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isWifiConn = false;
        boolean isMobileConn = false;

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: wifi");
                checkLogin = true;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: data");
                checkLogin = true;
            }
        }

        Log.d(TAG, "onCreate: Forgot Password Internet: " + activeNetworkInfo);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!checkLogin) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this).create();
                    alertDialog.setTitle("No Internet Connection");
                    alertDialog.setMessage("It seams you are offline but you need internet connection to reset your password");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    switch1.setVisibility(View.VISIBLE);
                    switch2.setVisibility(View.VISIBLE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    email1 = forgotEmail.getText().toString();
//                    finalEmail = email1;
                    Log.d(TAG, "onCreate: email2" + email2);
                    Log.d(TAG, "onCreate: email1" + email1);
                    validateEmailAddress = validateEmail(email1);

                    notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

                    notifyDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                                if (email1.equals(databaseEmail)) {
                                    value = String.valueOf(dataSnapshot1.child("userPhoneNumber").getValue());
                                    Log.d(TAG, "onDataChange: value: " + value);
                                    checkNumber = storeNumber(value);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if (validateEmailAddress) {
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordSelectionActivity.class);

                                intent.putExtra("email", email1);
                                intent.putExtra("phoneNumber", phoneNumber);
                                Log.d(TAG, "onClick: number: " + phoneNumber);

                                Pair[] pairs = new Pair[2];

                                pairs[0] = new Pair<View, String>(title, "logo_text");
                                pairs[1] = new Pair<View, String>(forgotBackButtonShow, "transition_back_arrow_button");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);
                                startActivity(intent, options.toBundle());
                            }
                        }, SPLASH_TIME_OUT);

                        progressBar.setVisibility(View.GONE);
                    }

//                    startActivity(intent);
//                    firebaseAuth.sendPasswordResetEmail(forgotEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            if (task.isSuccessful()){
//                                AlertDialog alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this).create();
//                                alertDialog.setTitle("Password Reset Link Send");
//                                alertDialog.setMessage("Please check your email to reset your password");
//                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                                        new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                            }
//                                        });
//                                alertDialog.show();
//
//                                notifyText.setVisibility(View.VISIBLE);
//                                notifyText.setText("We have send you an email for password reset." +
//                                        "If didn't receive an email wait for some time or else click on 'SEND EMAIL' button.");
//                            }
//                            else{
//                                Toast.makeText(ForgotPasswordActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
////                                AlertDialog alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this).create();
////                                alertDialog.setTitle("Error");
////                                alertDialog.setMessage("We don't recognize this email address");
////                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
////                                        new DialogInterface.OnClickListener() {
////                                            public void onClick(DialogInterface dialog, int which) {
////                                                dialog.dismiss();
////                                            }
////                                        });
////                                alertDialog.show();
//                            }
//                        }
//                    });
                }
            }
        });

        forgotBackButtonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email1 = forgotEmail.getText().toString();
                Log.d(TAG, "onClick: if email1: " + email1);
                if (!email1.isEmpty()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);

                            Pair[] pairs = new Pair[2];
                            pairs[0] = new Pair<View, String>(title, "logo_text");
                            pairs[1] = new Pair<View, String>(forgotBackButtonShow, "transition_back_arrow_button");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);
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
                else{
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);

                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(title, "logo_text");
                    pairs[1] = new Pair<View, String>(forgotBackButtonShow, "transition_back_arrow_button");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email1 = forgotEmail.getText().toString();
                Log.d(TAG, "onClick: if email1: " + email1);
                if (!email1.isEmpty()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);

                            Pair[] pairs = new Pair[2];
                            pairs[0] = new Pair<View, String>(title, "logo_text");
                            pairs[1] = new Pair<View, String>(forgotBackButtonShow, "transition_back_arrow_button");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);
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
                else{
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(title, "logo_text");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked)
                {
                    Log.d(TAG, "onCheckedChanged: if");
                    EnableWiFi() ;
                }
                else {
                    Log.d(TAG, "onCheckedChanged: else");
                    DisableWiFi();
                }
            }
        });

        switch2.setChecked(getMobileDataState());
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMobileDataState(isChecked);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    public boolean storeNumber(String number){
        phoneNumber = number;
        Log.d(TAG, "storeNumber: Number: " + phoneNumber);

        if (!number.isEmpty()){
            checkNumber = true;
            return true;
        }
        else{
            checkNumber = false;
            return false;
        }
    }

    public boolean validateEmail(String userEmail){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        if (userEmail.isEmpty()){
            Log.d(TAG, "validateEmail: Email" + userEmail);
            forgotEmail.setError("Email Address cannot be empty");
            forgotEmail.requestFocus();
            return false;
        }
        else {
            if (pat.matcher(userEmail).matches()) {
                return true;
            }
            else{
                forgotEmail.setError("Email Address is not valid");
                forgotEmail.requestFocus();
                return false;
            }
        }
    }

    private void setMobileDataState(boolean isChecked) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setMobileDataEnabledMethod.invoke(telephonyService, isChecked);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error setting mobile data state", ex);
        }
    }

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("getDataEnabled");
            return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error getting mobile data state", ex);
        }
        return false;
    }

    public void EnableWiFi(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(this, "Your now connected to internet", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = true;
    }

    public void DisableWiFi(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(this, ":-( connection lost", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = false;
    }
}
