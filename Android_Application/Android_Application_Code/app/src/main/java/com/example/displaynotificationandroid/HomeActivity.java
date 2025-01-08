package com.example.displaynotificationandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    private static int SPLASH_TIME_OUT = 5000;

    TextView welcomeText;
    Animation topAnim, bottomAnim;
    ImageView imageView;

    SharedPreferences onBoardingScreen;

    DatabaseReference notifyDatabase;
    StoreNotificationData store;

    String data = "";
    String displayTitle = "";
    String displayMessage = "";
    String displayName = "";
    String displayLoss = "";
    String displayDescription = "";
    String displayImage = "";
    String displayDateTime = "";
    String displayFirstTime = "";
    String displayLastTime = "";
    String showDate = "";
    String displayPersonStatus = "";

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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        welcomeText = findViewById(R.id.welcomeText);
        imageView = findViewById(R.id.welcomeImage);

        Log.d(TAG, "Home Activity");

        Log.d(TAG, "onCreate: Home Instance Data: " + (getIntent() != null));
        Log.d(TAG, "onCreate: Home Instance Data: " + (getIntent().hasExtra("key1")));

//         && getIntent().hasExtra("key1")

        String[] data1 = new String[10];
        String[] data2 = new String[10];
        int count = 0;

        if (getIntent() != null && getIntent().hasExtra("title")) {
            Log.d(TAG, "Home Intent Value: " + getIntent().toString());
            for (String key : getIntent().getExtras().keySet()) {
                Log.d(TAG, "onCreate: Home: key: " + key + " Data: " + getIntent().getExtras().getString(key));
                data = data + getIntent().getExtras().getString(key) + " ";
                if (key.equals("title")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayTitle = displayTitle + getIntent().getExtras().getString(key);
                }
                else if(key.equals("message")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayMessage = displayMessage + getIntent().getExtras().getString(key);
                }
                else if(key.equals("name")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayName = displayName + getIntent().getExtras().getString(key);
                }
                else if(key.equals("loss")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayLoss = displayLoss + getIntent().getExtras().getString(key);
                }
                else if(key.equals("description")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayDescription = displayDescription + getIntent().getExtras().getString(key);
                }
                else if(key.equals("image")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayImage = displayImage + getIntent().getExtras().getString(key);
                }
                else if(key.equals("dateTime")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayDateTime = displayDateTime + getIntent().getExtras().getString(key);
                }
                else if(key.equals("firstTime")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayFirstTime = displayFirstTime + getIntent().getExtras().getString(key);
                }
                else if(key.equals("lastTime")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayLastTime = displayLastTime + getIntent().getExtras().getString(key);
                }
                else if(key.equals("personStatus")) {
                    Log.d(TAG, "onCreate: Key: " + key);
                    displayPersonStatus = displayPersonStatus + getIntent().getExtras().getString(key);
                }
            }

            Log.d(TAG, "onCreate: Home: Data: " + data);

            Intent homeIntent = new Intent(HomeActivity.this, LoginActivity.class);
//            Intent homeIntent = new Intent(HomeActivity.this, DisplayDataActivity.class);

            if (displayTitle != null || !displayTitle.equals("")) {
                notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
                addNotificationData();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                Date date = new Date();
                showDate = formatter.format(date);
            }

            homeIntent.putExtra("text", data);
            homeIntent.putExtra("title", displayTitle);
            homeIntent.putExtra("message", displayMessage);
            homeIntent.putExtra("name", displayName);
            homeIntent.putExtra("loss", displayLoss);
            homeIntent.putExtra("description", displayDescription);
            homeIntent.putExtra("image", displayImage);
            homeIntent.putExtra("dateTime", showDate);
            homeIntent.putExtra("firstTime", displayFirstTime);
            homeIntent.putExtra("lastTime", displayLastTime);
            homeIntent.putExtra("personStatus", displayPersonStatus);

            Log.d(TAG, "onCreate: Home Data: " + homeIntent.putExtra("text", data));
            Log.d(TAG, "onCreate: Home Data: " + homeIntent.putExtra("text", displayTitle));
            Log.d(TAG, "onCreate: Home Data: " + homeIntent.putExtra("text", displayMessage));
            Log.d(TAG, "onCreate: Home Data: " + homeIntent.putExtra("text", displayName));
            Log.d(TAG, "onCreate: Home Data: " + homeIntent.putExtra("text", displayLoss));
            Log.d(TAG, "onCreate: Home Data: " + homeIntent.putExtra("text", displayDescription));
            Log.d(TAG, "onCreate: Home Data: " + homeIntent.putExtra("text", displayImage));
            startActivity(homeIntent);
        }
        else {
            imageView.setAnimation(topAnim);
            welcomeText.setAnimation(bottomAnim);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    Intent homeIntent = new Intent(HomeActivity.this, LoginActivity.class);

                    onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                    boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);

                    if (isFirstTime) {
                        SharedPreferences.Editor editor = onBoardingScreen.edit();
                        editor.putBoolean("firstTime", false);
                        editor.commit();

                        Intent homeIntent = new Intent(HomeActivity.this, OnBoardingActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(welcomeText, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, pairs);
                        startActivity(homeIntent, options.toBundle());

                        finish();
                    }
                    else{
                        Intent homeIntent = new Intent(HomeActivity.this, LoginActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(welcomeText, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, pairs);
                        startActivity(homeIntent, options.toBundle());

                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void addNotificationData() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        Date date = new Date();
        String showDate = formatter.format(date);

        store = new StoreNotificationData();

        store.setNotifyTitle(displayTitle);
        store.setNotifyMessage(displayMessage);
        store.setNotifyPersonName(displayName);
        store.setNotifyLoss(displayLoss);
        store.setNotifyDescription(displayDescription);
        store.setNotifyImage(displayImage);
        store.setNotifyDateTime(showDate);
        store.setNotifyFirstTime(displayFirstTime);
        store.setNotifyLastTime(displayLastTime);
        store.setNotifyPersonStatus(displayPersonStatus);

        notifyDatabase.push().setValue(store);

        Log.d(TAG, "addNotificationData: Notification Data Inserted!");
    }
}
