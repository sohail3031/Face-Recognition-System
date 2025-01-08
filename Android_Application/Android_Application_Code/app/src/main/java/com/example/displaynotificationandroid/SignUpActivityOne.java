package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class SignUpActivityOne extends AppCompatActivity {

    private static final String TAG = "MyTag";
    private static int SPLASH_TIME_OUT = 3000;
    ImageView backArrow;
    Button nextPage, loginPage, generatePasswordActivity;
    TextView titleText, imageOne;
    ProgressBar signUpOneProgress;
    TextView password_strength, login_instructions;
    EditText firstName, lastName, email, password1, password2, confirmEmail;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    boolean checkLogin = false;
    Switch switch1, switch2;
    ProgressBar progressBar;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    String fName, lName, userEmail, userPassword1, userPassword2;

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
    boolean foundEmail = false;
    boolean alreadyCreatedAccount = false;

    String generatedFName, generatedLName, generatedEmail, generatedPass1, generatedPass2, verifyEmail, verifyType, editUserType, generatedPassword, genetaredUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_one);

        backArrow = findViewById(R.id.signupBackButton);
        nextPage = findViewById(R.id.signupPageTwoButton);
        loginPage = findViewById(R.id.loginButton);
        titleText = findViewById(R.id.signupTitleText);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password1 = findViewById(R.id.password);
        password2 = findViewById(R.id.confirmPassword);
        progressBar = findViewById(R.id.progressBar);
        password_strength = findViewById(R.id.password_strength);
        login_instructions = findViewById(R.id.login_instructions);
        imageOne = findViewById(R.id.textOne);
        generatePasswordActivity = findViewById(R.id.generatePasswordActivity);
        signUpOneProgress = findViewById(R.id.signUpOneProgress);
//        confirmEmail = findViewById(R.id.dialogConfirmEmail);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

//        Intent i = getIntent();
//        generatedFName = i.getExtras("first");
//        Log.d(TAG, "onCreate: " + generatedFName);

        Bundle extras = getIntent().getExtras();
        generatedFName = extras.getString("fName");
        generatedLName = extras.getString("lName");
        generatedEmail = extras.getString("email");
        generatedPass1 = extras.getString("password1");
        generatedPass2 = extras.getString("password2");
        generatedPassword = extras.getString("generatedPassword");
        genetaredUserType = extras.getString("genetaredUserType");

        Log.d(TAG, "onCreate: fname" + generatedFName);
        Log.d(TAG, "onCreate: lname" + generatedLName);
        Log.d(TAG, "onCreate: email" + generatedEmail);
        Log.d(TAG, "onCreate: pass1" + generatedPass1);
        Log.d(TAG, "onCreate: pass2" + generatedPass2);

        if (generatedPassword.equals("no")) {
            generatePasswordActivity.setClickable(false);
            nextPage.setClickable(false);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            LayoutInflater inflater = this.getLayoutInflater();
            View newView = inflater.inflate(R.layout.layout_new_user_email, null);

            alertDialog.setView(newView)
                    .setTitle("To Create An Account, Plesae Verify Your Email")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            verifyEmail = confirmEmail.getText().toString();

                            signUpOneProgress.setVisibility(View.VISIBLE);

                            verifyEnteredEmail(verifyEmail);
                            existEmail(verifyEmail);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                Toast.makeText(SignUpActivityOne.this, "Time out", Toast.LENGTH_SHORT).show();

                                    if (foundEmail && (verifyEmail != null || !verifyEmail.equals(""))) {
                                        signUpOneProgress.setVisibility(View.GONE);

                                        Toast.makeText(SignUpActivityOne.this, "You can create your account", Toast.LENGTH_SHORT).show();
                                        //        if(!generatedFName.isEmpty() && !generatedLName.isEmpty() && !generatedEmail.isEmpty() && !generatedPass1.isEmpty() && !generatedPass2.isEmpty()){
                                        firstName.setText(generatedFName);
                                        lastName.setText(generatedLName);
                                        email.setText(verifyEmail);
                                        password1.setText(generatedPass1);
                                        password2.setText(generatedPass2);
//        }

                                        firstName.setClickable(true);
                                        firstName.setCursorVisible(true);
                                        firstName.setFocusable(true);
                                        firstName.setFocusableInTouchMode(true);

                                        lastName.setClickable(true);
                                        lastName.setCursorVisible(true);
                                        lastName.setFocusable(true);
                                        lastName.setFocusableInTouchMode(true);

                                        password1.setClickable(true);
                                        password1.setCursorVisible(true);
                                        password1.setFocusable(true);
                                        password1.setFocusableInTouchMode(true);

                                        password2.setClickable(true);
                                        password2.setCursorVisible(true);
                                        password2.setFocusable(true);
                                        password2.setFocusableInTouchMode(true);

                                        generatePasswordActivity.setClickable(true);
                                        nextPage.setClickable(true);

                                        generatePasswordActivity.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                fName = firstName.getText().toString().trim();
                                                lName = lastName.getText().toString().trim();
                                                userEmail = email.getText().toString().trim();

                                                Intent intent = new Intent(SignUpActivityOne.this, GeneratePassword.class);

                                                intent.putExtra("fName", fName);
                                                intent.putExtra("lName", lName);
                                                intent.putExtra("email", userEmail);
                                                intent.putExtra("genetaredUserType", verifyType);

                                                Pair[] pairs = new Pair[1];
                                                pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                                                startActivity(intent, options.toBundle());
                                            }
                                        });

                                        backArrow.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                fName = firstName.getText().toString().trim();
                                                lName = lastName.getText().toString().trim();
                                                userEmail = email.getText().toString().trim();
                                                userPassword1 = password1.getText().toString().trim();
                                                userPassword2 = password2.getText().toString().trim();

                                                if (!fName.isEmpty() || !lName.isEmpty() || !userEmail.isEmpty() || !userPassword1.isEmpty() || !userPassword2.isEmpty()) {
                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityOne.this);
                                                    alertDialog.setTitle("Are You Sure?");
                                                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                                                            Pair[] pairs = new Pair[1];
                                                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
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
                                                    Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                                                    Pair[] pairs = new Pair[1];
                                                    pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                                                    startActivity(intent, options.toBundle());
                                                }
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

                                        nextPage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                fName = firstName.getText().toString().trim();
                                                lName = lastName.getText().toString().trim();
                                                userEmail = email.getText().toString().trim();
                                                userPassword1 = password1.getText().toString().trim();
                                                userPassword2 = password2.getText().toString().trim();

                                                validateFirstName = validateFName(fName);
                                                validateLastName = validateLName(fName);
                                                validateBothName = validateBName(fName, lName);
                                                validateEmailAddress = validateEmail(userEmail);
