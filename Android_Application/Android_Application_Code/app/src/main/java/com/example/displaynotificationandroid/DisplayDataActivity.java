package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DisplayDataActivity extends AppCompatActivity {

    DatabaseReference notifyDatabase;

    List<StoreNotificationData> storeNotificationData;

    String[] nameArray = {"Octopus","Pig","Sheep","Rabbit","Snake","Spider" };

//    String[] infoArray = {
//            "8 tentacled monster",
//            "Delicious in rolls",
//            "Great for jumpers",
//            "Nice in a stew",
//            "Great for shoes",
//            "Scary."
//    };

//    Integer[] imageArray = {R.drawable.ic_accessibility_black_24dp,
//            R.drawable.ic_message_black_24dp,
//            R.drawable.ic_3d_rotation_black_24dp,
//            R.drawable.ic_ac_unit_black_24dp,
//            R.drawable.ic_access_alarm_black_24dp,
//            R.drawable.ic_access_time_black_24dp};

    String[] imageArray = new String[30];

//    String[] imageArray  = new String[30];
//    String[] nameArray = new String[30];
    String[] dateArray = new String[30];

    ListView listView;

    public static final String TAG = "MyTag";

    DatabaseHelper mDatabaseHelper;

    int length_count = 0;
    int array_length = imageArray.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

//        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.example.displaynotificationandroid_FCM-MESSAGE"));

        setContentView(R.layout.activity_display_data);

//        Intent intent = getIntent();
//        Log.d(TAG, "onCreate: Home to Main Intent: " + intent);
//        String str = intent.getStringExtra("text");
//        String displayTitle = intent.getStringExtra("title");
//        String displayMessage = intent.getStringExtra("message");
//        String displayName = intent.getStringExtra("name");
//        String displayLoss = intent.getStringExtra("loss");
//        String displayDescription = intent.getStringExtra("description");
//        String displayImage = intent.getStringExtra("image");
////        Log.d(TAG, "onCreate: Main Intent: " + intent);
//        Log.d(TAG, "onCreate: Data str: " + str);
//        Log.d(TAG, "onCreate: Data displayTitle: " + displayTitle);
//        Log.d(TAG, "onCreate: Data displayMessage: " + displayMessage);
//        Log.d(TAG, "onCreate: Data displayName: " + displayName);
//        Log.d(TAG, "onCreate: Data displayLoss: " + displayLoss);
//        Log.d(TAG, "onCreate: Data displayDescription: " + displayDescription);
//        Log.d(TAG, "onCreate: DisplayDatActivity Data displayImage: " + displayImage);

//        imageArray[0] = displayImage;
//        imageArray[1] = displayImage;
//        imageArray[2] = displayImage;
//        imageArray[3] = displayImage;
//        imageArray[4] = displayImage;
//        imageArray[5] = displayImage;
//        nameArray[0] = displayName;
//        dateArray[0] = displayLoss;

//        imageArray[length_count] = displayImage;
//        nameArray[length_count] = displayName;
//        dateArray[length_count] = displayLoss;

//        array_length = array_length - 1;
//        length_count = length_count + 1;

//        CustomListAdapter whatever = new CustomListAdapter(this, nameArray, infoArray, imageArray);

//        CustomListAdapter whatever = new CustomListAdapter(this, nameArray, imageArray, dateArray);

//        CustomListAdapter whatever = new CustomListAdapter(this, nameArray, infoArray, imageArray);

        listView = findViewById(R.id.listviewID);
//        listView.setAdapter(whatever);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if(position == 0) {
                    //code specific to first list item
                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 1) {
                    //code specific to 2nd list item
                    Toast.makeText(getApplicationContext(),"Place Your Second Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 2) {

                    Toast.makeText(getApplicationContext(),"Place Your Third Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 3) {

                    Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 4) {

                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
        storeNotificationData = new ArrayList<>();

        notifyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storeNotificationData.clear();

                for (DataSnapshot displayShapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Shapshot: " + displayShapshot);
                    Log.d(TAG, "onDataChange: Shapshot: " + displayShapshot.getValue());
                    StoreNotificationData storeData = displayShapshot.getValue(StoreNotificationData.class);
                    Log.d(TAG, "onDataChange: Store Data: " + storeData);
                    storeNotificationData.add(storeData);

//                    RetrieveNotificationData retriveData = new RetrieveNotificationData(DisplayDataActivity.this, storeNotificationData);
//                    listView.setAdapter(retriveData);

                }

                RetrieveNotificationData retrieveNotificationData = new RetrieveNotificationData(DisplayDataActivity.this, storeNotificationData);
                listView.setAdapter(retrieveNotificationData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AddData (String name, String loss, String description, String image, String date, String time) {
        boolean insertingData = mDatabaseHelper.addData(name, loss, description, image, date, time);

        if (insertingData) {
            toastMessage("Data Inserted Successfully!");
        }
        else {
            toastMessage("Something Went Wrong!");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

//    private BroadcastReceiver mHandler = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            displayPersonName.setText("");
//            displayPersonLoss.setText("");
//            displayPersonDescription.setText("");
//
//            Log.i(TAG, "onReceive: Main Content: " + context);
//            Log.i(TAG, "onReceive: Main Intent: " + intent);
//
//            RemoteMessage remoteMessage = null;
//            String title = intent.getStringExtra("title");
//            String message = intent.getStringExtra("message");
//            String imageURI = intent.getStringExtra("image");
//
//            Log.d(TAG, "onReceive: Main " + title);
//            Log.d(TAG, "onReceive: Main " + message);
//            Log.d(TAG, "onReceive: Main " + imageURI);
//
//            Picasso.with(context).load(imageURI).placeholder(R.drawable.ic_android_black_24dp)
//                    .error(R.drawable.ic_android_black_24dp)
//                    .into(userImage, new com.squareup.picasso.Callback() {
//
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError() {
//
//                        }
//                    });
//
//            displayPersonName.setText(title);
//            displayPersonDescription.setText(message);
//
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
//        }
//    };
}
