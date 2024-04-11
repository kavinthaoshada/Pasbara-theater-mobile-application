package lk.calm.pasbaradashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManageUsersActivity extends AppCompatActivity {

    public static final String TAG = ManageUsersActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("employees").document(mAuth.getCurrentUser().getUid().toString())
                .get().addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        String loggedEmployeeStatus = document.getString("status");
                        if(loggedEmployeeStatus==null){
                            onBackPressed();
                        }else if(loggedEmployeeStatus.equals("Employee")) {
                            onBackPressed();
                        }
                    }
                }).addOnFailureListener(e->{

                });

        ScrollView scrollViewMU = findViewById(R.id.scrollViewMU);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        CollectionReference userColRef = db.collection("employees");
        userColRef.get().addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                for(DocumentSnapshot document : task.getResult()){
                    String userName = document.getString("userName");
                    String image = document.getString("profileImage");
                    String status = document.getString("status");
                    String userPermission;

                    if(status==null){
                        userPermission = "user permission : Employee";
                    }else {
                        userPermission = "user permission : "+status;
                    }

                    View dynamicLayout = LayoutInflater.from(this).inflate(R.layout.user_item, null);
                    linearLayout.addView(dynamicLayout);

                    TextView textUserName = dynamicLayout.findViewById(R.id.textUserName);
                    textUserName.setText(userName);
                    TextView textDetail = dynamicLayout.findViewById(R.id.textDetail);
                    textDetail.setText(userPermission);

                    //user dialog
                    TextView textUserPermission = dynamicLayout.findViewById(R.id.textUserPermission);
                    if (textUserPermission != null) {
                        textUserPermission.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               UserDialogActivity userDialogActivity = new UserDialogActivity(
                                        ManageUsersActivity.this,
                                        document.getId().toString());
                                userDialogActivity.show();
                            }
                        });
                    }
                    //user dialog
                }
                scrollViewMU.removeAllViews();
                scrollViewMU.addView(linearLayout);
            }
        }).addOnFailureListener(e ->{
            Log.i(TAG, "");
        });

    }
}