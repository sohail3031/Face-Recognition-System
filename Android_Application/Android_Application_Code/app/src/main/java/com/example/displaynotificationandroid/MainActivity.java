package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    Button logoutButton;
    TextView text, displayPersonName, displayPersonLoss, displayPersonDescription;
    FirebaseAuth mFirebaseAuth;
    ImageView userImage;

    public static final String TAG = "MyTag";

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

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.example.displaynotificationandroid_FCM-MESSAGE"));

        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isWifiConn = false;
        boolean isMobileConn = false;

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: wifi");
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: data");
            }
        }

        if (activeNetworkInfo == null){
//            Intent checkActivity = new Intent(MainActivity.this, DisplayDataActivity.class);
//            startActivity(checkActivity);
        }
        else{
            logoutButton = findViewById(R.id.logout);
            text = findViewById(R.id.textView);
            userImage = findViewById(R.id.userImage);
            displayPersonName = findViewById(R.id.displayPersonName);
            displayPersonLoss = findViewById(R.id.displayPersonLoss);
            displayPersonDescription = findViewById(R.id.displayPersonDescription);

            Log.d(TAG, "onCreate: Main Instance Data: " + (getIntent() != null));
            Log.d(TAG, "onCreate: Main Instance Data: " + (getIntent().hasExtra("key1")));

            Intent intent = getIntent();
            Log.d(TAG, "onCreate: Home to Main Intent: " + intent);
            String str = intent.getStringExtra("text");
            String displayTitle = intent.getStringExtra("title");
            String displayMessage = intent.getStringExtra("message");
            String displayName = intent.getStringExtra("name");
            String displayLoss = intent.getStringExtra("loss");
            String displayDescription = intent.getStringExtra("description");
            String displayImage = intent.getStringExtra("image");
//        Log.d(TAG, "onCreate: Main Intent: " + intent);
            Log.d(TAG, "onCreate: Main str: " + str);
            Log.d(TAG, "onCreate: Main displayTitle: " + displayTitle);
            Log.d(TAG, "onCreate: Main displayMessage: " + displayMessage);
            Log.d(TAG, "onCreate: Main displayName: " + displayName);
            Log.d(TAG, "onCreate: Main displayLoss: " + displayLoss);
            Log.d(TAG, "onCreate: Main displayDescription: " + displayDescription);
            Log.d(TAG, "onCreate: Main displayImage: " + displayImage);

//        Log.d(TAG, "onCreate: Image Type: " + displayImage.getClass().getName().toString());

            displayPersonName.setText(displayName);
            displayPersonLoss.setText(displayLoss);
            displayPersonDescription.setText(displayDescription);
//        Picasso.get().load(displayImage).into(userImage);
            Picasso.with(this).load(displayImage).placeholder(R.drawable.ic_android_black_24dp).into(userImage);

            // To get fcm token
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isSuccessful()){
                        String token = task.getResult().getToken();
                        Log.d(TAG, "onComplete: Token: " + token);
                    }
                    else {
                        Log.d(TAG, "onComplete: Token Generation Failed!");
                    }
                }
            });

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();

                    Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(toLogin);
                }
            });
        }
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

            Log.d(TAG, "onReceive: getIntent != null: " + (getIntent() != null));
            Log.d(TAG, "onReceive: getIntent().hasExtra('key1'): " + getIntent().hasExtra("key1"));

            if (getIntent() != null && getIntent().hasExtra("key1")){
                Log.d(TAG, "onCreate: From Main Activity");
                for (String key: getIntent().getExtras().keySet()){
                    Log.d(TAG, "onCreate: key: " + key + " Data : " + getIntent().getExtras().getString(key));
                    displayPersonLoss.append(getIntent().getExtras().getString(key) + "\n");
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    @Override
    public void onBackPressed() {

    }
}
