package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    TextView signUp, loginAgain, password_strength, login_instructions;
    EditText firstName, lastName, userDateOfBirth, phone, email, password1, password2;
    FirebaseAuth mFirebaseAuth;
    Button signUpButton;
    Spinner gender, phoneSpinner;
    DatabaseReference refering;
    InsertingData insert;
    boolean checkLogin = false;
    Switch switch1, switch2;
//    CheckBox showHide;
    ProgressBar progressBar;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    public static final String TAG = "MyTag";

    boolean validateFirstName = false;
    boolean validateLastName = false;
    boolean validateBothName = false;
    boolean validateDOB = false;
    boolean validateGender = false;
    boolean validatePhoneNumber = false;
    boolean validateEmailAddress = false;
    boolean validatePassword1 = false;
    boolean validatePassword2 = false;
    boolean validateBothPassword = false;

    char ch;
    boolean lower = false;
    boolean upper = false;
    boolean special = false;
    boolean number = false;
    boolean length = false;
    String trackedPassword = "";

    final Calendar myCalendar = Calendar.getInstance();

    String dateBirth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        userDateOfBirth = findViewById(R.id.dateOfBirth);
        gender = findViewById(R.id.gender);
        phone = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        password1 = findViewById(R.id.password);
        password2 = findViewById(R.id.confirmPassword);
        loginAgain = findViewById(R.id.loginAgain);
        signUpButton = findViewById(R.id.signUpButton);
        switch1 = findViewById(R.id.openWifiButton);
        switch2 = findViewById(R.id.openMobileData);
//        showHide = (CheckBox) findViewById(R.id.showPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        password_strength = (TextView) findViewById(R.id.password_strength);
        login_instructions = (TextView) findViewById(R.id.login_instructions);
        phoneSpinner = (Spinner) findViewById(R.id.phoneSpinner);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        refering = FirebaseDatabase.getInstance().getReference().child("USERS");
        insert = new InsertingData();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isWifiConn = false;
        boolean isMobileConn = false;

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

        Log.d(TAG, "onCreate: Forgot Password Internet: " + activeNetworkInfo);

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

        String[] items = new String[]{"Gender", "Male", "Female", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gender.setAdapter(adapter);

        String[] countries = new String[] {"IND +91", "ARE +971", "GBR +44", "USA +1", "AUS +61", "BGD +880", "BRA +55", "CAN +1", "CHN +86"};
        ArrayAdapter<String> countryList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        phoneSpinner.setAdapter(countryList);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info", "Inside Function!");
                String fName = firstName.getText().toString();
                String lName = lastName.getText().toString();
                String dateBirth = userDateOfBirth.getText().toString();
                String userGender = gender.toString();
                String phoneNumber = phone.getText().toString();
                final String userEmail = email.getText().toString();
                String userPassword1 = password1.getText().toString();
                String userPassword2 = password2.getText().toString();
                String genderSelected = String.valueOf(gender.getSelectedItem());
                String countrySelected = String.valueOf(phoneSpinner.getSelectedItem());

                String storingNumber = "";

                Log.i("Info", "Gender is : " + String.valueOf(gender.getSelectedItem()));

                validateFirstName = validateFName(fName);
                validateLastName = validateLName(fName);
                validateBothName = validateBName(fName, lName);
                validateDOB = validateDate(dateBirth);
                validateGender = validateG(genderSelected);
                validatePhoneNumber = validatePhone(phoneNumber);
                validateEmailAddress = validateEmail(userEmail);
//                validatePassword1 = validatePass1(userPassword1);
//                validatePassword2 = validatePass2(userPassword2);
                validateBothPassword = validateBothPass(userPassword1, userPassword2);

                if (validatePhoneNumber) {
                    storingNumber = countrySelected + " - " + phoneNumber;
                }

                if(!(checkLogin)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
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
                else if (validateFirstName && validateLastName && validateBothName && validateDOB && validateGender && validatePhoneNumber && validatePhoneNumber && validateEmailAddress && validateBothPassword) {
//                    String usersEmail = insert.getUserEmailId();
//                    Log.d(TAG, "onClick: usersEmail: " + usersEmail);
                    insert.setUserFirstName(fName);
                    insert.setUserLastName(lName);
                    insert.setUserDateOfBirth(dateBirth);
                    insert.setUserGender(genderSelected);
                    insert.setUserPhoneNumber(storingNumber);
                    insert.setUserEmailId(userEmail);
                    insert.setUserPassword(userPassword1);
                    refering.push().setValue(insert);
                    Log.i("Info", "Inside main");
//                    Toast.makeText(SignUpActivity.this, "Values inserted!", Toast.LENGTH_LONG).show();
                    mFirebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword1).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
//                                Log.i("Info", "Account Created");
                                Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_LONG).show();
                                Intent start = new Intent(SignUpActivity.this, LoginActivity.class);
                                Log.d(TAG, "onComplete: Sending Email: " + userEmail);
                                start.putExtra("email", userEmail);
                                startActivity(start);
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
//                                Toast.makeText(SignUpActivity.this, "User with this email is already present", Toast.LENGTH_LONG).show();
                            } else {
//                                Log.i("Info", "Cannot Create Account");
                                Log.i(TAG, "onComplete: Cannot Create Account");
//                                Toast.makeText(SignUpActivity.this, "Account Cannot Be Created!", Toast.LENGTH_LONG).show();
                                Toast.makeText(SignUpActivity.this, "Account Created! :-)", Toast.LENGTH_LONG).show();
//                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                Intent start = new Intent(SignUpActivity.this, LoginActivity.class);
                                Log.d(TAG, "onComplete: Sending Email: " + userEmail);
                                start.putExtra("email", userEmail);
                                startActivity(start);
                            }
                        }
                    });
                }
                Log.i("Info", "Process Completed");
            }
        });

        loginAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
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

        switch2.setChecked(getMobileDataState());
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMobileDataState(isChecked);
            }
        });

