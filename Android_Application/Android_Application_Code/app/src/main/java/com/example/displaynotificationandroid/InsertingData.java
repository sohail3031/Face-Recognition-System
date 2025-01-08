package com.example.displaynotificationandroid;

public class InsertingData {
    private String userFirstName;
    private String userLastName;
    private String userDateOfBirth;
    private String userGender;
    private String userPhoneNumber;
    private String userEmailId;
    private String userPassword;
    private String userTypeAccount;
    private String dummyData;

    public InsertingData(){

    }

    public String getUserFirstName(){
        return userFirstName;
    }

    public void setUserFirstName(String fName){
        userFirstName = fName;
    }

    public String getUserLastName(){
        return userLastName;
    }

    public void setUserLastName(String lName){
        userLastName = lName;
    }

    public String getUserDateOfBirth(){
        return userDateOfBirth;
    }

    public void setUserDateOfBirth(String dob){
        userDateOfBirth = dob;
    }

    public String getUserGender(){
        return userGender;
    }

    public void setUserGender(String gen){
        userGender = gen;
    }

    public String getUserPhoneNumber(){
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String pn){
        userPhoneNumber = pn;
    }

    public String getUserEmailId(){
        return userEmailId;
    }

    public void setUserEmailId(String eid){
        userEmailId = eid;
    }

    public String getUserPassword(){
        return userPassword;
    }

    public void setUserPassword(String psd){
        userPassword = psd;
    }

    public String getUserTypeAccount(){
        return userTypeAccount;
    }

    public void setUserTypeAccount(String uType){
        userTypeAccount = uType;
    }

    public String getDummyData() {
        return dummyData;
    }

    public void setDummyData(String dummyValue) {
        dummyData = dummyValue;
    }
}
