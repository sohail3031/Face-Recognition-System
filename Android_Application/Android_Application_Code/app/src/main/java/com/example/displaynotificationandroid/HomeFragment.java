package com.example.displaynotificationandroid;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
//    TextView t1, t2;

    DatabaseReference notifyDatabase;

    StoreNotificationData store;

    private String title, message, personName, loss, description, image, dateTime, firstTime, lastTime,
            titleNext, messageNext, personNameNext, lossNext, descriptionNext, imageNext, dateTimeNext,
            firstTimeNext, lastTimeNext, notificationCounterString, loginAs, personStatus = "";
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
    private static final String Arg_Title_NEXT = "title";
    private static final String Arg_Message_NEXT = "message";
    private static final String Arg_Name_NEXT = "name";
    private static final String Arg_Loss_NEXT = "loss";
    private static final String Arg_Description_NEXT = "description";
    private static final String Arg_Image_NEXT = "image";
    private static final String Arg_DATE_TIME_NEXT = "dateTime";
    private static final String Arg_FIRST_TIME_NEXT = "firstTime";
    private static final String Arg_LAST_TIME_NEXT = "lastTime";
    private static final int NOTIFICATION_COUNTER = 0;
    private static final String NOTIFICATION_COUNTER_STRING = "counter";
    private static final String Arg_LOGIN_AS = "login";
    private static final String Arg_PERSON_STATUS = "status";

    public static final String TAG = "MyTag";

    private String[] checkData = new String[5];

    private static int count = 0;

    public static HomeFragment newInstance(String title, String message, String personName,
                                           String loss, String description, String image, String dateTime,
                                           String firstTime, String lastTime, String count, String loginAs, String status){
        HomeFragment homeFragment = new HomeFragment();
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
        arg.putString(Arg_PERSON_STATUS, status);
        arg.putString(NOTIFICATION_COUNTER_STRING, count);

        homeFragment.setArguments(arg);
        return homeFragment;
    }

    public static HomeFragment sedondInstance(String title, String message, String personName,
                                              String loss, String description, String image, String dateTime,
                                              String firstTime, String lastTime, String count, String loginAs){
        HomeFragment homeFragment = new HomeFragment();
        Bundle arg = new Bundle();
        arg.putString(Arg_Title_NEXT, title);
        arg.putString(Arg_Message_NEXT, message);
        arg.putString(Arg_Name_NEXT, personName);
        arg.putString(Arg_Loss_NEXT, loss);
        arg.putString(Arg_Description_NEXT, description);
        arg.putString(Arg_Image_NEXT, image);
        arg.putString(Arg_DATE_TIME_NEXT, dateTime);
        arg.putString(Arg_FIRST_TIME_NEXT, firstTime);
        arg.putString(Arg_LAST_TIME_NEXT, lastTime);
        arg.putString(NOTIFICATION_COUNTER_STRING, count);
        arg.putString(Arg_LOGIN_AS, loginAs);

        homeFragment.setArguments(arg);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

//        Toast.makeText(getContext(), "Count is : " + count, Toast.LENGTH_SHORT).show();
//        count += 1;
//        Toast.makeText(getContext(), "Now Count is : " + count, Toast.LENGTH_SHORT).show();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mHandler, new IntentFilter("com.example.displaynotificationandroid_FCM-MESSAGE"));

        final TextView t1 = v.findViewById(R.id.first);
        TextView t2 = v.findViewById(R.id.second);
        Button homeButton = v.findViewById(R.id.homeButton);
        Button notificationButton = v.findViewById(R.id.notificationButton);
        Button notificationListButton = v.findViewById(R.id.notificationListButton);
        Button logoutButton = v.findViewById(R.id.logoutButton);
        Button rateButton = v.findViewById(R.id.rateButton);
        Button accountButton = v.findViewById(R.id.accountButton);
        Button helpButton = v.findViewById(R.id.helpButton);
        Button resetPasswordButton = v.findViewById(R.id.resetPasswordButton);
        Button chatButton = v.findViewById(R.id.chatButton);
        Button shareButton = v.findViewById(R.id.shareButton);
        Button addEmployeeButton = v.findViewById(R.id.addEmployeeButton);

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

//            if (notificationCounterString == "yes") {
//                t1.setText(personName);
//
//                notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
//                addNotificationData();
//            }
//            else {
//                t1.setText("No Notification");
//            }

            checkData[0] = image;

//            t2.setText("You have " + info + " new message's.");
        }

        Log.d(TAG, "onCreateView: title: " + title);

//        if (title == "Person Identified") {
//            Log.d(TAG, "onCreateView: inside if");
//            t2.setText("You have " + info + " new message's.");
//        }
//        else {
//            t2.setVisibility(View.INVISIBLE);
//        }

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Send Message", Toast.LENGTH_SHORT).show();
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NotificationFragment notificationFragment = new NotificationFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(((ViewGroup).getView().getParent()).getId(), notificationFragment, "findThisFragment")
//                        .addToBackStack(null)
//                        .commit();
//                Intent intent = new Intent(getActivity(), NotificationFragment.class);
//                startActivity(intent);

//                NotificationFragment notificationFragment = new NotificationFragment();
                NotificationFragment notificationFragment = NotificationFragment.newInstance(title,
                        message, personName, loss, description, image, dateTime, firstTime, lastTime, loginAs, personStatus);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, notificationFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        notificationListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationListFragment notificationListFragment = NotificationListFragment.newInstance(title,
                        message, personName, loss, description, image, dateTime, firstTime, lastTime, loginAs, personStatus);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, notificationListFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, accountInfoFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, resetPasswordFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUsFragment contactUsFragment = new ContactUsFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, contactUsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEmployeeFragment addEmployeeFragment = new AddEmployeeFragment();
                FragmentTransaction fragmentTransaction= getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, addEmployeeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plane");

                String shareLink = "Download This Application Now: https://play.google.com/store/apps/details?id=com.android.chrome&h1=en";
                String shareSub = "Face Recognition System App";

                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareLink);

                startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("market://details?id=" + getActivity().getPackageName())));
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.android.chrome")));
                }
                catch (ActivityNotFoundException e){
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.android.chrome")));
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Are You Sure?");
                alertDialog.setMessage("Are you sure you want to logout?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), LoginActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(t1, "logo_text");

                        FirebaseAuth.getInstance().signOut();

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
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

        return v;
    }

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RemoteMessage remoteMessage = null;
//            String title = intent.getStringExtra("title");
//            String message = intent.getStringExtra("message");
//            String imageURI = intent.getStringExtra("image");
        }
    };

    private void addNotificationData(){
//        String addTitle = title.trim();
//        String addMessage = message.trim();
//        String addPersonName = personName.trim();
//        String addLoss = loss.trim();
//        String addDescription = description.trim();
//        String addImage = image.trim();
//
//        StoreNotificationData storeData = new StoreNotificationData(addTitle, addMessage, addPersonName, addLoss, addDescription, addImage);

        store = new StoreNotificationData();

        store.setNotifyTitle(title);
        store.setNotifyMessage(message);
        store.setNotifyPersonName(personName);
        store.setNotifyLoss(loss);
        store.setNotifyDescription(description);
        store.setNotifyImage(image);
        store.setNotifyDateTime(dateTime);
        store.setNotifyFirstTime(firstTime);
        store.setNotifyLastTime(lastTime);

        notifyDatabase.push().setValue(store);

        Log.d(TAG, "addNotificationData: Notification Data Inserted!");
    }
}
