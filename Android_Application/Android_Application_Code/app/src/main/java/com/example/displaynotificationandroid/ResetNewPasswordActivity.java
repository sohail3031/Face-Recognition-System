package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ResetNewPasswordActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";

    String email, password, confirmPassword, generatedPassword, generatedConfirmPassword, value;
    String pass = "sohail";

    EditText resetNewPassword, resetNewConfirmPassword;
    ProgressBar resetProgressBar, resetNewProgressBar;
    TextView reset_password_strength, login_instructions, titleText;
    Button setNewPassword, generateNewPassword;
    Switch switch1, switch2;
    ImageView resetBackButtonShow;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    boolean isWifiConn = false;
    boolean isMobileConn = false;
    boolean checkLogin = false;
    boolean passwordUpdated = false;
    boolean validatePassword1 = false;
    boolean validatePassword2 = false;
    boolean validateBothPassword = false;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference notifyDatabase;

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

        setContentView(R.layout.activity_reset_new_password);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        password = extras.getString("password1");
        confirmPassword = extras.getString("password2");

        Log.d(TAG, "onCreate: final email: " + email);
        Log.d(TAG, "onCreate: final password: " + password);

        resetNewPassword = findViewById(R.id.resetNewPassword);
        resetNewConfirmPassword = findViewById(R.id.resetNewConfirmPassword);
        resetProgressBar = findViewById(R.id.resetProgressBar);
        reset_password_strength = findViewById(R.id.reset_password_strength);
        setNewPassword = findViewById(R.id.setNewPassword);
        generateNewPassword = findViewById(R.id.generateNewPassword);
        switch1 = findViewById(R.id.resetOpenWifiButtonShow);
        switch2 = findViewById(R.id.resetOpenMobileDataShow);
        titleText = findViewById(R.id.newPasswordTitle);
        resetBackButtonShow = findViewById(R.id.resetBackButtonShow);
        resetNewProgressBar = findViewById(R.id.resetNewProgressBar);

//        if (password != null) {
            resetNewPassword.setText(password);
            resetNewConfirmPassword.setText(confirmPassword);
//        }

