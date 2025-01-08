package com.example.displaynotificationandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.displaynotificationandroid.App.FCM_CHANNEL_ID;

public class FCMMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MyTag";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived: Message Received From " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String imageUri = remoteMessage.getNotification().getImageUrl().toString();
            Log.d(TAG, "Image Url: " + imageUri);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

            Notification notification = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_android_black_24dp)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColor(Color.BLUE)
                    .setTimeoutAfter(30)
                    .build();

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1002, notification);

            String demo_title = remoteMessage.getNotification().getTitle();
            String demo_message = remoteMessage.getNotification().getBody();
            String demo_imageUri = remoteMessage.getNotification().getImageUrl().toString();

            Log.d(TAG, "onReceive: Main Notification" + demo_title);
            Log.d(TAG, "onReceive: Main Notification" + demo_message);
            Log.d(TAG, "onMessageReceived: Main Notification" + demo_imageUri);

//            "com.example.displaynotificationandroid_FCM-MESSAGE"

            Intent intent = new Intent("com.example.displaynotificationandroid_FCM-MESSAGE");
            intent.putExtra("title", demo_title);
            intent.putExtra("message", demo_message);
            intent.putExtra("image", demo_imageUri);

            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);
        }

        if (remoteMessage.getData().size() > 0){

            Log.d(TAG, "onMessageReceived: Data Size: " + remoteMessage.getData().size());

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            String demo_title = remoteMessage.getNotification().getTitle();
            String demo_message = remoteMessage.getNotification().getBody();
            String demo_imageUri = remoteMessage.getNotification().getImageUrl().toString();

            Log.d(TAG, "onReceive: Main FCM" + title);
            Log.d(TAG, "onReceive: Main FCM" + message);

            Log.d(TAG, "onReceive: Main FCM" + demo_title);
            Log.d(TAG, "onReceive: Main FCM" + demo_message);
            Log.d(TAG, "onReceive: Main FCM" + demo_imageUri);

//            "com.example.displaynotificationandroid_FCM-MESSAGE"

//            if (getIntent() != null && getIntent().hasExtra("title")) {
//                Log.d(TAG, "Home Intent Value: " + getIntent().toString());
//                for (String key : getIntent().getExtras().keySet()) {
//                    Log.d(TAG, "onCreate: Home: key: " + key + " Data: " + getIntent().getExtras().getString(key));
//                    data = data + getIntent().getExtras().getString(key) + " ";
//                    if (key.equals("title")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayTitle = displayTitle + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("message")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayMessage = displayMessage + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("name")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayName = displayName + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("loss")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayLoss = displayLoss + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("description")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayDescription = displayDescription + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("image")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayImage = displayImage + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("dateTime")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayDateTime = displayDateTime + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("firstTime")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayFirstTime = displayFirstTime + getIntent().getExtras().getString(key);
//                    }
//                    else if(key.equals("lastTime")) {
//                        Log.d(TAG, "onCreate: Key: " + key);
//                        displayLastTime = displayLastTime + getIntent().getExtras().getString(key);
//                    }
//                }
//
//            if (displayTitle != null || !displayTitle.equals("")) {
//                notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
//                addNotificationData();
//
//                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
//                Date date = new Date();
//                showDate = formatter.format(date);
//            }

            Intent intent = new Intent("com.example.displaynotificationandroid_FCM-MESSAGE");
            intent.putExtra("title", demo_title);
            intent.putExtra("message", demo_message);
            intent.putExtra("image", demo_imageUri);

            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);

            Log.d(TAG, "onMessageReceived: Bread Casted ");

//            for (String key: remoteMessage.getData().keySet()){
//                Log.d(TAG, "onMessageReceived: key: " + key + " Data : " + remoteMessage.getData().get(key));
//            }

            Log.d(TAG, "onMessageReceived: Data: " + remoteMessage.getData().toString());
//            remoteMessage.getData().

        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages: called");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: called");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */

//    private void sendNotification(String messageBody, Bitmap image, String TrueOrFalse) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("AnotherActivity", TrueOrFalse);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setLargeIcon(image)/*Notification icon image*/
//                .setSmallIcon(R.drawable.ic_android_black_24dp)
//                .setContentTitle(messageBody)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(image))/*Notification with Image*/
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
//
//    /*
//     *To get a Bitmap image from the URL received
//     * */
//    public Bitmap getBitmapfromUrl(String imageUrl) {
//        try {
//            Log.i("Info", "Inside Image Function Try");
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(input);
//            return bitmap;
//
//        } catch (Exception e) {
//            Log.i("Info", "Inside Image Function catch");
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//
//        }
//    }
//
//    public Bitmap getBitmapFromURL(String src) {
//        try {
//            java.net.URL url = new java.net.URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            return myBitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}

//    private void addNotificationData() {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
//        Date date = new Date();
//        String showDate = formatter.format(date);
//
//        store = new StoreNotificationData();
//
//        store.setNotifyTitle(displayTitle);
//        store.setNotifyMessage(displayMessage);
//        store.setNotifyPersonName(displayName);
//        store.setNotifyLoss(displayLoss);
//        store.setNotifyDescription(displayDescription);
//        store.setNotifyImage(displayImage);
//        store.setNotifyDateTime(showDate);
//        store.setNotifyFirstTime(displayFirstTime);
//        store.setNotifyLastTime(displayLastTime);
//
//        notifyDatabase.push().setValue(store);
//
//        Log.d(TAG, "addNotificationData: Notification Data Inserted!");
//    }
