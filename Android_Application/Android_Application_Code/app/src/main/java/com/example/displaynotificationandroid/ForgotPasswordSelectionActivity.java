package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ForgotPasswordSelectionActivity extends AppCompatActivity {

    ImageView forgotSelectBackButtonShow;
    Button mobileButton, emailButton;
    Switch switch1, switch2;
    TextView email_desc, title, mobile_desc;
    FirebaseAuth firebaseAuth;
    DatabaseReference notifyDatabase;
//    InsertingData insertingData;

    String email, phoneNumber, value;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    public static final String TAG = "MyTag";

    boolean checkLogin = false;
    boolean checkNumber = false;

    private List<RetrivingData> displayData;

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

        setContentView(R.layout.activity_forgot_password_selection);

        email_desc = findViewById(R.id.email_desc);
        forgotSelectBackButtonShow = findViewById(R.id.forgotSelectBackButtonShow);
        mobileButton = findViewById(R.id.mobileButton);
        emailButton = findViewById(R.id.emailButton);
        switch1 = findViewById(R.id.selectOpenWifiButton);
        switch2 = findViewById(R.id.selectOpenMobileData);
        title = findViewById(R.id.forgotSelectionText);
        mobile_desc = findViewById(R.id.mobile_desc);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        phoneNumber = extras.getString("phoneNumber");
        email_desc.setText(email);
        mobile_desc.setText(phoneNumber);
        Log.d(TAG, "onCreate: email: " + email);
        Log.d(TAG, "onCreate: phone: " + phoneNumber);

//        if (phoneNumber == null || phoneNumber.equals("")) {
//            Intent intent = new Intent(ForgotPasswordSelectionActivity.this, LoginActivity.class);
//
//            intent.putExtra("email", email);
//
//            Pair[] pairs = new Pair[1];
//            pairs[0] = new Pair<View, String>(title, "logo_text");
//
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordSelectionActivity.this, pairs);
//            startActivity(intent, options.toBundle());
//        }

//        if (!email.isEmpty()){
//            onStart();
//        }

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

//        Log.d(TAG, "onCreate: got data: " + insertingData);

//        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
//
//        notifyDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                    String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
//                    if (email.equals(databaseEmail)) {
//                        value = String.valueOf(dataSnapshot1.child("userPhoneNumber").getValue());
//                        Log.d(TAG, "onDataChange: value: " + value);
//                        checkNumber = storeNumber(value);
//                        mobile_desc.setText(value);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        forgotSelectBackButtonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPasswordSelectionActivity.this);
                alertDialog.setTitle("Are You Sure?");
                alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ForgotPasswordSelectionActivity.this, ForgotPasswordActivity.class);

                        intent.putExtra("email", email);

                        Pair[] pairs = new Pair[2];
                        pairs[0] = new Pair<View, String>(title, "logo_text");
                        pairs[1] = new Pair<View, String>(forgotSelectBackButtonShow, "transition_back_arrow_button");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordSelectionActivity.this, pairs);
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

        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordSelectionActivity.this, ForgotPasswordMobilePinActivity.class);

                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("email", email);

                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View, String>(title, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordSelectionActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLogin) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ForgotPasswordSelectionActivity.this).create();
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
                    firebaseAuth.sendPasswordResetEmail(email_desc.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(ForgotPasswordSelectionActivity.this, ResetPasswordEmailActivity.class);

                                intent.putExtra("email", email);

                                Pair[] pairs = new Pair[1];

                                pairs[0] = new Pair<View, String>(title, "logo_text");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordSelectionActivity.this, pairs);
                                startActivity(intent, options.toBundle());
                            }
                            else{
                                Toast.makeText(ForgotPasswordSelectionActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
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

        Log.d(TAG, "onCreate: outside phone: " + phoneNumber);
        Log.d(TAG, "onCreate: outside value: " + value);
    }

    @Override
    public void onBackPressed() {

    }

    public boolean storeNumber(String number){
        phoneNumber = number;
        Log.d(TAG, "storeNumber: Number: " + phoneNumber);

        if (!number.isEmpty()){
            return false;
        }
        else{
            return true;
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
////        insertingData = new InsertingData();
//
//        notifyDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                mobile_desc.setText(dataSnapshot.child("userPhoneNumber").getValue().toString());
//
//                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                    String value = dataSnapshot1.child("userPhone").getValue().toString();
//                    Log.d(TAG, "onDataChange: final values: " + value);
//                }
//
////                for (DataSnapshot displayShapShot: dataSnapshot.getChildren()){
////                    String key = displayShapShot.getKey().toString();
////                    Log.d(TAG, "onDataChange: got key: " + key);
////                    notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child(key);
////                    Log.d(TAG, "onDataChange: database: " + notifyDatabase);
//////                    String phoneNumber = insertingData.getUserPhoneNumber();
//////                    Log.d(TAG, "onDataChange: phone number: " + phoneNumber);
////                    InsertingData insertingData = dataSnapshot.getValue(InsertingData.class);
////                    Log.d(TAG, "onDataChange: insering data" + insertingData);
////                    Log.d(TAG, "onDataChange: phone number" + insertingData.getUserPhoneNumber());
////                    notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child(key).child("userPhoneNumber");
////                    Log.d(TAG, "onDataChange: values: " + notifyDatabase);
//////                    String result = dataSnapshot.child(key).child("userPhoneNumber").getValue().toString();
//////                    Log.d(TAG, "onDataChange: result: " + result);
//////                    Log.d(TAG, "onDataChange: got value: " + dataSnapshot.child("userPhoneNumber").getValue().toString());
////                }
//
////                int position = 0;
////
////                for (DataSnapshot displayShapShot: dataSnapshot.getChildren()){
////                    Log.d(TAG, "onDataChange: SnapShot:" + displayShapShot);
////                    Log.d(TAG, "onDataChange: SnapShot Value:" + displayShapShot.getValue());
////                    Log.d(TAG, "onDataChange: SnapShot Value:" + displayShapShot.getKey());
////
////                    RetrivingData retrivingData = displayData.get(position);
////                    Log.d(TAG, "onDataChange: retrivingData: " + retrivingData);
////
//////                    String key = displayShapShot.getKey().toString();
//////                    Log.d(TAG, "onDataChange: SnapShot Value:" + displayShapShot.child(key).getKey());
//////                    Log.d(TAG, "onDataChange: SnapShot Value:" + displayShapShot.child("userPhoneNumber").getValue().toString());
//////                    int count = Integer.parseInt((String) displayShapShot.getValue());
//////                    for (int i=0; i<count; i++){
//////                        Log.d(TAG, "onDataChange: count: " + i + " value: " + displayShapShot.child("userPhoneNumber").getValue().toString());
//////                    }
////                }
////                for (DataSnapshot displayShapshot: dataSnapshot.getChildren()){
////                    Log.d(TAG, "onDataChange: Shapshot: " + displayShapshot);
////                    Log.d(TAG, "onDataChange: Shapshot: " + displayShapshot.getValue());
////                    StoreNotificationData storeData = displayShapshot.getValue(StoreNotificationData.class);
////                    Log.d(TAG, "onDataChange: Store Data: " + storeData);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

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