//        password = resetNewPassword.getText().toString();
//        confirmPassword = resetNewConfirmPassword.getText().toString();

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

        resetBackButtonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResetNewPasswordActivity.this);
                alertDialog.setTitle("Are You Sure?");
                alertDialog.setMessage("If you go back your data will lost. So are you sure?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ResetNewPasswordActivity.this, LoginActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(titleText, "logo_text");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
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

        setNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(password.equals("") || password == null){
                    generatedPassword = resetNewPassword.getText().toString().trim();
                    generatedConfirmPassword = resetNewConfirmPassword.getText().toString().trim();
//                }
//                else{
//                    generatedPassword = password;
//                    generatedConfirmPassword = password;
//                }

                Log.d(TAG, "onClick: gen1: " + generatedPassword);
                Log.d(TAG, "onClick: gen2: " + generatedConfirmPassword);

                validatePassword1 = validatePass1(generatedPassword);
                validatePassword2 = validatePass2(generatedConfirmPassword);
                validateBothPassword = validateBothPass(generatedPassword, generatedConfirmPassword);

                if (!checkLogin) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ResetNewPasswordActivity.this).create();
                    alertDialog.setTitle("No Internet Connection");
                    alertDialog.setMessage("It seams you are offline but you need internet connection to login");
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
                else if (validatePassword1 && validatePassword2 && validateBothPassword) {
//                    firebaseAuth = FirebaseAuth.getInstance();
//                    Log.d(TAG, "onDataChange: Firebase Auth: " + firebaseAuth.toString());
//
//                    AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
//
//                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(ResetNewPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
//
//                                        Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
//
//                                        Pair[] pairs = new Pair[1];
//                                        pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
//                                        startActivity(intent, options.toBundle());
//                                    }
//                                    else{
//                                        Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//                        }
//                    });

//                    firebaseUser.updatePassword(generatedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(ResetNewPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
//                                Log.d(TAG, "onComplete: password updated success");
//
//                                Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
//
//                                Pair[] pairs = new Pair[1];
//                                pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
//                                startActivity(intent, options.toBundle());
//                            }
//                            else{
//                                Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                Log.d(TAG, "onComplete: password updated fail");
//                            }
//                        }
//                    });

//                    if (firebaseUser != null) {
//                        Toast.makeText(ResetNewPasswordActivity.this, "Firebase Auth is null", Toast.LENGTH_LONG).show();
//                        Log.d(TAG, "onClick: fire suth is not null");
//                    }
//                    else{
//                        firebaseUser.updatePassword(generatedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(ResetNewPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
//                                    Log.d(TAG, "onComplete: password updated success");
//
//                                    Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
//
//                                    Pair[] pairs = new Pair[1];
//                                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
//                                    startActivity(intent, options.toBundle());
//                                }
//                                else{
//                                    Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                    Log.d(TAG, "onComplete: password updated fail");
//                                }
//                            }
//                        });
//                    }
                    notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

                    notifyDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            resetNewProgressBar.setVisibility(View.VISIBLE);

                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                String firstKey = dataSnapshot1.getKey();
                                Log.d(TAG, "onDataChange: first: " + firstKey);
                                String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                                Log.d(TAG, "onDataChange: second: " + databaseEmail);
                                if (email.equals(databaseEmail)) {
//                                String key = databaseEmail;
                                    pass = String.valueOf(dataSnapshot1.child("userPassword").getValue());
                                    Log.d(TAG, "onDataChange: pass: " + pass);
                                    notifyDatabase.child("Updated Password").child(firstKey).child("userPassword").setValue(resetNewPassword.getText().toString());
                                    Log.d(TAG, "onDataChange: updated: " + notifyDatabase.child(firstKey).child("userPassword").setValue(resetNewPassword.getText().toString()));
                                    Log.d(TAG, "onDataChange: password: " + dataSnapshot1.child("userPassword").getValue());
                                    value = String.valueOf(dataSnapshot1.child("userPassword").getValue());
                                    Log.d(TAG, "onDataChange: value: " + value);

                                    firebaseAuth = FirebaseAuth.getInstance();
                                    firebaseAuth.getCurrentUser();

                                    Log.d(TAG, "onDataChange: reset password: " + resetNewPassword.getText().toString().trim());

                                    AuthCredential credential = EmailAuthProvider.getCredential(email, pass);

                                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            firebaseUser.updatePassword(resetNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ResetNewPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();

                                                        Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);

                                                        intent.putExtra("email", email);

                                                        Pair[] pairs = new Pair[1];
                                                        pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                                        firebaseAuth.signOut();

                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
                                                        startActivity(intent, options.toBundle());
                                                    }
                                                    else{
                                                        Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

//                                    firebaseUser.updatePhoneNumber()

//                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                                    if (user != null) {
//                                        Log.d(TAG, "onDataChange: Sign in success");
//                                        Log.d(TAG, "onDataChange: reset password: " + resetNewPassword.getText().toString().trim());
//
//                                        firebaseUser.updatePassword("sohail789").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    Toast.makeText(ResetNewPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
//                                                    Log.d(TAG, "onComplete: password updated success");
//
//                                                    firebaseAuth.signOut();
//
//                                                    Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
//
//                                                    Pair[] pairs = new Pair[1];
//                                                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
//                                                    startActivity(intent, options.toBundle());
//                                                }
//                                                else{
//                                                    Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                    Log.d(TAG, "onComplete: password updated fail");
//                                                }
//                                            }
//                                        });
                                    break;
                                    }
                                    else{
                                        Log.d(TAG, "onDataChange: sign in failed");
                                        continue;
                                    }

//                                    passwordUpdated = true;

//                                    firebaseAuth = FirebaseAuth.getInstance();
//                                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(DummyLoginActivity.this, new OnCompleteListener<AuthResult>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<AuthResult> task) {
//                                            if (!task.isSuccessful()){
//                                                Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                            else{
//                                                Intent intent = new Intent();
//                                            }
//                                        }
//                                    });

//                                    break;
//                                }
                            }

//                                    AuthCredential credential = EmailAuthProvider.getCredential(email, pass);

//                                    firebaseAuth.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            firebaseAuth.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(ResetNewPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
//
//                                                        Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
//
//                                                        Pair[] pairs = new Pair[1];
//                                                        pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
//                                                        startActivity(intent, options.toBundle());
//                                                    }
//                                                    else{
//                                                        Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    });
//                                }
//                            }

//                            resetNewProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//                    if (passwordUpdated) {
//                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                        Log.d(TAG, "onDataChange: Firebase Auth: " + firebaseAuth.toString());
//
//                        if (firebaseUser != null) {
//                            Toast.makeText(ResetNewPasswordActivity.this, "Firebase Auth is null", Toast.LENGTH_LONG).show();
//                        }
//                        else{
//                            firebaseUser.updatePassword(generatedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(ResetNewPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
//                                        Log.d(TAG, "onComplete: password updated success");
//
//                                        Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
//
//                                        Pair[] pairs = new Pair[1];
//                                        pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
//                                        startActivity(intent, options.toBundle());
//                                    }
//                                    else{
//                                        Toast.makeText(ResetNewPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                        Log.d(TAG, "onComplete: password updated fail");
//                                    }
//                                }
//                            });
//                        }
//                    }
                }

