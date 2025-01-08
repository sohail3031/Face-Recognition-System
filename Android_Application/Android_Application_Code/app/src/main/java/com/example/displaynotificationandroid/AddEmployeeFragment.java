package com.example.displaynotificationandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class AddEmployeeFragment extends Fragment {

    private EditText addEmpEmail, password;
    TextView addEmpName, addEmpEmailTitle;
    Spinner addEmpType;
    Button addEmpButton;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference notifyDatabase, newUserDB;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;
    SensorManager sensorManager;

    boolean validPassword, checkLogin, isMobileConn, isWifiConn, isDataEdited = false;
    String checkPassword;

    AddNewUser addNewUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_employee, container, false);

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

        addEmpEmail = view.findViewById(R.id.addEmpEmail);
        addEmpName = view.findViewById(R.id.addEmpName);
        addEmpEmailTitle = view.findViewById(R.id.addEmpEmailTitle);
        addEmpType = view.findViewById(R.id.addEmpType);
        addEmpButton = view.findViewById(R.id.addEmpButton);

        if (checkLogin) {
            final String userEmailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();

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
                            addEmpName.setText(String.valueOf(dataSnapshot1.child("userFirstName").getValue())
                            + " " + String.valueOf(dataSnapshot1.child("userLastName").getValue()));
                            addEmpEmailTitle.setText(String.valueOf(dataSnapshot1.child("userEmailId").getValue()));
                            checkPassword = String.valueOf(dataSnapshot1.child("userPassword").getValue());
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

            String[] items = new String[]{"Select Employee", "Admin", "Worker"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
            addEmpType.setAdapter(adapter);

            addEmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!String.valueOf(addEmpType.getSelectedItem()).equals("Select Employee")) {
                        if (validateEmail(addEmpEmail.getText().toString())) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View newView = inflater.inflate(R.layout.layout_dialog, null);

                            alertDialog.setView(newView)
                                    .setTitle("To modify your data, please enter your password")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                                            String enteredPassword = password.getText().toString();

                                            if (enteredPassword.equals(checkPassword)) {
//                                                Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_SHORT).show();

                                                addNewUser = new AddNewUser();
                                                newUserDB = FirebaseDatabase.getInstance().getReference().child("Added New User");

                                                addNewUser.setUserEmail(addEmpEmail.getText().toString());
                                                addNewUser.setUserType(String.valueOf(addEmpType.getSelectedItem()));
                                                newUserDB.push().setValue(addNewUser);

                                                Toast.makeText(getContext(), "Employee Added", Toast.LENGTH_SHORT).show();

                                                addEmpEmail.setText("");
//                                                addEmpEmail.setFocusable(false);
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
                        else{
                            Toast.makeText(getContext(), "Email Address is invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "Please Select Employee Type", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(getContext(), "Please Connect To The Internet", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public boolean validateEmail(String userEmail){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        if (userEmail.isEmpty()){
            addEmpEmail.setError("Email Address cannot be empty");
            addEmpEmail.requestFocus();
            return false;
        }
        else {
            if (pat.matcher(userEmail).matches()) {
                return true;
            }
            else{
                addEmpEmail.setError("Email Address is not valid");
                addEmpEmail.requestFocus();
                return false;
            }
        }
    }
}
