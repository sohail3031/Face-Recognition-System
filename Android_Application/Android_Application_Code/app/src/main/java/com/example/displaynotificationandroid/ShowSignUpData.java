package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Method;
import java.security.Provider;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowSignUpData extends AppCompatActivity {

    private static final String TAG = "BTag";

    ImageView backArrow;
    Button createAccount, editData;
    TextView sigh;
    EditText firstNameShow, lastNameShow, emailShow, passwordShow, genderShow, dateShow, phoneNumberThreeShow;
    Switch switch1, switch2;
    LinearLayout linearLayout2Show;
    ProgressBar showDataProgress;

    String fName, lName, userEmail, userPassword, userGender, dateDOBDay, dateDOBMonth, dateDOBYear, phoneNumber, finalDate, userType;

    final Calendar myCalendar = Calendar.getInstance();

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    boolean checkLogin = false;
    boolean dateEditable = false;
    boolean isDataEdited = false;
    boolean validateFN = false;
    boolean validateLN = false;
    boolean validateBN = false;
    boolean validateUE = false;
    boolean validateDBO = false;
    boolean validatePN = false;
    boolean isWifiConn = false;
    boolean isMobileConn = false;
    boolean verifyEmailExist = false;

    String editedFName, editedLName, editedUserEmail, editedPassword, editedGender, editedDOB, editedPhone, editUserType;

    FirebaseAuth mFirebaseAuth;
    DatabaseReference refering;
    InsertingData insert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sign_up_data);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: wifi");
                checkLogin = true;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: data");
                checkLogin = true;
            }
        }

        backArrow = findViewById(R.id.signupBackButtonShow);
        createAccount = findViewById(R.id.SignUpShow);
        editData = findViewById(R.id.SignUpModifyShow);
        sigh = findViewById(R.id.finalSignUpText);
        firstNameShow=  findViewById(R.id.firstNameShow);
        lastNameShow=  findViewById(R.id.lastNameShow);
        emailShow=  findViewById(R.id.emailShow);
        passwordShow=  findViewById(R.id.passwordShow);
        genderShow=  findViewById(R.id.genderShow);
        dateShow=  findViewById(R.id.dateShow);
        phoneNumberThreeShow=  findViewById(R.id.phoneNumberThreeShow);
        switch1 = findViewById(R.id.openWifiButtonShow);
        switch2 = findViewById(R.id.openMobileDataShow);
        linearLayout2Show = findViewById(R.id.linearLayout2Show);
        showDataProgress = findViewById(R.id.showDataProgress);
        mFirebaseAuth = FirebaseAuth.getInstance();

        refering = FirebaseDatabase.getInstance().getReference().child("USERS");
        insert = new InsertingData();

        Bundle extras = getIntent().getExtras();
        fName = extras.getString("firstName");
        lName = extras.getString("lastName");
        userEmail = extras.getString("userEmail");
        userPassword = extras.getString("userPassword");
        userGender = extras.getString("userGender");
        dateDOBDay = extras.getString("userDOBDay");
        dateDOBMonth = extras.getString("userDOBMonth");
        dateDOBYear = extras.getString("userDOBYear");
        phoneNumber = extras.getString("phone");
        userType = extras.getString("userType");

//        Toast.makeText(this, "user type: " + userType, Toast.LENGTH_SHORT).show();
        editUserType = userType;
//        Toast.makeText(this, "user type 1: " + editUserType, Toast.LENGTH_SHORT).show();

        finalDate = dateDOBDay + "/" + dateDOBMonth + "/" + dateDOBYear;

        firstNameShow.setText(fName);
        lastNameShow.setText(lName);
        emailShow.setText(userEmail);
        passwordShow.setText(userPassword);
        genderShow.setText(userGender);
        dateShow.setText(finalDate);
        phoneNumberThreeShow.setText(phoneNumber);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowSignUpData.this);
                alertDialog.setTitle("Are You Sure?");
                alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ShowSignUpData.this, LoginActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(sigh, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ShowSignUpData.this, pairs);
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

        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDataEdited = true;

                firstNameShow.setClickable(true);
                firstNameShow.setCursorVisible(true);
                firstNameShow.setFocusable(true);
                firstNameShow.setFocusableInTouchMode(true);

                lastNameShow.setClickable(true);
                lastNameShow.setCursorVisible(true);
                lastNameShow.setFocusable(true);
                lastNameShow.setFocusableInTouchMode(true);