//        showHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked){
//                    Log.d(TAG, "onCheckedChanged: hidding password");
//                    password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                }
//                else{
//                    Log.d(TAG, "onCheckedChanged: showing password");
//                    password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                }
//            }
//        });

        password1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                password_strength.setVisibility(View.VISIBLE);
                login_instructions.setVisibility(View.VISIBLE);
            }
        });

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                password_strength.setVisibility(View.VISIBLE);
                login_instructions.setVisibility(View.VISIBLE);
                calculatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                password_strength.setVisibility(View.VISIBLE);
                login_instructions.setVisibility(View.VISIBLE);
                calculatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userDateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignUpActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
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
            Log.d(TAG, "setMobileDataState: mobile data setdata method");
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
            Log.d(TAG, "getMobileDataState: mobile data getdata method ");
            return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error getting mobile data state", ex);
        }
        return false;
    }

    public boolean validateFName(String fname){
        if (fname.isEmpty()){
            firstName.setError("First Name Should Not Be Empty");
            firstName.requestFocus();
            return false;
        }
        else {
            if (fname.length() <= 20){
                return true;
            }
            else {
                firstName.setError("First Name should not exceed 20 characters");
                firstName.requestFocus();
                return false;
            }
        }
    }

    public boolean validateLName(String lName){
        if (lName.isEmpty()){
            lastName.setError("Last Name Should Not Be Empty");
            lastName.requestFocus();
            return false;
        }
        else {
            if (lName.length() <= 20){
                return true;
            }
            else {
                lastName.setError("Last Name should not exceed 20 characters");
                lastName.requestFocus();
                return false;
            }
        }
    }

    public boolean validateBName(String fName, String lName){
        if (fName.equals(lName)){
            lastName.setError("First Name and Last Name should not be same");
            lastName.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    public boolean validateDate(String dob){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNow = new Date();
        int currentYear = dateNow.getYear() + 1900;
        int startYear = 1950;

        Log.d(TAG, "validateDate: currentyear: " + currentYear);

        String regex = "([0-3][0-9]+[/]+[0-1][0-9]+[/]+[0-2][0-9][0-9][0-9])";

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
                userDateOfBirth.setError("The Date-Of-Birth is not valid");
                userDateOfBirth.requestFocus();
                return false;
            }
        }
        else {
            userDateOfBirth.setError("The Date-Of-Birth should be in proper format like: " + formatter.format(dateNow));
            userDateOfBirth.requestFocus();
            return false;
        }
    }

    public boolean validateG(String gender){
        if (gender.equals("gender") || gender.equals("Gender") || gender.equals("GENDER")){
            Toast.makeText(this, "Please select a valid gender", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            return true;
        }
    }

    public boolean validatePhone(String phoneNumber){
        if (phoneNumber.isEmpty()){
            phone.setError("Phone Number should not be empty");
            phone.requestFocus();
            return false;
        }
        else {
            if (phoneNumber.length() <10) {
                phone.setError("Phone Number should be of 10 digits");
                phone.requestFocus();
                return false;
            }
            else if (phoneNumber.length() > 10) {
                phone.setError("Phone Number should be of 10 digits");
                phone.requestFocus();
                return false;
            }
            else {
                return true;
            }
        }
    }

    public boolean validateEmail(String userEmail){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        if (userEmail.isEmpty()){
            email.setError("Email Address cannot be empty");
            email.requestFocus();
            return false;
        }
        else {
            if (pat.matcher(userEmail).matches()) {
                return true;
            }
            else{
                email.setError("Email Address is not valid");
                email.requestFocus();
                return false;
            }
        }
    }

//    public boolean validatePass1(String pass1){
//        String WEAK = getResources().getString(R.string.weak);
//        String MEDIUM = getResources().getString(R.string.medium);
//        String STRONG = getResources().getString(R.string.strong);
//        String VERY_STRONG = getResources().getString(R.string.very_strong);
//
//        int MIN_LENGTH = 8;
//        int MAX_LENGTH = 15;
//        int score = 0;
//
//        if (pass1.isEmpty()){
//            password1.setError("Password Should Not Be Empty");
//            password1.requestFocus();
//            return false;
//        }
//        else {
//            for (int i=0; i<pass1.length(); i++){
//                ch = pass1.charAt(i);
//
//                if (Character.isUpperCase(ch)) {
//                    score++;
//                    upper = true;
//                }
//                else if (Character.isLowerCase(ch)) {
//                    score++;
//                    lower = true;
//                }
//                else if (Character.isDigit(ch)) {
//                    score++;
//                    number = true;
//                }
//                else if (ch == '!' || ch == '@' || ch == '#' || ch == '$' || ch == '%' || ch == '^' || ch == '&' || ch == '*' || ch == '(' || ch == ')' || ch == '-' || ch == '_' || ch == '=' || ch == '+' || ch == '[' || ch == '{' || ch == ']' || ch == '}' || ch == '|' || ch == ';' || ch == ':' || ch == '"' || ch == '<' || ch == ',' || ch == '>' || ch == '.' || ch == '/' || ch == '?'){
//                    score++;
//                    special = true;
//                }
//            }
//
//            if (upper && lower && number && special) {
//                password_strength.setText("Strong");
//                password_strength.setTextColor(Color.GREEN);
//                return true;
//            }
//            else{
//                password1.setError("Password should match below criteria");
//                password1.requestFocus();
//                return false;
//            }
//        }
//    }

//    public boolean validatePass2(String pass2){
//        if (pass2.isEmpty()){
//            password1.setError("Password Should Not Be Empty");
//            password1.requestFocus();
//            return false;
//        }
//        else {
//            if (pass2.length() < 8) {
//                password1.setError("Password should be of 8 characters");
//                password1.requestFocus();
//                return false;
//            }
//            else{
//                for (int i=0; i<pass2.length(); i++){
////                    Log.i(TAG, String.valueOf(pass2.charAt(i)));
//                    ch = pass2.charAt(i);
//
//                    if (Character.isUpperCase(ch)) {
//                        upper = true;
////                        Log.d(TAG, "validatePass1: upper");
//                    }
//                    else if (Character.isLowerCase(ch)) {
//                        lower = true;
////                        Log.d(TAG, "validatePass1: lower");
//                    }
//                    else if (Character.isDigit(ch)) {
//                        number = true;
////                        Log.d(TAG, "validatePass1: digit");
//                    }
//                    else if (ch == '!' || ch == '@' || ch == '#' || ch == '$' || ch == '%' || ch == '^' || ch == '&' || ch == '*' || ch == '(' || ch == ')' || ch == '-' || ch == '_' || ch == '=' || ch == '+' || ch == '[' || ch == '{' || ch == ']' || ch == '}' || ch == '|' || ch == ';' || ch == ':' || ch == '"' || ch == '<' || ch == ',' || ch == '>' || ch == '.' || ch == '/' || ch == '?'){
//                        special = true;
////                        Log.d(TAG, "validatePass1: special");
//                    }
//                }
//                if (upper && lower && number && special) {
//                    return true;
//                }
//                else{
//                    password2.setError("Password should match below criteria");
//                    password2.requestFocus();
//                    return false;
//                }
//            }
//        }
//    }

    public boolean validateBothPass(String pass1, String pass2){
        if (pass1.equals(pass2)){
            return true;
        }
        else {
            password2.setError("Both the passwords must be same");
            password2.requestFocus();
            return false;
        }
    }

    public void displayHidden(View v){
        progressBar.setVisibility(View.VISIBLE);
        password_strength.setVisibility(View.VISIBLE);
        login_instructions.setVisibility(View.VISIBLE);
    }

    private void calculatePasswordStrength(String password) {
        // Now, we need to define a PasswordStrength enum
        // with a calculate static method returning the password strength
//        PasswordStrength passwordStrength = PasswordStrength.calculate(str);
//        password_strength.setText(passwordStrength.msg);
//        password_strength.setTextColor(passwordStrength.color);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView strengthView = (TextView) findViewById(R.id.password_strength);
        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty()) {
            strengthView.setText("");
            progressBar.setProgress(0);
            return;
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        strengthView.setText(str.getText(this));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        }
        else if (str.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        }
        else if (str.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        }
        else {
            progressBar.setProgress(100);
        }
    }

