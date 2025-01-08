package com.example.displaynotificationandroid;

import android.util.Log;

public class StoreNotificationData {

    private static final String TAG = "MyTag";
    String notifyTitle;
    String notifyMessage;
    String notifyPersonName;
    String notifyLoss;
    String notifyDescription;
    String notifyImage;
    String notifyDateTime;
    String notifyFirstTime;
    String notifyLastTime;
    String notifyPersonStatus;

    public StoreNotificationData(){

    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    public String getNotifyMessage() {
        return notifyMessage;
    }

    public void setNotifyMessage(String notifyMessage) {
        this.notifyMessage = notifyMessage;
    }

    public String getNotifyPersonName() {
        Log.d(TAG, "getNotifyPersonName: Getting Name: " + notifyPersonName);
        return notifyPersonName;
    }

    public void setNotifyPersonName(String notifyPersonName) {
        this.notifyPersonName = notifyPersonName;
    }

    public String getNotifyLoss() {
        Log.d(TAG, "getNotifyPersonName: Getting Name: " + notifyLoss);
        return notifyLoss;
    }

    public void setNotifyLoss(String notifyLoss) {
        this.notifyLoss = notifyLoss;
    }

    public String getNotifyDescription() {
        return notifyDescription;
    }

    public void setNotifyDescription(String notifyDescription) {
        this.notifyDescription = notifyDescription;
    }

    public String getNotifyDateTime() {
        return notifyDateTime;
    }

    public void setNotifyDateTime(String notifyDateTime) {
        this.notifyDateTime = notifyDateTime;
    }

    public String getNotifyFirstTime() {
        return notifyFirstTime;
    }

    public void setNotifyFirstTime(String notifyFirstTime) {
        this.notifyFirstTime = notifyFirstTime;
    }

    public String getNotifyLastTime() {
        return notifyLastTime;
    }

    public void setNotifyLastTime(String notifyLastTime) {
        this.notifyLastTime = notifyLastTime;
    }

    public String getNotifyImage() {
//        Log.d(TAG, "getNotifyPersonName: Getting Name: " + notifyImage);
        return notifyImage;
    }

    public String getNotifyPersonStatus() {
        return notifyPersonStatus;
    }

    public void setNotifyPersonStatus(String notifyPersonStatus) {
        this.notifyPersonStatus = notifyPersonStatus;
    }

    public void setNotifyImage(String notifyImage) {
        this.notifyImage = notifyImage;
    }

//    public StoreNotificationData(String notifyTitle, String notifyMessage, String notifyPersonName, String notifyLoss, String notifyDescription, String notifyImage) {
//        this.notifyTitle = notifyTitle;
//        this.notifyMessage = notifyMessage;
//        this.notifyPersonName = notifyPersonName;
//        this.notifyLoss = notifyLoss;
//        this.notifyDescription = notifyDescription;
//        this.notifyImage = notifyImage;
//    }

//    public String getNotifyTitle() {
//        return notifyTitle;
//    }
//
//    public String getNotifyMessage() {
//        return notifyMessage;
//    }
//
//    public String getNotifyPersonName() {
//        return notifyPersonName;
//    }
//
//    public String getNotifyLoss() {
//        return notifyLoss;
//    }
//
//    public String getNotifyDescription() {
//        return notifyDescription;
//    }
//
//    public String getNotifyImage() {
//        return notifyImage;
//    }

}