//                emailShow.setClickable(true);
//                emailShow.setCursorVisible(true);
//                emailShow.setFocusable(true);
//                emailShow.setFocusableInTouchMode(true);

                dateShow.setClickable(true);
                dateShow.setCursorVisible(true);
                dateShow.setFocusable(true);
                dateShow.setFocusableInTouchMode(true);

                dateEditable = true;

                phoneNumberThreeShow.setClickable(true);
                phoneNumberThreeShow.setCursorVisible(true);
                phoneNumberThreeShow.setFocusable(true);
                phoneNumberThreeShow.setFocusableInTouchMode(true);

                Toast.makeText(ShowSignUpData.this, "You can Modify Data Now", Toast.LENGTH_LONG).show();
            }
        });

        dateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (dateEditable) {
                    new DatePickerDialog(ShowSignUpData.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        dateShow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dateEditable) {
                    new DatePickerDialog(ShowSignUpData.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataEdited) {
                    Log.d("MyTag", "onClick: Inside Last If");
                    editedFName = firstNameShow.getText().toString().trim();
                    editedLName = lastNameShow.getText().toString().trim();
                    editedUserEmail = emailShow.getText().toString().trim();
                    editedPassword = userPassword;
                    editedGender = userGender;
                    editedDOB = dateShow.getText().toString().trim();
                    editedPhone = phoneNumberThreeShow.getText().toString().trim();
                    editUserType = userType;

                    String[] dobArray = editedDOB.split("/");

                    validateFN = validateFName(editedFName);
                    validateLN = validateLName(editedLName);
                    validateBN = validateBName(editedFName, editedLName);
                    validateUE = validateEmail(editedUserEmail);
                    validateDBO = validateDateYear(Integer.parseInt(dobArray[2]));
                    validatePN = validatePhone(editedPhone);

                    if (!(checkLogin)) {
                        AlertDialog alertDialog = new AlertDialog.Builder(ShowSignUpData.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        alertDialog.setMessage("It seams you are offline but you need internet connection to create your account");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        switch1.setVisibility(View.VISIBLE);
                        switch2.setVisibility(View.VISIBLE);
                    }
                    else if (validateFN && validateLN && validateBN && validateUE && validateDBO && validatePN){
                            mFirebaseAuth.createUserWithEmailAndPassword(editedUserEmail, editedPassword).addOnCompleteListener(ShowSignUpData.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                        Toast.makeText(ShowSignUpData.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "onComplete: " + "Failed Registration: "+e.getMessage());
                                    }
                                    else {
                                        //                        if (verifyEmailExist){
                                        insert.setUserFirstName(editedFName);
                                        insert.setUserLastName(editedLName);
                                        insert.setUserDateOfBirth(editedDOB);
                                        insert.setUserGender(editedGender);
                                        insert.setUserPhoneNumber(editedPhone);
                                        insert.setUserEmailId(editedUserEmail);
                                        insert.setUserPassword(editedPassword);
                                        insert.setUserTypeAccount(editUserType);
                                        refering.push().setValue(insert);



//                                        Log.i("Info", "Inside main");
//                                        Toast.makeText(ShowSignUpData.this, "Account Created", Toast.LENGTH_LONG).show();
//                                        Intent i = new Intent(ShowSignUpData.this, LoginActivity.class);
//                                        startActivity(i);
                                        Toast.makeText(ShowSignUpData.this, "Account Created Please Verify Your Email To Login", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(ShowSignUpData.this, LoginActivity.class);

                                        intent.putExtra("createdEmail", editedUserEmail);

                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<View, String>(sigh, "logo_text");

                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ShowSignUpData.this, pairs);
                                        startActivity(intent, options.toBundle());
                                    }
                                }
                            });
//                        }
//                        else {
//                            Toast.makeText(ShowSignUpData.this, "User withthis email already exist", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
                else {
                    Log.d("MyTag", "onClick: Inside Last Else");
                    if (!(checkLogin)) {
                        Log.d("MyTag", "onClick: Inside Last Else-If");
                        AlertDialog alertDialog = new AlertDialog.Builder(ShowSignUpData.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        alertDialog.setMessage("It seams you are offline but you need internet connection to create your account");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        switch1.setVisibility(View.VISIBLE);
                        switch2.setVisibility(View.VISIBLE);
                    }
                    else {
                            mFirebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(ShowSignUpData.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    showDataProgress.setVisibility(View.VISIBLE);

                                    if (!task.isSuccessful()) {
                                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                        Toast.makeText(ShowSignUpData.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "onComplete: " + "Failed Registration: "+e.getMessage());
                                        showDataProgress.setVisibility(View.GONE);
                                    }
                                    else {
//                                     if (verifyEmailExist) {
                                        Log.d("MyTag", "onClick: Inside Last Else-Else");
                                        insert.setUserFirstName(fName);
                                        insert.setUserLastName(lName);
                                        insert.setUserDateOfBirth(finalDate);
                                        insert.setUserGender(userGender);
                                        insert.setUserPhoneNumber(phoneNumber);
                                        insert.setUserEmailId(userEmail);
                                        insert.setUserPassword(userPassword);
                                        insert.setUserTypeAccount(userType);
                                        refering.push().setValue(insert);
                                        Log.i("Info", "Inside main");
                                        mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    showDataProgress.setVisibility(View.GONE);
                                                    Toast.makeText(ShowSignUpData.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    showDataProgress.setVisibility(View.GONE);
                                                    Toast.makeText(ShowSignUpData.this, "Account Created Please Verify Your Email To Login", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(ShowSignUpData.this, LoginActivity.class);

                                                    intent.putExtra("createdEmail", userEmail);

                                                    Pair[] pairs = new Pair[1];
                                                    pairs[0] = new Pair<View, String>(sigh, "logo_text");

                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ShowSignUpData.this, pairs);
                                                    startActivity(intent, options.toBundle());
                                                }
                                            }
                                        });
                                    }
                                }
                            });
