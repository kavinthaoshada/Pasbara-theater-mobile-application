package lk.calm.pasbaratheater01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(MainActivity.this, task->{
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Retrieve additional user details from Firestore
                                    getUserDetails(user.getUid());
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });



        findViewById(R.id.textSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUserDetails(String userId) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // User details found
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String contactNumber = documentSnapshot.getString("contact_number");

                            // Pass user details to the next activity (e.g., HomeActivity)
                            Intent intent = new Intent(MainActivity.this, MovieRecyclerViewActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email);
                            intent.putExtra("contact_number", contactNumber);
                            startActivity(intent);
                            finish();
                        } else {
                            // User details not found
                            Toast.makeText(MainActivity.this, "User details not found.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failures in fetching user details
                        Toast.makeText(MainActivity.this, "Failed to fetch user details.",
                                Toast.LENGTH_SHORT).show();
                    });

    }

    }