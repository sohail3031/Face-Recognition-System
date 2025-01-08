package com.example.displaynotificationandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationFragment extends Fragment {
//    TextView t1, t2;
    private String title, message, personName, loss, description, image, dateTime, firstTime, lastTime,
        notificationCounterString, loginAs, personStatus;
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

//    Button logoutButton, modifyNotification, deleteNotification, updateNotification;
    TextView text;
    FirebaseAuth mFirebaseAuth;
    ImageView userImage;
    EditText displayPersonName, displayPersonLoss, displayNotificationDateTime, displayPersonFirstIdentified,
            displayPersonLastIdentified, displayPersonDescription, displayPersonStatusShow;

    DatabaseReference notifyDatabase, mDatabase;
    StoreNotificationData store;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    public static NotificationFragment newInstance(String title, String message, String personName,
                                                   String loss, String description, String image, String dateTime,
                                                   String firstTime, String lastTime, String loginAs, String status){
        NotificationFragment notificationFragment = new NotificationFragment();
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
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mHandler, new IntentFilter("com.example.displaynotificationandroid_FCM-MESSAGE"));

        userImage = v.findViewById(R.id.userImage);
        displayPersonName = v.findViewById(R.id.displayPersonName);
        displayPersonLoss = v.findViewById(R.id.displayPersonLoss);
        displayPersonDescription = v.findViewById(R.id.displayPersonDescription);
        displayNotificationDateTime = v.findViewById(R.id.displayNotificationDateTime);
        displayPersonFirstIdentified = v.findViewById(R.id.displayPersonFirstIdentified);
        displayPersonLastIdentified = v.findViewById(R.id.displayPersonLastIdentified);
        displayPersonStatusShow = v.findViewById(R.id.displayPersonStatus);
//        modifyNotification = v.findViewById(R.id.modifyNotification);
//        deleteNotification = v.findViewById(R.id.deleteNotification);
//        updateNotification = v.findViewById(R.id.updateNotification);

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

//        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
//        notifyDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                    String firstKey = dataSnapshot1.getKey();
//                    Log.d("NewTag", "onDataChange: first: " + firstKey);
//                    String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
//                    Log.d("NewTag", "onDataChange: second: " + databaseEmail);
//                    if (userEmailAddress.equals(databaseEmail)) {
//                        loginAs = String.valueOf(dataSnapshot1.child("userTypeAccount").getValue());
//
//                        if (loginAs.equalsIgnoreCase("admin")) {
//                            modifyNotification.setVisibility(View.VISIBLE);
//                            deleteNotification.setVisibility(View.VISIBLE);
//                        }
//
//                        break;
//                    }
//                    else {
//                        continue;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

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

//        modifyNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayPersonName.setClickable(true);
//                displayPersonName.setCursorVisible(true);
//                displayPersonName.setFocusable(true);
//                displayPersonName.setFocusableInTouchMode(true);
//
//                displayPersonLoss.setClickable(true);
//                displayPersonLoss.setCursorVisible(true);
//                displayPersonLoss.setFocusable(true);
//                displayPersonLoss.setFocusableInTouchMode(true);
//
//                displayNotificationDateTime.setClickable(true);
//                displayNotificationDateTime.setCursorVisible(true);
//                displayNotificationDateTime.setFocusable(true);
//                displayNotificationDateTime.setFocusableInTouchMode(true);
//
//                displayPersonFirstIdentified.setClickable(true);
//                displayPersonFirstIdentified.setCursorVisible(true);
//                displayPersonFirstIdentified.setFocusable(true);
//                displayPersonFirstIdentified.setFocusableInTouchMode(true);
//
//                displayPersonLastIdentified.setClickable(true);
//                displayPersonLastIdentified.setCursorVisible(true);
//                displayPersonLastIdentified.setFocusable(true);
//                displayPersonLastIdentified.setFocusableInTouchMode(true);
//
//                displayPersonDescription.setClickable(true);
//                displayPersonDescription.setCursorVisible(true);
//                displayPersonDescription.setFocusable(true);
//                displayPersonDescription.setFocusableInTouchMode(true);
//
//                updateNotification.setVisibility(View.VISIBLE);
//            }
//        });
//
//        updateNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        deleteNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return v;
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