//    public boolean validateDate(String dob){
//        String regex = "^(1[0-2]|0[1-9])/(3[01]"
//                + "|[12][0-9]|0[1-9])/[0-9]{4}$";
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(dob);
////        return matcher.matches();
//
//        if(matcher.matches()){
//            matcher.reset();
//
//            if(matcher.find()){
//                String day = matcher.group(1);
//                String month = matcher.group(2);
//                int year = Integer.parseInt(matcher.group(3));
//
//                if (day.equals("31") &&
//                        (month.equals("4") || month .equals("6") || month.equals("9") ||
//                                month.equals("11") || month.equals("04") || month .equals("06") ||
//                                month.equals("09"))) {
//                    date.setError("The Day is invalid. The " + month + " has 30 days not " + day + " days.");
//                    date.requestFocus();
//                    return false; // only 1,3,5,7,8,10,12 has 31 days
//                }
//                else if (month.equals("2") || month.equals("02")) {
//                    //leap year
//                    if(year % 4==0){
//                        if(day.equals("30") || day.equals("31")){
//                            date.setError("The Day is invalid. The " + year + " is a leap year and has 29 days not " + day + " days.");
//                            date.requestFocus();
//                            return false;
//                        }
//                        else{
//                            return true;
//                        }
//                    }
//                    else{
//                        if(day.equals("29")||day.equals("30")||day.equals("31")){
//                            date.setError("The Day is invalid. The " + year + " is not a leap year and has 28 days not " + day + " days.");
//                            date.requestFocus();
//                            return false;
//                        }
//                        else{
//                            return true;
//                        }
//                    }
//                }
//                else{
//                    return true;
//                }
//            }
//            else{
//                date.setError("Something went wrong.");
//                date.requestFocus();
//                return false;
//            }
//        }
//        else{
//            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//            Date dateNow = new Date();
//            date.setError("The Date-Of-Birth should be in proper format like: " + formatter.format(dateNow));
//            date.requestFocus();
//            return false;
//        }
////      return matcher.matches();
//    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        userDateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }
}
