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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public class SignUpActivityThree extends AppCompatActivity {

    private static final String TAG = "BTag";
    ImageView backArrow;
    Button nextPage, loginPage;
    TextView titleText, imageThree;
    EditText country, phone;
    CountryCodePicker cpp;

    boolean validatePhoneNumber = false;

    String fName, lName, userEmail, userPassword, userGender, number, countrySelected, dateDOBDay, dateDOBMonth, dateDOBYear, fullNumber, sendNumber, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_three);

        backArrow = findViewById(R.id.signupBackButton);
        nextPage = findViewById(R.id.signupPageThreeButton);
        loginPage = findViewById(R.id.loginButton);
        titleText = findViewById(R.id.signupTitleTextThree);
        phone = findViewById(R.id.phoneNumberThree);
//        country = findViewById(R.id.countryCode);
        imageThree = findViewById(R.id.textThree);
        cpp = findViewById(R.id.countryCode);
        cpp.registerCarrierNumberEditText(phone);

        fullNumber = cpp.getFullNumberWithPlus();
        Log.d(TAG, "onCreate: cpp: " + cpp.getFullNumberWithPlus());

        Bundle extras = getIntent().getExtras();
        fName = extras.getString("firstName");
        lName = extras.getString("lastName");
        userEmail = extras.getString("userEmail");
        userPassword = extras.getString("userPassword");
        userGender = extras.getString("userGender");
        dateDOBDay = extras.getString("userDOBDay");
        dateDOBMonth = extras.getString("userDOBMonth");
        dateDOBYear = extras.getString("userDOBYear");
        userType = extras.getString("userType");

        Log.d(TAG, "onCreate: date: " + dateDOBDay);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = phone.getText().toString();

                if (!number.isEmpty()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityThree.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignUpActivityThree.this, LoginActivity.class);

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityThree.this, pairs);
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
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityThree.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignUpActivityThree.this, LoginActivity.class);

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityThree.this, pairs);
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
//                    Intent intent = new Intent(SignUpActivityThree.this, LoginActivity.class);
//
//                    Pair[] pairs = new Pair[1];
//                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityThree.this, pairs);
//                    startActivity(intent, options.toBundle());
                }
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = phone.getText().toString();
//                countrySelected = country.getText().toString();

                validatePhoneNumber = validatePhone(number);

                if (validatePhoneNumber) {
                    sendNumber = "+" + fullNumber + number;
                    Intent nextIntent = new Intent(SignUpActivityThree.this, ShowSignUpData.class);

                    nextIntent.putExtra("firstName", fName);
                    nextIntent.putExtra("lastName", lName);
                    nextIntent.putExtra("userEmail", userEmail);
                    nextIntent.putExtra("userPassword", userPassword);
                    nextIntent.putExtra("userGender", userGender);
                    nextIntent.putExtra("userDOBDay", dateDOBDay);
                    nextIntent.putExtra("userDOBMonth", dateDOBMonth);
                    nextIntent.putExtra("userDOBYear", dateDOBYear);
                    nextIntent.putExtra("phone", sendNumber);
                    nextIntent.putExtra("userType", userType);

                    Log.d(TAG, "onClick: gender: " + userGender);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityThree.this, pairs);
                    startActivity(nextIntent, options.toBundle());
//                    startActivity(nextIntent);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivityThree.this).create();
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
//                number = phone.getText().toString();

//                if (!number.isEmpty()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityThree.this);
                    alertDialog.setTitle("Are You Sure?");
                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignUpActivityThree.this, LoginActivity.class);

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityThree.this, pairs);
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
//                    Intent intent = new Intent(SignUpActivityThree.this, LoginActivity.class);
//
//                    Pair[] pairs = new Pair[1];
//                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityThree.this, pairs);
//                    startActivity(intent, options.toBundle());
//                }
            }
        });
    }

    @Override
    public void onBackPressed() {

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
}