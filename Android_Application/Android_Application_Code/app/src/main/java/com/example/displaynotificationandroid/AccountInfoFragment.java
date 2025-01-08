package com.example.displaynotificationandroid;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Objects;

public class AccountInfoFragment extends Fragment {

    boolean validPassword, checkLogin, isMobileConn, isWifiConn, isDataEdited = false;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference notifyDatabase;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;
    SensorManager sensorManager;

    String userPassword, phoneNumber;

    private EditText password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);

        final String userEmailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final Button SignUpModifyAccount = view.findViewById(R.id.SignUpModifyAccount);
        Button UpdateProfileAccount = view.findViewById(R.id.UpdateProfileAccount);

        final EditText firstNameAccount = view.findViewById(R.id.firstNameAccount);
        final EditText lastNameAccount = view.findViewById(R.id.lastNameAccount);
        final EditText emailAccount = view.findViewById(R.id.emailAccount);
        final EditText phoneNumberThreeAccount = view.findViewById(R.id.phoneNumberThreeAccount);

        phoneNumber = phoneNumberThreeAccount.getText().toString();

        final TextView userNameAccount = view.findViewById(R.id.userNameAccount);
        final TextView userEmailInfoAccount = view.findViewById(R.id.userEmailInfoAccount);
        final TextView userTypeAccountInfo = view.findViewById(R.id.userTypeAccountInfo);

        final CountryCodePicker cpp = view.findViewById(R.id.countryCodeAccount);
        cpp.registerCarrierNumberEditText(phoneNumberThreeAccount);
        final String fullNumber;
        fullNumber = cpp.getFullNumberWithPlus();
//        Toast.makeText(getContext(), "Country Code: " + fullNumber, Toast.LENGTH_SHORT).show();

        Switch openWifiButtonAccount = view.findViewById(R.id.openWifiButtonAccount);
        Switch openMobileDataAccount = view.findViewById(R.id.openMobileDataAccount);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