//                validatePassword1 = validatePass1(userPassword1);
//                validatePassword2 = validatePass2(userPassword2);
                                                validateBothPassword = validateBothPass(userPassword1, userPassword2);

                                                if (validateFirstName && validateLastName && validateBothName && validateEmailAddress && validateBothPassword) {
                                                    if(userPassword1.length() >= 6 && userPassword2.length() >= 6){
                                                        Intent nextIntent = new Intent(SignUpActivityOne.this, SignUpActivityTwo.class);

                                                        nextIntent.putExtra("firstName", fName);
                                                        nextIntent.putExtra("lastName", lName);
                                                        nextIntent.putExtra("userEmail", userEmail);
                                                        nextIntent.putExtra("userPassword", userPassword1);
                                                        nextIntent.putExtra("userType", verifyType);

                                                        Pair[] pairs = new Pair[5];
                                                        pairs[0] = new Pair<View, String>(backArrow, "transition_back_arrow_button");
                                                        pairs[1] = new Pair<View, String>(nextPage, "transition_next_button");
                                                        pairs[2] = new Pair<View, String>(loginPage, "transition_login_button");
                                                        pairs[3] = new Pair<View, String>(titleText, "logo_text");
                                                        pairs[4] = new Pair<View, String>(imageOne, "image_transition");

                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                                                        startActivity(nextIntent, options.toBundle());
                                                    }
                                                    else{
                                                        if (userPassword1.length() < 6) {
                                                            password1.setError("Password Should Contain At least 6 Characters");
                                                            password1.requestFocus();
                                                        }
                                                        else if (userPassword2.length() < 6) {
                                                            password1.setError("Confirm Password Should Contain At least 6 Characters");
                                                            password1.requestFocus();
                                                        }
                                                    }
                                                }
                                                else{
                                                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivityOne.this).create();
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
                                                fName = firstName.getText().toString().trim();
                                                lName = lastName.getText().toString().trim();
                                                userEmail = email.getText().toString().trim();
                                                userPassword1 = password1.getText().toString().trim();
                                                userPassword2 = password2.getText().toString().trim();

                                                if (!fName.isEmpty() || !lName.isEmpty() || !userEmail.isEmpty() || !userPassword1.isEmpty() || !userPassword2.isEmpty()) {
                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityOne.this);
                                                    alertDialog.setTitle("Are You Sure?");
                                                    alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                                                            Pair[] pairs = new Pair[1];
                                                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
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
                                                    Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                                                    Pair[] pairs = new Pair[1];
                                                    pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                                                    startActivity(intent, options.toBundle());
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(SignUpActivityOne.this, "You are not allowed to create an account", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                                        intent.putExtra("email", "");

                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                                        startActivity(intent, options.toBundle());
                                    }
                                }
                            }, SPLASH_TIME_OUT);

