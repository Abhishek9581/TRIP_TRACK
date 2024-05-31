package com.example.newfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    EditText name;
    EditText email;
    EditText password;
    EditText confirmPassword;
    TextView login;
    Button signup;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.editTextUsername);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);
        login = findViewById(R.id.textViewLogin);
        signup = findViewById(R.id.buttonRegister);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignup();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle login redirection
            }
        });
    }

    private void handleSignup() {
        String fullName = name.getText().toString();
        String emailInput = email.getText().toString();
        String passwordInput = password.getText().toString();
        String confirmPasswordInput = confirmPassword.getText().toString();

        if (fullName.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            Toast.makeText(Signup.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordInput.equals(confirmPasswordInput)) {
            Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        signup.setEnabled(false);

        createAccount(fullName, emailInput, passwordInput);
    }

    private void createAccount(String fullName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            writeNewUser(user.getUid(), fullName, email);
                        }
                        Toast.makeText(Signup.this, "Signup successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup.this, login.class);
                        intent.putExtra("userName", fullName);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Signup.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        signup.setEnabled(true);
                    }
                });
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "User data saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Signup.this, "Failed to save user data: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class User {
        public String name;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}

