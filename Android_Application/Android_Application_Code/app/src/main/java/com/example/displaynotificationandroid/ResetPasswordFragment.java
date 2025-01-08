package com.example.displaynotificationandroid;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordFragment extends Fragment {

    DatabaseReference notifyDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    boolean isWifiConn, isMobileConn, checkLogin, validatePass1, validatePass2, validateBothPass = false;

    final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String currentUserPassword, password, newPassword, confirmPassword;

    ProgressBar progressBar;
    TextView strengthView;
    TextView textDescription;
//    EditText resetNewPasswordResetCurrent, resetNewPasswordReset, resetNewConfirmPasswordReset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resset_password, container, false);

        final TextView userNameResetContact = (TextView) view.findViewById(R.id.userNameResetContact);
        final TextView userEmailInfoResetContact = (TextView) view.findViewById(R.id.userEmailInfoResetContact);
        final EditText resetNewPasswordResetCurrent = view.findViewById(R.id.resetNewPasswordResetCurrent);
        final EditText resetNewPasswordReset = view.findViewById(R.id.resetNewPasswordReset);
        final EditText resetNewConfirmPasswordReset = view.findViewById(R.id.resetNewConfirmPasswordReset);
        Button setNewPasswordReset = (Button) view.findViewById(R.id.setNewPasswordReset);
        Button generateNewPasswordReset = (Button) view.findViewById(R.id.generateNewPasswordReset);

        progressBar = view.findViewById(R.id.resetProgressBarReset);
        strengthView = view.findViewById(R.id.reset_password_strengthReset);
        textDescription = view.findViewById(R.id.login_instructionsReset);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
                Log.d("NewTag", "onCreate: wifi");
                checkLogin = true;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
                Log.d("NewTag", "onCreate: data");
                checkLogin = true;
            }
        }

        if (checkLogin) {
            notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
            notifyDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        String firstKey = dataSnapshot1.getKey();
                        Log.d("NewTag", "onDataChange: first: " + firstKey);
                        String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                        Log.d("NewTag", "onDataChange: second: " + databaseEmail);
                        if (userEmail.equals(databaseEmail)) {
                            userNameResetContact.setText(String.valueOf(dataSnapshot1.child("userFirstName").getValue())
                                    + " " + String.valueOf(dataSnapshot1.child("userLastName").getValue()));
                            userEmailInfoResetContact.setText(String.valueOf(dataSnapshot1.child("userEmailId").getValue()));
                            currentUserPassword = String.valueOf(dataSnapshot1.child("userPassword").getValue());
//                            Toast.makeText(getContext(), "Current user password: " + currentUserPassword, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            password = resetNewPasswordResetCurrent.getText().toString();
            newPassword = resetNewPasswordReset.getText().toString();
            confirmPassword = resetNewConfirmPasswordReset.getText().toString();

            resetNewPasswordReset.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                strengthView.setVisibility(View.VISIBLE);
                textDescription.setVisibility(View.VISIBLE);
                    calculatePasswordStrength(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            resetNewConfirmPasswordReset.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    progressBar.setVisibility(View.VISIBLE);
                    strengthView.setVisibility(View.VISIBLE);
                    textDescription.setVisibility(View.VISIBLE);
                    calculatePasswordStrength(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            generateNewPasswordReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GeneratePasswordFragment generatePasswordFragment = new GeneratePasswordFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, generatePasswordFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            setNewPasswordReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), "Entered password: " + resetNewPasswordResetCurrent.getText().toString() + " " + newPassword + " " + confirmPassword, Toast.LENGTH_SHORT).show();
                    if (resetNewPasswordResetCurrent.getText().toString().equals(currentUserPassword)) {
                        validatePass1 = validatePass1(resetNewPasswordReset.getText().toString());
                        validatePass2 = validatePass2(resetNewConfirmPasswordReset.getText().toString());
                        validateBothPass = validateBothPass(resetNewPasswordReset.getText().toString(), resetNewConfirmPasswordReset.getText().toString());

                        if (validatePass1 && validatePass2 && validateBothPass) {
                            AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentUserPassword);

                            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseAuth.getCurrentUser();

//                            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseUser.updatePassword(resetNewPasswordReset.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();

                                                notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
                                                notifyDatabase.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                                            String firstKey = dataSnapshot1.getKey();
                                                            Log.d("NewTag", "onDataChange: first: " + firstKey);
                                                            String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                                                            Log.d("NewTag", "onDataChange: second: " + databaseEmail);
                                                            if (userEmail.equals(databaseEmail)) {
                                                                notifyDatabase.child(firstKey).child("userPassword").setValue(resetNewPasswordReset.getText().toString());
                                                                notifyDatabase.child("Updated Password").child(firstKey).child("userPassword").setValue(resetNewPasswordReset.getText().toString());
                                                                notifyDatabase.child("Updated Password").child(firstKey).child("userEmail").setValue(userEmail);

                                                                Toast.makeText(getContext(), "You can now login with your new password ", Toast.LENGTH_SHORT).show();
                                                                break;
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                Intent intent = new Intent(getContext(), LoginActivity.class);

                                                firebaseAuth.signOut();

                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
//                                }
//                            });
                        }
                        else{
                            if (!validatePass1) {
                                resetNewPasswordReset.setError("New Password length should be no less than 6");
                                resetNewPasswordReset.requestFocus();
                            }
                            else if(!validatePass2){
                                resetNewConfirmPasswordReset.setError("New Password length should be no less than 6");
                                resetNewConfirmPasswordReset.requestFocus();
                            }
                            else if(!validateBothPass){
                                resetNewConfirmPasswordReset.setError("Both the passwords must be same");
                                resetNewConfirmPasswordReset.requestFocus();
                            }
                            else {
                                Toast.makeText(getContext(), "Fill the correct data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "User Password is in valid", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(getContext(), "PLea5se check your internet connection", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public boolean validatePass1(String pass1){
        if (pass1.length() < 6  || pass1.equals("")) {
//            Toast.makeText(getContext(), "pass 1: " + pass1.length(), Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    public boolean validatePass2(String pass2){
        if (pass2.length() < 6 || pass2.equals("")) {
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
            return false;
        }
    }

    private void calculatePasswordStrength(String password) {
        // Now, we need to define a PasswordStrength enum
        // with a calculate static method returning the password strength
//        PasswordStrength passwordStrength = PasswordStrength.calculate(str);
//        password_strength.setText(passwordStrength.msg);
//        password_strength.setTextColor(passwordStrength.color);

//        ProgressBar progressBar = findViewById(R.id.resetProgressBar);
//        TextView strengthView = findViewById(R.id.reset_password_strength);

        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty()) {
            strengthView.setText("");
            progressBar.setProgress(0);
            return;
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        strengthView.setText(str.getText(getActivity()));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(getActivity()).equals("Weak")) {
            progressBar.setProgress(25);
        }
        else if (str.getText(getActivity()).equals("Medium")) {
            progressBar.setProgress(50);
        }
        else if (str.getText(getActivity()).equals("Strong")) {
            progressBar.setProgress(75);
        }
        else {
            progressBar.setProgress(100);
        }
    }
}