//                        if (verifyEmail.length() > 0) {
//                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Added New User");
//                            databaseReference.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                                        String firstKey = dataSnapshot1.getKey();
//                                        String compareEmailAddress = String.valueOf(dataSnapshot1.child("userEmail").getValue());
//                                        if (compareEmailAddress.equals(verifyEmail)) {
//                                            verifyType = String.valueOf(dataSnapshot1.child("userType").getValue());
//                                            foundEmail = true;
//                                            Toast.makeText(SignUpActivityOne.this, "Email Found: " + verifyEmail, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(SignUpActivityOne.this, "Compared Email: " + compareEmailAddress, Toast.LENGTH_SHORT).show();
////
////                                        Toast.makeText(SignUpActivityOne.this, "Founf Email: " + foundEmail, Toast.LENGTH_SHORT).show();
//
//                                            break;
//                                        }
//                                        else {
//                                            Toast.makeText(SignUpActivityOne.this, "Email not found", Toast.LENGTH_SHORT).show();
//
//                                            verifyEmail = confirmEmail.getText().toString();
////                                            verifyType = String.valueOf(dataSnapshot1.child("userType").getValue());
//
////                                            Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);
////
////                                            intent.putExtra("email", "");
////
////                                            Pair[] pairs = new Pair[1];
////                                            pairs[0] = new Pair<View, String>(titleText, "logo_text");
////
////                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
////                                            startActivity(intent, options.toBundle());
//                                        }
//
//                                        break;
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                        else{
//                            Toast.makeText(SignUpActivityOne.this, "You are not allowed to create an account", Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);
//
//                            Pair[] pairs = new Pair[1];
//                            pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
//                            startActivity(intent, options.toBundle());
//                        }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Toast.makeText(SignUpActivityOne.this, "Verification Failed", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                            intent.putExtra("email", "");

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                            startActivity(intent, options.toBundle());
                        }
                    });

            confirmEmail = newView.findViewById(R.id.dialogConfirmEmail);

            alertDialog.show();
        }
        else {
//            Toast.makeText(SignUpActivityOne.this, "verify type: " + genetaredUserType, Toast.LENGTH_SHORT).show();
            //        if(!generatedFName.isEmpty() && !generatedLName.isEmpty() && !generatedEmail.isEmpty() && !generatedPass1.isEmpty() && !generatedPass2.isEmpty()){
            firstName.setText(generatedFName);
            lastName.setText(generatedLName);
            email.setText(generatedEmail);
            password1.setText(generatedPass1);
            password2.setText(generatedPass2);
//        }

            firstName.setClickable(true);
            firstName.setCursorVisible(true);
            firstName.setFocusable(true);
            firstName.setFocusableInTouchMode(true);

            lastName.setClickable(true);
            lastName.setCursorVisible(true);
            lastName.setFocusable(true);
            lastName.setFocusableInTouchMode(true);

            password1.setClickable(true);
            password1.setCursorVisible(true);
            password1.setFocusable(true);
            password1.setFocusableInTouchMode(true);

            password2.setClickable(true);
            password2.setCursorVisible(true);
            password2.setFocusable(true);
            password2.setFocusableInTouchMode(true);

            generatePasswordActivity.setClickable(true);
            nextPage.setClickable(true);

            generatePasswordActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fName = firstName.getText().toString().trim();
                    lName = lastName.getText().toString().trim();
                    userEmail = email.getText().toString().trim();

                    Intent intent = new Intent(SignUpActivityOne.this, GeneratePassword.class);

                    intent.putExtra("fName", fName);
                    intent.putExtra("lName", lName);
                    intent.putExtra("email", userEmail);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(titleText, "logo_text");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            });

            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fName = firstName.getText().toString().trim();
                    lName = lastName.getText().toString().trim();
                    userEmail = email.getText().toString().trim();
                    userPassword1 = password1.getText().toString().trim();
                    userPassword2 = password2.getText().toString().trim();

                    if (!fName.isEmpty() || !lName.isEmpty() || !userEmail.isEmpty() || !userPassword1.isEmpty() || !userPassword2.isEmpty()) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityOne.this);
                        alertDialog.setTitle("Are You Sure?");
                        alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                                Pair[] pairs = new Pair[1];
                                pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
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
                        Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(titleText, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                        startActivity(intent, options.toBundle());
                    }
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

            nextPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fName = firstName.getText().toString().trim();
                    lName = lastName.getText().toString().trim();
                    userEmail = email.getText().toString().trim();
                    userPassword1 = password1.getText().toString().trim();
                    userPassword2 = password2.getText().toString().trim();

                    validateFirstName = validateFName(fName);
                    validateLastName = validateLName(fName);
                    validateBothName = validateBName(fName, lName);
                    validateEmailAddress = validateEmail(userEmail);
