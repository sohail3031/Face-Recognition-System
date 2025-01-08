package com.example.displaynotificationandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivityTwo extends AppCompatActivity {

    private static final String TAG = "BTag";
    ImageView backArrow;
    Button nextPage, loginPage;
    TextView titleText, imageTwo;
    Spinner gender;
    DatePicker userDateOfBirth;

    String genderSelected;
    int dateBirthDay;
    int dateBirthMonth;
    int dateBirthYear;

    boolean validateGender = false;
    boolean validateDOB = false;
    boolean validateDOBYear = false;

    String fName, lName, userEmail, userPassword, userType;
    String day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two);

        backArrow = findViewById(R.id.signupBackButton);
        nextPage = findViewById(R.id.signupPageTwoButton);
        loginPage = findViewById(R.id.loginButton);
        titleText = findViewById(R.id.signupTitleTextTwo);
        gender = findViewById(R.id.gender);
        userDateOfBirth = findViewById(R.id.userDOB);
        imageTwo = findViewById(R.id.textTwo);

        Bundle extras = getIntent().getExtras();
        fName = extras.getString("firstName");
        lName = extras.getString("lastName");
        userEmail = extras.getString("userEmail");
        userPassword = extras.getString("userPassword");
        userType = extras.getString("userType");

        Log.d(TAG, "onCreate: fname" + fName);
        Log.d(TAG, "onCreate: fname" + lName);
        Log.d(TAG, "onCreate: fname" + userEmail);
        Log.d(TAG, "onCreate: fname" + userPassword);

        String[] items = new String[]{"Gender", "Male", "Female", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gender.setAdapter(adapter);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderSelected = String.valueOf(gender.getSelectedItem());
//                dateBirth = userDateOfBirth.toString();

                if (!(gender.equals("gender") || gender.equals("Gender") || gender.equals("GENDER"))) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityTwo.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignUpActivityTwo.this, LoginActivity.class);

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityTwo.this, pairs);
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
                else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityTwo.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignUpActivityTwo.this, LoginActivity.class);

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityTwo.this, pairs);
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
//                    Intent intent = new Intent(SignUpActivityTwo.this, LoginActivity.class);
//
//                    Pair[] pairs = new Pair[1];
//                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityTwo.this, pairs);
//                    startActivity(intent, options.toBundle());
                }
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderSelected = String.valueOf(gender.getSelectedItem());
//                dateBirth = userDateOfBirth.toString();
                dateBirthDay = userDateOfBirth.getDayOfMonth();
                dateBirthMonth = userDateOfBirth.getMonth()+1;
                dateBirthYear = userDateOfBirth.getYear();
                day = Integer.toString(dateBirthDay);
                month = Integer.toString(dateBirthMonth);
                year = Integer.toString(dateBirthYear);

                Log.d(TAG, "onClick: dob: " + userDateOfBirth.getDayOfMonth() + " " + userDateOfBirth.getMonth()+1 + " " + userDateOfBirth.getYear());

                validateGender = validateG(genderSelected);
//                validateDOB = validateDate(dateBirth);
                validateDOBYear = validateDateYear(dateBirthYear);

                if (validateGender && validateDOBYear) {
                    Intent nextIntent = new Intent(SignUpActivityTwo.this, SignUpActivityThree.class);

                    nextIntent.putExtra("firstName", fName);
                    nextIntent.putExtra("lastName", lName);
                    nextIntent.putExtra("userEmail", userEmail);
                    nextIntent.putExtra("userPassword", userPassword);
                    nextIntent.putExtra("userGender", genderSelected);
                    nextIntent.putExtra("userDOBDay", day);
                    nextIntent.putExtra("userDOBMonth", month);
                    nextIntent.putExtra("userDOBYear", year);
                    nextIntent.putExtra("userType", userType);

                    Pair[] pairs = new Pair[5];
                    pairs[0] = new Pair<View, String>(backArrow, "transition_back_arrow_button");
                    pairs[1] = new Pair<View, String>(nextPage, "transition_next_button");
                    pairs[2] = new Pair<View, String>(loginPage, "transition_login_button");
                    pairs[3] = new Pair<View, String>(titleText, "logo_text");
                    pairs[4] = new Pair<View, String>(imageTwo, "image_transition");
//
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityTwo.this, pairs);
                    startActivity(nextIntent, options.toBundle());
//                    startActivity(nextIntent);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivityTwo.this).create();
                    alertDialog.setTitle("Please Fill The Data");
                    alertDialog.setMessage("To proceed forward, please fill the required data");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
//                    Toast.makeText(SignUpActivityOne.this, "Fill All Fields!", Toast.LENGTH_LONG).show();
                }
            }
        });

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                genderSelected = String.valueOf(gender.getSelectedItem());
//                dateBirth = userDateOfBirth.toString();

//                if (!(gender.equals("gender") || gender.equals("Gender") || gender.equals("GENDER"))) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityTwo.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignUpActivityTwo.this, LoginActivity.class);

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityTwo.this, pairs);
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
//                }
//                else{
//                    Intent intent = new Intent(SignUpActivityTwo.this, LoginActivity.class);
//
//                    Pair[] pairs = new Pair[1];
//                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityTwo.this, pairs);
//                    startActivity(intent, options.toBundle());
//                }
            }
        });
    }

    @Override
    public void onBackPressed() {

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
                userDateOfBirth.requestFocus();
                return false;
            }
        }
        else {
            userDateOfBirth.requestFocus();
            return false;
        }
    }
}