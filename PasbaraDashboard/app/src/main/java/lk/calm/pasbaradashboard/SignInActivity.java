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

public class SignInActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //input email and password for sign-in
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText password = findViewById(R.id.inputPassword);
        EditText email = findViewById(R.id.inputEmail);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in
            Log.i(TAG, "User is signed in");
            getUserDetails(currentUser.getUid());
        } else {
            // No user is signed in
            Log.i(TAG, "No user is signed in");
        }

        findViewById(R.id.btnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignInActivity.this, task->{
                            if (task.isSuccessful()) {
                                // Authentication success
                                // You can get the user details from task.getResult().getUser()
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Retrieve additional user details from Firestore
                                    getUserDetails(user.getUid());
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        findViewById(R.id.textCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

    }

    private void getUserDetails(String userId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("employees")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        // User details found
                        String name = documentSnapshot.getString("userName");
                        String email = documentSnapshot.getString("email");
                        String contactNumber = documentSnapshot.getString("contact");

                        // Pass user details to the next activity (e.g., HomeActivity)
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.putExtra("userName", name);
                        intent.putExtra("email", email);
                        intent.putExtra("contact", contactNumber);
                        intent.putExtra("employeeDocumentId", userId);
                        startActivity(intent);
                        finish();
                    } else {
                        // User details not found
                        Toast.makeText(SignInActivity.this, "User details not found.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failures in fetching user details
                    Toast.makeText(SignInActivity.this, "Failed to fetch user details.",
                            Toast.LENGTH_SHORT).show();
                });

    }

}