package com.example.displaynotificationandroid;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

public class NotificationListShowFragment extends Fragment {

    //    TextView t1, t2;
    private String title, message, personName, loss, description, image, dateTime, firstTime, lastTime,
            notificationCounterString, loginAs, personStatus, foundKey, foundImage, deleteFoundKey;
    private int info = 0;

    private static final String Arg_Title = "title";
    private static final String Arg_Message = "message";
    private static final String Arg_Name = "name";
    private static final String Arg_Loss = "loss";
    private static final String Arg_Description = "description";
    private static final String Arg_Image = "image";
    private static final String Arg_DATE_TIME = "dateTime";
    private static final String Arg_FIRST_TIME = "firstTime";
    private static final String Arg_LAST_TIME = "lastTime";
    private static final String Arg_Number = "number";
    private static final String Arg_LOGIN_AS = "login";
    private static final String Arg_PERSON_STATUS = "status";
    private static final int NOTIFICATION_COUNTER = 0;
    private static final String NOTIFICATION_COUNTER_STRING = "counter";

    public static final String TAG = "MyTag";

    private static int SPLASH_TIME_OUT = 3000;

    Button logoutButton, modifyNotification, deleteNotification, updateNotification;
    TextView text;
    FirebaseAuth mFirebaseAuth;
    ImageView userImage;
    EditText displayPersonName, displayPersonLoss, displayNotificationDateTime, displayPersonFirstIdentified,
            displayPersonLastIdentified, displayPersonDescription, displayPersonStatusShow;

