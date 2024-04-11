package lk.calm.pasbaratheater01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentReference;

import lk.calm.pasbaratheater01.entity.User;

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
        EditText name = findViewById(R.id.inputNameSignUp);
        EditText email = findViewById(R.id.inputEmailSignUp);
        EditText contact_number = findViewById(R.id.inputContactSignUp);
        EditText password = findViewById(R.id.inputPasswordSignUp);

        findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, task -> {

                            if (task.isSuccessful()) {
                                // Registration success
                                // Get the registered user and add additional information
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    User user = new User(name.getText().toString(), password.getText().toString(),
                                            email.getText().toString(), contact_number.getText().toString(), false);
                                    // Save additional information to Firestore
                                    db.collection("users")
                                            .document(firebaseUser.getUid())
                                            .set(user)
                                            .addOnSuccessListener(documentReference -> {
                                                // Send email verification
                                                sendEmailVerification();
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
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

            }
        });

        findViewById(R.id.textSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
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
        db.collection("users")
                .document(userId)
                .update("isEmailVerified", isEmailVerified)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated email verification status
                    Toast.makeText(SignUpActivity.this,
                            "Email verification status updated in Firestore.",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failures in updating email verification status
                    Toast.makeText(SignUpActivity.this,
                            "Failed to update email verification status in Firestore.",
                            Toast.LENGTH_SHORT).show();
                });
    }

}