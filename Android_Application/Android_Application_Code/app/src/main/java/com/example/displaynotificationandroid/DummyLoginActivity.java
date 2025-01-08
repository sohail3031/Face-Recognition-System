package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DummyLoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference notifyDatabase;

    String email, pass1, pass2, pass, value;

    private static final String TAG = "MyTag";

    TextView titleText;
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_login);

        editText = findViewById(R.id.editDummy);
        button = findViewById(R.id.buttonDummy);

        titleText = findViewById(R.id.dummyTitle);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        pass1 = extras.getString("password1");
        pass2 = extras.getString("password2");

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

                notifyDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                            String firstKey = dataSnapshot1.getKey();
                            Log.d(TAG, "onDataChange: first: " + firstKey);
                            String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                            Log.d(TAG, "onDataChange: second: " + databaseEmail);
                            if (email.equals(databaseEmail)) {
                                pass = String.valueOf(dataSnapshot1.child("userPassword").getValue());
                                Log.d(TAG, "onDataChange: pass: " + pass);

                                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(DummyLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(DummyLoginActivity.this, ResetNewPasswordActivity.class);

                                            intent.putExtra("email", email);
                                            intent.putExtra("password1", "");
                                            intent.putExtra("password2", "");

                                            Pair[] pairs = new Pair[1];
                                            pairs[0] = new Pair<View, String>(titleText, "logo_text");

                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DummyLoginActivity.this, pairs);
                                            startActivity(intent, options.toBundle());
                                            Log.d(TAG, "onComplete: Login Again Successfully");
//                                            Toast.makeText(DummyLoginActivity.this, "Updated", Toast.LENGTH_SHORT).show();

//                                            firebaseUser.updatePassword(editText.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(DummyLoginActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
//                                                        Log.d(TAG, "onComplete: password updated success");
//
//                                                        firebaseAuth.signOut();
//
//                                                        Intent intent = new Intent(DummyLoginActivity.this, PasswordResetSuccessActivity.class);
//
//                                                        Pair[] pairs = new Pair[1];
//                                                        pairs[0] = new Pair<View, String>(titleText, "logo_text");
//
//                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DummyLoginActivity.this, pairs);
//                                                        startActivity(intent, options.toBundle());
//                                                    }
//                                                    else{
//                                                        Toast.makeText(DummyLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                        Log.d(TAG, "onComplete: password updated fail");
//                                                    }
//                                                }
//                                            });
                                        }
                                        else{
                                            Log.d(TAG, "onComplete: failed");
                                            Toast.makeText(DummyLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//            }
//        });

        Log.d(TAG, "onCreate: email: " + email);
        Log.d(TAG, "onCreate: password: " + pass);
    }
}