//                Log.d(TAG, "onClick: reset password: " + passwordUpdated);
//
//                if (passwordUpdated) {
//                    Intent intent = new Intent(ResetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
//
//                    Pair[] pairs = new Pair[1];
//                    pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
//                    startActivity(intent, options.toBundle());
//                }
            }
        });

        generateNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetNewPasswordActivity.this, MobileGeneratePassword.class);

                intent.putExtra("email", email);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(titleText, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetNewPasswordActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        resetNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                resetProgressBar.setVisibility(View.VISIBLE);
//                reset_password_strength.setVisibility(View.VISIBLE);
//                login_instructions.setVisibility(View.VISIBLE);
                calculatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        resetNewConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                resetProgressBar.setVisibility(View.VISIBLE);
//                reset_password_strength.setVisibility(View.VISIBLE);
//                login_instructions.setVisibility(View.VISIBLE);
                calculatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
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

        if (passwordUpdated) {

        }
    }

    @Override
    public void onBackPressed() {

    }

    public boolean validatePass1(String pass1){
        if (pass1.length() < 6) {
            resetNewPassword.setError("New Password length should be no less than 6");
            resetNewPassword.requestFocus();
            return false;
        }
        else{
            return true;
        }
    }

    public boolean validatePass2(String pass2){
        if (pass2.length() < 6) {
            resetNewConfirmPassword.setError("New Password length should be no less than 6");
            resetNewConfirmPassword.requestFocus();
            return false;
        }
        else{
            return true;
        }
    }

    public boolean validateBothPass(String pass1, String pass2){
        if (pass1.equals(pass2)){
            return true;
        }
        else {
            resetNewConfirmPassword.setError("Both the passwords must be same");
            resetNewConfirmPassword.requestFocus();
            return false;
        }
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

    public void displayHidden(View v){
        resetProgressBar.setVisibility(View.VISIBLE);
        reset_password_strength.setVisibility(View.VISIBLE);
        login_instructions.setVisibility(View.VISIBLE);
    }

    private void calculatePasswordStrength(String password) {
        // Now, we need to define a PasswordStrength enum
        // with a calculate static method returning the password strength
//        PasswordStrength passwordStrength = PasswordStrength.calculate(str);
//        password_strength.setText(passwordStrength.msg);
//        password_strength.setTextColor(passwordStrength.color);

        ProgressBar progressBar = findViewById(R.id.resetProgressBar);
        TextView strengthView = findViewById(R.id.reset_password_strength);
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
