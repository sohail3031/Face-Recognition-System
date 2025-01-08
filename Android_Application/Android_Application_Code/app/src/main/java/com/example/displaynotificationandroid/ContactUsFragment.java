package com.example.displaynotificationandroid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactUsFragment extends Fragment {

    private static final int REQUEST_CALL = 1;
    private static final int REQUEST_SMS = 0;
    private static final int LOCATION_PERMISSION_CODE = 2;

    DatabaseReference notifyDatabase;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    boolean isWifiConn, isMobileConn, checkLogin = false;

    String phoneNumber, email, url, location, latitude, longitude, smsText, subject, message, sendTo, callNumber;
    final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private EditText smsData;
    private EditText subEmail;
    private EditText msgEmail;
    private EditText toAddress;
    private EditText makeCall;
    private EditText showLocation;
    private EditText showWebPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        Button contactCallButton = view.findViewById(R.id.contactCallButton);
        Button contactCallButtonTwo = view.findViewById(R.id.contactCallButtonTwo);
        Button contactCallButtonThree = view.findViewById(R.id.contactCallButtonThree);
        Button contactCallButtonFour = view.findViewById(R.id.contactCallButtonFour);
        Button contactCallButtonFive = view.findViewById(R.id.contactCallButtonFive);

        final TextView contactPhoneText = view.findViewById(R.id.contactPhoneText);
        final TextView contactPhoneTextTwo = view.findViewById(R.id.contactPhoneTextTwo);
        final TextView contactPhoneTextThree = view.findViewById(R.id.contactPhoneTextThree);
        final TextView contactPhoneTextFour = view.findViewById(R.id.contactPhoneTextFour);
        final TextView contactPhoneTextFive = view.findViewById(R.id.contactPhoneTextFive);
        final TextView userNameContact = view.findViewById(R.id.userNameContact);
        final TextView userEmailInfoContact = view.findViewById(R.id.userEmailInfoContact);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

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

        if (checkLogin) {
            notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
            notifyDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        String firstKey = dataSnapshot1.getKey();
                        Log.d("NewTag", "onDataChange: first: " + firstKey);
                        String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                        Log.d("NewTag", "onDataChange: second: " + databaseEmail);
                        if (userEmail.equals(databaseEmail)) {
                            userNameContact.setText(String.valueOf(dataSnapshot1.child("userFirstName").getValue())
                                    + " " + String.valueOf(dataSnapshot1.child("userLastName").getValue()));
                            userEmailInfoContact.setText(String.valueOf(dataSnapshot1.child("userEmailId").getValue()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Contact Information");
            notifyDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        String firstKey = dataSnapshot1.getKey();
                        Log.d("NewTag", "onDataChange: Contact Information");
                        Log.d("NewTag", "onDataChange: first: " + firstKey);

                        phoneNumber = String.valueOf(dataSnapshot1.child("contactPhone").getValue());
                        email = String.valueOf(dataSnapshot1.child("contactEmail").getValue());
                        url = String.valueOf(dataSnapshot1.child("contactWebSite").getValue());
                        location = String.valueOf(dataSnapshot1.child("contactAddress").getValue());
                        latitude = String.valueOf(dataSnapshot1.child("contactCoordinatesLatitude").getValue());
                        longitude = String.valueOf(dataSnapshot1.child("contactCoordinatesLongitude").getValue());

                        contactPhoneText.setText(phoneNumber);
                        contactPhoneTextTwo.setText(email);
                        contactPhoneTextThree.setText(url);
                        contactPhoneTextFour.setText(location);
                        contactPhoneTextFive.setText(phoneNumber);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            contactCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View newView = inflater.inflate(R.layout.layout_call, null);

                    alertDialog.setView(newView)
                            .setTitle("Make A Call")
                            .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    makePhoneCall();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(getContext(), "Calling Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                    makeCall = newView.findViewById(R.id.dialogCall);
                    makeCall.setText(phoneNumber);

                    alertDialog.show();
                }
            });

            contactCallButtonTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View newView = inflater.inflate(R.layout.layout_dialog_email, null);

                    alertDialog.setView(newView)
                            .setTitle("Write email")
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    subject = subEmail.getText().toString();
                                    message = msgEmail.getText().toString();

                                    sendMail();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(getContext(), "Email Send Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                    subEmail = newView.findViewById(R.id.subjectEmail);
                    msgEmail = newView.findViewById(R.id.messageEmail);
                    toAddress = newView.findViewById(R.id.toEmailAddress);
                    toAddress.setText(email);

                    alertDialog.show();
                }
            });

            contactCallButtonThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View newView = inflater.inflate(R.layout.layout_web_page, null);

                    alertDialog.setView(newView)
                            .setTitle("Open Web Page")
                            .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(getContext(), "Could not open web page", Toast.LENGTH_SHORT).show();
                                }
                            });

                    showWebPage = newView.findViewById(R.id.dialogWebPage);
                    showWebPage.setText(url);

                    alertDialog.show();
                }
            });

            contactCallButtonFour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View newView = inflater.inflate(R.layout.layout_location, null);

                            alertDialog.setView(newView)
                                    .setTitle("Go To Location")
                                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //                    ShowLocationFragment showLocationFragment = new ShowLocationFragment();
                                            ShowLocationFragment showLocationFragment = ShowLocationFragment.newInstance(latitude, longitude);
                                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                            fragmentTransaction.replace(R.id.frame_container, showLocationFragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Toast.makeText(getContext(), "Could not open location", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            showLocation = newView.findViewById(R.id.dialogLocation);
                            showLocation.setText(location);

                            alertDialog.show();
                        }
                        else {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
//                            Toast.makeText(getActivity(), "To Perform this action, we need to access your location", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
//                        Toast.makeText(getActivity(), "To perform this action, we need to access your device location", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            contactCallButtonFive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View newView = inflater.inflate(R.layout.layout_dialog_sms, null);

                        alertDialog.setView(newView)
                                .setTitle("Enter your message")
                                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        smsText = smsData.getText().toString();

                                        sendSMS();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Toast.makeText(getContext(), "Message Send Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        smsData = newView.findViewById(R.id.dialogSMS);

                        alertDialog.show();
                    }
                    else {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.SEND_SMS}, REQUEST_SMS);
//                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Connect to the internet", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void sendSMS() {
        String smsNumber = phoneNumber;
        String smsMessage = smsText;

        if (smsNumber != null || smsNumber.trim().length() > 0) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, smsMessage, null, null);

            Toast.makeText(getContext(), "Message Send", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMail() {
//        String subject = "jhjhb";
//        String message = "hjvjhvjh";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
//        intent.putExtra(Intent.EXTRA_CC, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose An Email Client"));
    }

    private void makePhoneCall() {
        String number = phoneNumber;
        if (number.trim().length() > 0 || number != null){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }
            else{
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if (requestCode == REQUEST_SMS){
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View newView = inflater.inflate(R.layout.layout_dialog_sms, null);

                        alertDialog.setView(newView)
                                .setTitle("Enter your message")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        smsText = smsData.getText().toString();

                                        sendSMS();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Toast.makeText(getContext(), "Message Cannot Be Send", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        smsData = newView.findViewById(R.id.dialogSMS);

                        alertDialog.show();
                    }
                    else{
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 1:
                if (requestCode == REQUEST_CALL){
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        makePhoneCall();
                    }
                    else{
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 2:
                if (requestCode == LOCATION_PERMISSION_CODE) {
                    ShowLocationFragment showLocationFragment = ShowLocationFragment.newInstance(latitude, longitude);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, showLocationFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
        }
    }
}
