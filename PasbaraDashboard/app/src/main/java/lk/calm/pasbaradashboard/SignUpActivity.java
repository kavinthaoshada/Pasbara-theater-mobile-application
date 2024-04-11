package lk.calm.pasbaradashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.calm.pasbaradashboard.entity.Employee;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = SignUpActivity.class.getName();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //data input for sign-up
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText username = findViewById(R.id.inputUsername);
        EditText email = findViewById(R.id.inputEmail);
        EditText contact = findViewById(R.id.inputContact);
        EditText password = findViewById(R.id.inputPassword);
        EditText confirm_password = findViewById(R.id.inputConfirmPassword);

        //create account button
        findViewById(R.id.btnCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, "password : "+password.getText().toString()+" | confirm_password : "+confirm_password.getText().toString());
            if(password.getText().toString().equals(confirm_password.getText().toString())){
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, task -> {

                            if (task.isSuccessful()) {
                                // Registration success
                                // Get the registered user and add additional information
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    Employee employee = new Employee(username.getText().toString(), email.getText().toString(),
                                            contact.getText().toString(), password.getText().toString(), false);
                                    // Save additional information to Firestore
                                    db.collection("employees")
                                            .document(firebaseUser.getUid())
                                            .set(employee)
                                            .addOnSuccessListener(documentReference -> {
                                                // Send email verification
                                                sendEmailVerification();
                                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> {
                                                // If storing information fails, display a message to the user.
                                                Toast.makeText(SignUpActivity.this,
                                                        "Failed to store user information.",
                                                        Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                // If registration fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "Registration failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        });
            }else{
                Toast.makeText(SignUpActivity.this, "Password doesn't match.", Toast.LENGTH_SHORT).show();
            }


            }
        });
        //create account button

        findViewById(R.id.imageBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.textSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Email sent
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                            // Update email verification status in Firestore
                            updateEmailVerificationStatus(user.getUid(), false);
                        } else {
                            // If sending fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateEmailVerificationStatus(String userId, boolean isEmailVerified) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("employees")
                .document(userId)
                .update("isEmailVerified", isEmailVerified)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated email verification status
                    Toast.makeText(SignUpActivity.this,
                            "Email verification status updated.",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failures in updating email verification status
                    Toast.makeText(SignUpActivity.this,
                            "Failed to update email verification status.",
                            Toast.LENGTH_SHORT).show();
                });
    }
}