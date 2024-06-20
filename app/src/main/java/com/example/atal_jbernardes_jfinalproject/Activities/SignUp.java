package com.example.atal_jbernardes_jfinalproject.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.atal_jbernardes_jfinalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText confirmPassword;
    Button signin;
    Button signup;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences appPreferences = getSharedPreferences("APP_THEME",
                Context.MODE_PRIVATE);
        boolean useDarkMode = appPreferences.getBoolean("DARK_MODE", false);

        Log.v("THEME_MODE_SIGNUP", ""+useDarkMode);
        if (useDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.nameField);
        password = findViewById(R.id.amountField);
        confirmPassword = findViewById(R.id.confirmPasswordField);
        signin = findViewById(R.id.addButton);
        signup = findViewById(R.id.cancelButton);

        setUpButtons();
        signUpEnable();
    }

    private void signUpEnable() {
        signup.setEnabled(false);
        // Enabling Sign Up Button Only When ALl Fields Are Full
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signup.setEnabled(areFieldsFull());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do Nothing
            }
        };

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);
    }

    private boolean isFieldFull(EditText textEdit) {
        return !textEdit.getText().toString().isEmpty();
    }

    private boolean areFieldsFull() {
        return (isFieldFull(username)) && (isFieldFull(password)) && (isFieldFull(confirmPassword));
    }

    private boolean matchingPasswords() {
        return password.getText().toString().equals(confirmPassword.getText().toString());
    }

    protected void setUpButtons() {
        signin.setOnClickListener(v -> finish());

        signup.setOnClickListener(v -> {


            if(!matchingPasswords()){
                Toast.makeText(SignUp.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                return;
            }

            if(password.getText().toString().length() < 6){
                Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            createAccount(username.getText().toString(), password.getText().toString());

//            Intent intent = new Intent(SignUp.this, SetBudget.class);
//            startActivity(intent);
//            finish();
        });
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> data = new HashMap<>();
                            data.put(mAuth.getCurrentUser().getUid(), email);
                            db.collection("Users").add(data);
                            sendUserHome();
                            Toast.makeText(SignUp.this, "Successfully Created User " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Unable to Sign Up User \n" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                });

        // [END create_user_with_email]
    }



    private void sendUserHome() {
        Intent intent = new Intent(SignUp.this, TabActivity.class);
        startActivity(intent);
        finish();
    }

}