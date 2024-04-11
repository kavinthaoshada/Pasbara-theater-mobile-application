package lk.calm.pasbaradashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;
    String employeeDocumentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        employeeDocumentId = getIntent().getStringExtra("employeeDocumentId");

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in
            Log.i(TAG, "User is signed in");
        } else {
            // No user is signed in
            Log.i(TAG, "No user is signed in");
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }

        //navigation menu
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.textMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        //navigation menu

        //navigation direction
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menuProfile) {
                    //
                    return true;
                } else if (id == R.id.menuLogin) {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menuProfile) {
                    Intent intent = new Intent(MainActivity.this, EmployeeProfileEntity.class);
                    startActivity(intent);
                    return true;
                }else if (id == R.id.menuSettings) {
                    Intent intent = new Intent(MainActivity.this, EmployeeProfileEntity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menuRegister) {
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menuLogout) {
                    mAuth.signOut();
                    Toast.makeText(MainActivity.this, "Sign-out Successful..", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MovieRecyclerViewActivity.this, MainActivity.class);
//                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

        CollectionReference employeesCollectionRef = db.collection("employees");
        DocumentReference specificEmployeeDocRef = employeesCollectionRef.document(currentUser.getUid());

        if (mAuth.getCurrentUser() != null) {
            boolean isEmailVerified = mAuth.getCurrentUser().isEmailVerified();
            if (isEmailVerified) {
                // User's email is verified
                Toast.makeText(MainActivity.this, "Email is verified", Toast.LENGTH_SHORT).show();

                Map<String, Object> data = new HashMap<>();
                data.put("emailVerified", true);;

                specificEmployeeDocRef
                        .update(data)
                        .addOnSuccessListener(aVoid -> {
                            Log.i(TAG, "Employee verification Updated!");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error updating document", e);
                        });
            } else {
                // User's email is not verified
                Toast.makeText(MainActivity.this, "Email is not verified", Toast.LENGTH_SHORT).show();
            }
        } else {
            // User is not logged in
        }

        specificEmployeeDocRef
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String userName = document.getString("userName");
                            TextView navUserName = findViewById(R.id.navUserName);
                            navUserName.setText(userName);

                            String loggedEmployeeStatus = document.getString("status");
                            if(loggedEmployeeStatus.equals("Blocked")) {
                                Toast.makeText(this, "Access permission denied.", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                            }

                            boolean isEmailVerified = document.getBoolean("emailVerified");
                            TextView verify = findViewById(R.id.navUserName);
                            if(isEmailVerified){
                                verify.setText("verified");
                                int teal200Color = getResources().getColor(R.color.teal_200);
                                verify.setTextColor(teal200Color);
                            }else{
                                verify.setText("not verified");
                                int primary = getResources().getColor(R.color.primary);
                                verify.setTextColor(primary);
                            }

                            Log.d(TAG, "isEmailVerified : " + isEmailVerified);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "Error getting documents.", task.getException());
                    }
                });

        //navigation direction

        findViewById(R.id.textManage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScheduleMovieActivity.class));
            }
        });

        findViewById(R.id.textManageMovieDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManageMovieDetailActivity.class));
            }
        });

        findViewById(R.id.textManageUsers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManageUsersActivity.class));
            }
        });

        findViewById(R.id.textManageBookingRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManageBookingRecordsActivity.class));
            }
        });

        findViewById(R.id.textManagePromotion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManagePromotionActivity.class));
            }
        });
    }
} 