//                        }
//                        else{
//                            Toast.makeText(ShowSignUpData.this, "User withthis email already exist", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked)
                {
                    Log.d(TAG, "onCheckedChanged: if");
                    EnableWiFi() ;
                }
                else {
                    Log.d(TAG, "onCheckedChanged: else");
                    DisableWiFi();
                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getMobileDataState();
                setMobileDataState(isChecked);
            }
        });
    }

    public void checkEmailExist(String userEmail){
//        for (DataSnapshot ds: dataSnapshot.getChildren()){
//            CheckEmailAlreadyExists cs = ds.getValue(CheckEmailAlreadyExists.class);
//        }
        mFirebaseAuth.fetchSignInMethodsForEmail(emailShow.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                Log.d(TAG, "onComplete: " + task.getResult().getSignInMethods().size());
                if (task.getResult().getSignInMethods().size() == 0) {
                    verifyEmailExist = true;
                }
                else {
                    verifyEmailExist = false;
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateShow.setText(sdf.format(myCalendar.getTime()));
    }

    public void EnableWiFi(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(this, "Your now connected to internet", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = true;
    }

    public void DisableWiFi(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(this, ":-( connection lost", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = false;
    }

    private void setMobileDataState(boolean isChecked) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            Log.d(TAG, "setMobileDataState: mobile data set data method");
            setMobileDataEnabledMethod.invoke(telephonyService, isChecked);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error setting mobile data state", ex);
        }
    }

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("getDataEnabled");
            Log.d(TAG, "getMobileDataState: mobile data get data method ");
            return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error getting mobile data state", ex);
        }
        return false;
    }

    public boolean validateFName(String fname){
        if (fname.isEmpty()){
            firstNameShow.setError("First Name Should Not Be Empty");
            firstNameShow.requestFocus();
            return false;
        }
        else {
            if (fname.length() <= 20){
                return true;
            }
            else {
                firstNameShow.setError("First Name should not exceed 20 characters");
                firstNameShow.requestFocus();
                return false;
            }
        }
    }

    public boolean validateLName(String lName){
        if (lName.isEmpty()){
            lastNameShow.setError("Last Name Should Not Be Empty");
            lastNameShow.requestFocus();
            return false;
        }
        else {
            if (lName.length() <= 20){
                return true;
            }
            else {
                lastNameShow.setError("Last Name should not exceed 20 characters");
                lastNameShow.requestFocus();
                return false;
            }
        }
    }

    public boolean validateBName(String fName, String lName){
        if (fName.equals(lName)){
            lastNameShow.setError("First Name and Last Name should not be same");
            lastNameShow.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    public boolean validateEmail(String userEmail){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        if (userEmail.isEmpty()){
            emailShow.setError("Email Address cannot be empty");
            emailShow.requestFocus();
            return false;
        }
        else {
            if (pat.matcher(userEmail).matches()) {
                return true;
            }
            else{
                emailShow.setError("Email Address is not valid");
                emailShow.requestFocus();
                return false;
            }
        }
    }

    public boolean validateDateYear(int year){
        Date dateNow = new Date();
        int currentYear = dateNow.getYear() + 1900;
        int startYear = 1950;

        if ((year > startYear) && (year < (currentYear - 18))) {
            return true;
        }
        else{
            Toast.makeText(this, "The Date-Of-Birth is not valid", Toast.LENGTH_SHORT).show();
//            userDateOfBirth.requestFocus();
            return false;
        }
    }

    public boolean validateDate(String dob){
        Date dateNow = new Date();
        int currentYear = dateNow.getYear() + 1900;
        int startYear = 1950;

        Log.d(TAG, "validateDate: currentyear: " + currentYear);

        String regex = "([0-1][0-9]+[/]+[0-3][0-9]+[/]+[0-2][0-9][0-9][0-9])";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((CharSequence) dob);

        String[] year = dob.split("/");

        int checkYear = Integer.parseInt(year[2]);

        if (matcher.matches()){
            Log.d(TAG, "validateDate: checkyear: " + checkYear);
            Log.d(TAG, "validateDate: checkyear: " + (currentYear - 18));
            if ((checkYear > startYear) && (checkYear < (currentYear - 18))) {
                return true;
            }
            else{
                Toast.makeText(this, "The Date-Of-Birth is not valid", Toast.LENGTH_SHORT).show();
                dateShow.requestFocus();
                return false;
            }
        }
        else {
            dateShow.requestFocus();
            return false;
        }
    }

    public boolean validatePhone(String phoneNumber){
        if (phoneNumber.isEmpty()){
            phoneNumberThreeShow.setError("Phone Number should not be empty");
            phoneNumberThreeShow.requestFocus();
            return false;
        }
        else {
            if (phoneNumber.length() <13) {
                phoneNumberThreeShow.setError("Phone Number should be of 10 digits");
                phoneNumberThreeShow.requestFocus();
                return false;
            }
            else if (phoneNumber.length() > 13) {
                phoneNumberThreeShow.setError("Phone Number should be of 10 digits");
                phoneNumberThreeShow.requestFocus();
                return false;
            }
            else {
                return true;
            }
        }
    }
}