//                validatePassword1 = validatePass1(userPassword1);
//                validatePassword2 = validatePass2(userPassword2);
                    validateBothPassword = validateBothPass(userPassword1, userPassword2);

                    if (validateFirstName && validateLastName && validateBothName && validateEmailAddress && validateBothPassword) {
                        if(userPassword1.length() >= 6 && userPassword2.length() >= 6){
                            Intent nextIntent = new Intent(SignUpActivityOne.this, SignUpActivityTwo.class);

                            nextIntent.putExtra("firstName", fName);
                            nextIntent.putExtra("lastName", lName);
                            nextIntent.putExtra("userEmail", userEmail);
                            nextIntent.putExtra("userPassword", userPassword1);
                            nextIntent.putExtra("userType", genetaredUserType);

                            Pair[] pairs = new Pair[5];
                            pairs[0] = new Pair<View, String>(backArrow, "transition_back_arrow_button");
                            pairs[1] = new Pair<View, String>(nextPage, "transition_next_button");
                            pairs[2] = new Pair<View, String>(loginPage, "transition_login_button");
                            pairs[3] = new Pair<View, String>(titleText, "logo_text");
                            pairs[4] = new Pair<View, String>(imageOne, "image_transition");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                            startActivity(nextIntent, options.toBundle());
                        }
                        else{
                            if (userPassword1.length() < 6) {
                                password1.setError("Password Should Contain At least 6 Characters");
                                password1.requestFocus();
                            }
                            else if (userPassword2.length() < 6) {
                                password1.setError("Confirm Password Should Contain At least 6 Characters");
                                password1.requestFocus();
                            }
                        }
                    }
                    else{
                        AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivityOne.this).create();
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
                    fName = firstName.getText().toString().trim();
                    lName = lastName.getText().toString().trim();
                    userEmail = email.getText().toString().trim();
                    userPassword1 = password1.getText().toString().trim();
                    userPassword2 = password2.getText().toString().trim();

                    if (!fName.isEmpty() || !lName.isEmpty() || !userEmail.isEmpty() || !userPassword1.isEmpty() || !userPassword2.isEmpty()) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivityOne.this);
                        alertDialog.setTitle("Are You Sure?");
                        alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                                Pair[] pairs = new Pair[1];
                                pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
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
                        Intent intent = new Intent(SignUpActivityOne.this, LoginActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(titleText, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivityOne.this, pairs);
                        startActivity(intent, options.toBundle());
                    }
                }
            });
        }
    }

    private void verifyEnteredEmail(final String email) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Added New User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(SignUpActivityOne.this, "Please Wait", Toast.LENGTH_SHORT).show();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String firstKey = dataSnapshot1.getKey();
                    String compareEmailAddress = String.valueOf(dataSnapshot1.child("userEmail").getValue());
                    if (compareEmailAddress.equals(verifyEmail)) {
                        verifyType = String.valueOf(dataSnapshot1.child("userType").getValue());
                        foundEmail = true;
//                        Toast.makeText(SignUpActivityOne.this, "Email Found", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void existEmail(final String email){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("USERS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String firstKey = dataSnapshot1.getKey();
                    String compareEmailAddress = String.valueOf(dataSnapshot1.child("userEmail").getValue());
                    if (compareEmailAddress.equals(verifyEmail)) {
                        verifyType = String.valueOf(dataSnapshot1.child("userType").getValue());
                        alreadyCreatedAccount = true;
                        break;
                    }
                    else {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

            @Override
    public void onBackPressed() {

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

        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView strengthView = findViewById(R.id.password_strength);
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
}