//        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
                Log.d("NewTag", "onCreate: wifi");
                checkLogin = true;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
                Log.d("NewTag", "onCreate: data");
                checkLogin = true;
            }
        }

        Log.d("NewTag", "onCreate: Login Internet: " + activeNetworkInfo);

        if (checkLogin) {
//            Context context = null;
//            String locale = context.getResources().getConfiguration().locale.getCountry();

//            TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//            String countryCodeValue = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                countryCodeVal0ue = tm.getManufacturerCode();
//            }
//            Toast.makeText(getContext(), "Country Code: " + countryCodeValue, Toast.LENGTH_SHORT).show();

            notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
            notifyDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        String firstKey = dataSnapshot1.getKey();
                        Log.d("NewTag", "onDataChange: first: " + firstKey);
                        String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                        Log.d("NewTag", "onDataChange: second: " + databaseEmail);
                        if (userEmailAddress.equals(databaseEmail)) {
                            firstNameAccount.setText(String.valueOf(dataSnapshot1.child("userFirstName").getValue()));
                            lastNameAccount.setText(String.valueOf(dataSnapshot1.child("userLastName").getValue()));
                            emailAccount.setText(String.valueOf(dataSnapshot1.child("userEmailId").getValue()));
                            phoneNumberThreeAccount.setText(String.valueOf(dataSnapshot1.child("userPhoneNumber").getValue()));
                            userNameAccount.setText(String.valueOf(dataSnapshot1.child("userFirstName").getValue())
                                    + " " + String.valueOf(dataSnapshot1.child("userLastName").getValue()));
                            userEmailInfoAccount.setText(String.valueOf(dataSnapshot1.child("userEmailId").getValue()));
                            userTypeAccountInfo.setText(String.valueOf(dataSnapshot1.child("userTypeAccount").getValue()));

                            userPassword = String.valueOf(dataSnapshot1.child("userPassword").getValue());

                            break;
                        }
                        else {
                            continue;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            SignUpModifyAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View newView = inflater.inflate(R.layout.layout_dialog, null);

                    alertDialog.setView(newView)
                            .setTitle("To modify your data, please enter your password")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String enteredPassword = password.getText().toString();

                                    if (enteredPassword.equals(userPassword)) {
                                        isDataEdited = true;

                                        Toast.makeText(getContext(), "You Can Now Update Your Data", Toast.LENGTH_SHORT).show();

                                        firstNameAccount.setClickable(true);
                                        firstNameAccount.setCursorVisible(true);
                                        firstNameAccount.setFocusable(true);
                                        firstNameAccount.setFocusableInTouchMode(true);

                                        lastNameAccount.setClickable(true);
                                        lastNameAccount.setCursorVisible(true);
                                        lastNameAccount.setFocusable(true);
                                        lastNameAccount.setFocusableInTouchMode(true);

                                        phoneNumberThreeAccount.setClickable(true);
                                        phoneNumberThreeAccount.setCursorVisible(true);
                                        phoneNumberThreeAccount.setFocusable(true);
                                        phoneNumberThreeAccount.setFocusableInTouchMode(true);
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Password is invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(getContext(), "You cannot modify your data", Toast.LENGTH_SHORT).show();
                                }
                            });

                    password = newView.findViewById(R.id.dialogPassword);

                    alertDialog.show();
                }
            });

            UpdateProfileAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDataEdited) {
                        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

                        notifyDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                    String firstKey = dataSnapshot1.getKey();
                                    Log.d("NewTag", "onDataChange: first: " + firstKey);
                                    String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                                    Log.d("NewTag", "onDataChange: second: " + databaseEmail);
                                    if (userEmailAddress.equals(databaseEmail)) {
                                        Log.d("NewTag", "onDataChange: Email Found!");

                                        notifyDatabase.child(firstKey).child("userFirstName").setValue(firstNameAccount.getText().toString());
                                        notifyDatabase.child("Updated Profile Data").child(firstKey).child("userFirstName").setValue(firstNameAccount.getText().toString());

                                        notifyDatabase.child(firstKey).child("userLastName").setValue(lastNameAccount.getText().toString());
                                        notifyDatabase.child("Updated Profile Data").child(firstKey).child("userLastName").setValue(lastNameAccount.getText().toString());

                                        notifyDatabase.child(firstKey).child("userPhoneNumber").setValue(cpp.getFullNumberWithPlus());
                                        notifyDatabase.child("Updated Profile Data").child(firstKey).child("userPhoneNumber").setValue(cpp.getFullNumberWithPlus());

                                        Toast.makeText(getContext(), "Your Profile Has Been Updated", Toast.LENGTH_SHORT).show();

                                        isDataEdited = false;

                                        firstNameAccount.setClickable(false);
                                        firstNameAccount.setCursorVisible(false);
                                        firstNameAccount.setFocusable(false);
                                        firstNameAccount.setFocusableInTouchMode(false);

                                        lastNameAccount.setClickable(false);
                                        lastNameAccount.setCursorVisible(false);
                                        lastNameAccount.setFocusable(false);
                                        lastNameAccount.setFocusableInTouchMode(false);

                                        phoneNumberThreeAccount.setClickable(false);
                                        phoneNumberThreeAccount.setCursorVisible(false);
                                        phoneNumberThreeAccount.setFocusable(false);
                                        phoneNumberThreeAccount.setFocusableInTouchMode(false);

                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

//                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                        firebaseAuth = FirebaseAuth.getInstance();
//                        firebaseAuth.getCurrentUser();
//
//                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(userEmailAddress, userPassword);
//
//                        firebaseUser.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//
//                            }
//                        });
                    }
                }
            });

            openWifiButtonAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if(isChecked)
                    {
                        Log.d("NewTag", "onCheckedChanged: if");
                        EnableWiFi() ;
                    }
                    else {
                        Log.d("NewTag", "onCheckedChanged: else");
                        DisableWiFi();
                    }
                }
            });

            openWifiButtonAccount.setChecked(getMobileDataState());
            openWifiButtonAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setMobileDataState(isChecked);
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Please Connect To The Internet", Toast.LENGTH_SHORT).show();

            openWifiButtonAccount.setVisibility(View.VISIBLE);
            openMobileDataAccount.setVisibility(View.VISIBLE);

            openWifiButtonAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if(isChecked)
                    {
                        Log.d("NewTag", "onCheckedChanged: if");
                        EnableWiFi() ;
                    }
                    else {
                        Log.d("NewTag", "onCheckedChanged: else");
                        DisableWiFi();
                    }
                }
            });

            openMobileDataAccount.setChecked(getMobileDataState());
            openMobileDataAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setMobileDataState(isChecked);
                }
            });
        }

        return view;
    }

    private void setMobileDataState(boolean isChecked) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            Log.d("NewTag", "setMobileDataState: mobile data setdata method");
            setMobileDataEnabledMethod.invoke(telephonyService, isChecked);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error setting mobile data state", ex);
        }
//        try
//        {
//            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
//            if (null != setMobileDataEnabledMethod)
//            {
//                setMobileDataEnabledMethod.invoke(telephonyService, isChecked);
//                setMobileDataEnabledMethod.setAccessible(true);
//            }
//        }
//        catch (Exception ex)
//        {
//            Log.e(TAG, "Error setting mobile data state", ex);
//        }
    }

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("getDataEnabled");
            Log.d("NewTag", "getMobileDataState: mobile data getdata method ");
            return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error getting mobile data state", ex);
        }
        return false;
    }

    public void EnableWiFi(){
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(getContext(), "Your now connected to internet", Toast.LENGTH_SHORT).show();
        Log.d("NewTag", "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = true;

//        finish();
//        startActivity(getIntent());

//        AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frame_container, accountInfoFragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
    }

    public void DisableWiFi(){
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(getContext(), ":-( connection lost", Toast.LENGTH_SHORT).show();
        Log.d("NewTag", "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = false;
    }

}