    DatabaseReference notifyDatabase, mDatabase;
    StoreNotificationData store;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    public static NotificationListShowFragment newInstance(String title, String message, String personName,
                                                   String loss, String description, String image, String dateTime,
                                                   String firstTime, String lastTime, String loginAs, String status){
        NotificationListShowFragment notificationFragment = new NotificationListShowFragment();
        Bundle arg = new Bundle();
        arg.putString(Arg_Title, title);
        arg.putString(Arg_Message, message);
        arg.putString(Arg_Name, personName);
        arg.putString(Arg_Loss, loss);
        arg.putString(Arg_Description, description);
        arg.putString(Arg_Image, image);
        arg.putString(Arg_DATE_TIME, dateTime);
        arg.putString(Arg_FIRST_TIME, firstTime);
        arg.putString(Arg_LAST_TIME, lastTime);
        arg.putString(Arg_LOGIN_AS, loginAs);
        arg.putString(Arg_PERSON_STATUS, status);

        notificationFragment.setArguments(arg);
        return notificationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list_show, container, false);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mHandler, new IntentFilter("com.example.displaynotificationandroid_FCM-MESSAGE"));

        userImage = view.findViewById(R.id.userImageShow);
        displayPersonName = view.findViewById(R.id.displayPersonNameShow);
        displayPersonLoss = view.findViewById(R.id.displayPersonLossShow);
        displayPersonDescription = view.findViewById(R.id.displayPersonDescriptionShow);
        displayNotificationDateTime = view.findViewById(R.id.displayNotificationDateTimeShow);
        displayPersonFirstIdentified = view.findViewById(R.id.displayPersonFirstIdentifiedShow);
        displayPersonLastIdentified = view.findViewById(R.id.displayPersonLastIdentifiedShow);
        modifyNotification = view.findViewById(R.id.modifyNotificationShow);
        deleteNotification = view.findViewById(R.id.deleteNotificationShow);
        updateNotification = view.findViewById(R.id.updateNotificationShow);
        displayPersonStatusShow = view.findViewById(R.id.displayPersonStatusShow);

        if (getArguments() != null) {
            title = getArguments().getString(Arg_Title);
            message = getArguments().getString(Arg_Message);
            personName = getArguments().getString(Arg_Name);
            loss = getArguments().getString(Arg_Loss);
            description = getArguments().getString(Arg_Description);
            image = getArguments().getString(Arg_Image);
            dateTime = getArguments().getString(Arg_DATE_TIME);
            firstTime = getArguments().getString(Arg_FIRST_TIME);
            lastTime = getArguments().getString(Arg_LAST_TIME);
            info = getArguments().getInt(Arg_Number);
            loginAs = getArguments().getString(Arg_LOGIN_AS);
            personStatus = getArguments().getString(Arg_PERSON_STATUS);
            notificationCounterString = getArguments().getString(NOTIFICATION_COUNTER_STRING);

//            Toast.makeText(getContext(), "got data: " + description, Toast.LENGTH_SHORT).show();
        }

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
                        loginAs = String.valueOf(dataSnapshot1.child("userTypeAccount").getValue());

                        if (loginAs.equalsIgnoreCase("admin")) {
                            modifyNotification.setVisibility(View.VISIBLE);
                            deleteNotification.setVisibility(View.VISIBLE);
                        }

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

        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
        notifyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String firstKey = dataSnapshot1.getKey();
                    Log.d("NewTag", "onDataChange: first: " + firstKey);
                    String databaseImage = String.valueOf(dataSnapshot1.child("notifyImage").getValue());
                    Log.d("NewTag", "onDataChange: second: " + databaseImage);
                    if (image.equals(databaseImage)) {
                        foundImage = String.valueOf(dataSnapshot1.child("notifyImage").getValue());
                        foundKey = firstKey;

                        Log.d("sohail", "onDataChange: url found: " + foundImage);
                        Log.d("sohail", "onDataChange: url key: " + foundKey);

                        if (loginAs.equalsIgnoreCase("admin")) {
                            modifyNotification.setVisibility(View.VISIBLE);
                            deleteNotification.setVisibility(View.VISIBLE);
                        }

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

        displayPersonName.setText(personName);
        displayPersonLoss.setText(loss);
        displayPersonDescription.setText(description);
        displayNotificationDateTime.setText(dateTime);
        displayPersonFirstIdentified.setText(firstTime);
        displayPersonLastIdentified.setText(lastTime);
        displayPersonStatusShow.setText(personStatus);

        if (image == null) {
            Picasso.with(getContext())
                    .load(R.drawable.ic_android_white_24dp)
                    .placeholder(R.drawable.ic_android_white_24dp)
                    .into(userImage);
        }
        else{
            Picasso.with(getContext())
                    .load(image)
                    .placeholder(R.drawable.ic_android_white_24dp)
                    .into(userImage);
        }

        modifyNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPersonName.setClickable(true);
                displayPersonName.setCursorVisible(true);
                displayPersonName.setFocusable(true);
                displayPersonName.setFocusableInTouchMode(true);

                displayPersonLoss.setClickable(true);
                displayPersonLoss.setCursorVisible(true);
                displayPersonLoss.setFocusable(true);
                displayPersonLoss.setFocusableInTouchMode(true);

//                displayNotificationDateTime.setClickable(true);
//                displayNotificationDateTime.setCursorVisible(true);
//                displayNotificationDateTime.setFocusable(true);
//                displayNotificationDateTime.setFocusableInTouchMode(true);

                displayPersonFirstIdentified.setClickable(true);
                displayPersonFirstIdentified.setCursorVisible(true);
                displayPersonFirstIdentified.setFocusable(true);
                displayPersonFirstIdentified.setFocusableInTouchMode(true);

                displayPersonLastIdentified.setClickable(true);
                displayPersonLastIdentified.setCursorVisible(true);
                displayPersonLastIdentified.setFocusable(true);
                displayPersonLastIdentified.setFocusableInTouchMode(true);

                displayPersonDescription.setClickable(true);
                displayPersonDescription.setCursorVisible(true);
                displayPersonDescription.setFocusable(true);
                displayPersonDescription.setFocusableInTouchMode(true);

                displayPersonStatusShow.setClickable(true);
                displayPersonStatusShow.setCursorVisible(true);
                displayPersonStatusShow.setFocusable(true);
                displayPersonStatusShow.setFocusableInTouchMode(true);

                updateNotification.setVisibility(View.VISIBLE);
            }
        });

        updateNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
                notifyDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String firstKey = dataSnapshot1.getKey();
                            Log.d("NewTag", "onDataChange: first: " + firstKey);
                            String databaseImage = String.valueOf(dataSnapshot1.child("notifyImage").getValue());
                            Log.d("NewTag", "onDataChange: second: " + databaseImage);
                            if (image.equals(databaseImage)) {
                                foundImage = String.valueOf(dataSnapshot1.child("notifyImage").getValue());
                                foundKey = firstKey;

                                Log.d("sohail", "onDataChange: url found: " + foundImage);
                                Log.d("sohail", "onDataChange: url key: " + foundKey);

                                notifyDatabase.child(firstKey).child("notifyDescription").setValue(displayPersonDescription.getText().toString());
                                notifyDatabase.child(firstKey).child("notifyFirstTime").setValue(displayPersonFirstIdentified.getText().toString());
                                notifyDatabase.child(firstKey).child("notifyLastTime").setValue(displayPersonLastIdentified.getText().toString());
                                notifyDatabase.child(firstKey).child("notifyLoss").setValue(displayPersonLoss.getText().toString());
                                notifyDatabase.child(firstKey).child("notifyPersonName").setValue(displayPersonName.getText().toString());
                                notifyDatabase.child(firstKey).child("notifyPersonStatus").setValue(displayPersonStatusShow.getText().toString());

                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("Updated Notification Data").child(firstKey).child("notifyDescription").setValue(displayPersonDescription.getText().toString());
                                mDatabase.child("Updated Notification Data").child(firstKey).child("notifyFirstTime").setValue(displayPersonFirstIdentified.getText().toString());
                                mDatabase.child("Updated Notification Data").child(firstKey).child("notifyLastTime").setValue(displayPersonLastIdentified.getText().toString());
                                mDatabase.child("Updated Notification Data").child(firstKey).child("notifyLoss").setValue(displayPersonLoss.getText().toString());
                                mDatabase.child("Updated Notification Data").child(firstKey).child("notifyPersonName").setValue(displayPersonName.getText().toString());
                                mDatabase.child("Updated Notification Data").child(firstKey).child("notifyPersonStatus").setValue(displayPersonStatusShow.getText().toString());

                                displayPersonName.setClickable(false);
                                displayPersonName.setCursorVisible(false);
                                displayPersonName.setFocusable(false);
                                displayPersonName.setFocusableInTouchMode(false);

                                displayPersonLoss.setClickable(false);
                                displayPersonLoss.setCursorVisible(false);
                                displayPersonLoss.setFocusable(false);
                                displayPersonLoss.setFocusableInTouchMode(false);

//                displayNotificationDateTime.setClickable(true);
//                displayNotificationDateTime.setCursorVisible(true);
//                displayNotificationDateTime.setFocusable(true);
//                displayNotificationDateTime.setFocusableInTouchMode(true);

                                displayPersonFirstIdentified.setClickable(false);
                                displayPersonFirstIdentified.setCursorVisible(false);
                                displayPersonFirstIdentified.setFocusable(false);
                                displayPersonFirstIdentified.setFocusableInTouchMode(false);

                                displayPersonLastIdentified.setClickable(false);
                                displayPersonLastIdentified.setCursorVisible(false);
                                displayPersonLastIdentified.setFocusable(false);
                                displayPersonLastIdentified.setFocusableInTouchMode(false);

                                displayPersonDescription.setClickable(false);
                                displayPersonDescription.setCursorVisible(false);
                                displayPersonDescription.setFocusable(false);
                                displayPersonDescription.setFocusableInTouchMode(false);

                                displayPersonStatusShow.setClickable(false);
                                displayPersonStatusShow.setCursorVisible(false);
                                displayPersonStatusShow.setFocusable(false);
                                displayPersonStatusShow.setFocusableInTouchMode(false);

                                updateNotification.setVisibility(View.GONE);
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
            }
        });

        deleteNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
                                notifyDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                            String firstKey = dataSnapshot1.getKey();
                                            Log.d("NewTag", "onDataChange: first: " + firstKey);
                                            String databaseImage = String.valueOf(dataSnapshot1.child("notifyImage").getValue());
                                            Log.d("NewTag", "onDataChange: second: " + databaseImage);
                                            if (image.equals(databaseImage)) {
                                                Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_SHORT).show();
                                                foundImage = String.valueOf(dataSnapshot1.child("notifyImage").getValue());
                                                deleteFoundKey = firstKey;
                                            }
                                            else {
                                                continue;
                                            }
                                        }

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!deleteFoundKey.equals("")) {
                                                    Task<Void> deleteValue = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(foundKey).setValue(null);
                                                    deleteValue.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            displayPersonName.setText("");
                                                            displayPersonLoss.setText("");
                                                            displayPersonDescription.setText("");
                                                            displayNotificationDateTime.setText("");
                                                            displayPersonFirstIdentified.setText("");
                                                            displayPersonLastIdentified.setText("");
                                                            displayPersonStatusShow.setText("");

                                                            Picasso.with(getContext())
                                                                    .load(R.drawable.ic_android_white_24dp)
                                                                    .placeholder(R.drawable.ic_android_white_24dp)
                                                                    .into(userImage);

                                                            Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                    Task<Void> deleteValueUpdate = FirebaseDatabase.getInstance().getReference().child("Updated Notification Data").child(foundKey).setValue(null);
                                                    deleteValueUpdate.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show(); }
                                                    });
                                                }
                                            }
                                        }, SPLASH_TIME_OUT);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        
        return view;
    }

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayPersonName.setText("");
            displayPersonLoss.setText("");
            displayPersonDescription.setText("");

            Log.i(TAG, "onReceive: Main Content: " + context);
            Log.i(TAG, "onReceive: Main Intent: " + intent);

            RemoteMessage remoteMessage = null;
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            String imageURI = intent.getStringExtra("image");

            Log.d(TAG, "onReceive: Main " + title);
            Log.d(TAG, "onReceive: Main " + message);
            Log.d(TAG, "onReceive: Main " + imageURI);

            Picasso.with(context).load(imageURI).placeholder(R.drawable.ic_android_black_24dp)
                    .error(R.drawable.ic_android_black_24dp)
                    .into(userImage, new com.squareup.picasso.Callback() {

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });

            displayPersonName.setText(title);
            displayPersonDescription.setText(message);

//            Log.d(TAG, "onReceive: getIntent != null: " + (getIntent() != null));
//            Log.d(TAG, "onReceive: getIntent().hasExtra('key1'): " + getIntent().hasExtra("key1"));
//
//            if (getIntent() != null && getIntent().hasExtra("key1")){
//                Log.d(TAG, "onCreate: From Main Activity");
//                for (String key: getIntent().getExtras().keySet()){
//                    Log.d(TAG, "onCreate: key: " + key + " Data : " + getIntent().getExtras().getString(key));
//                    displayPersonLoss.append(getIntent().getExtras().getString(key) + "\n");
//                }
//            }
        }
    };

//    @Override
//    protected void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
//    }
//
//    @Override
//    public void onBackPressed() {
//
//    }
}
