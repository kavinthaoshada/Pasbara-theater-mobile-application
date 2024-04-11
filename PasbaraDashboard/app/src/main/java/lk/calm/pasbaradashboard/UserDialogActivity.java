package lk.calm.pasbaradashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserDialogActivity extends Dialog {

    public static final String TAG = ManageUsersActivity.class.getName();
    private String documentId;
    public UserDialogActivity(@NonNull Context context, String documentId) {
        super(context);
        this.documentId=documentId;
        Log.i(TAG, "Doc id : "+documentId);
    }

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dialog);

        db = FirebaseFirestore.getInstance();

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButton1 = findViewById(R.id.radioButton1);
        RadioButton radioButton2 = findViewById(R.id.radioButton2);
        RadioButton radioButton3 = findViewById(R.id.radioButton3);
        RadioButton radioButton4 = findViewById(R.id.radioButton4);
        radioButton4.setEnabled(false);
        db.collection("employees").document(documentId)
                        .get().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                String status = document.getString("status");
                                if(status==null){
                                   radioButton1.setChecked(true);
                                }else if(status.equals("Employee")){
                                    radioButton1.setChecked(true);
                                }else if(status.equals("Admin")){
                                    radioButton2.setChecked(true);
                                }else if(status.equals("Super Admin")){
                                    radioButton4.setChecked(true);
                                    radioButton1.setEnabled(false);
                                    radioButton2.setEnabled(false);
                                    radioButton3.setEnabled(false);
                                }else if(status.equals("Blocked")){
                                    radioButton3.setChecked(true);
                                }
                            }
                }).addOnFailureListener(e ->{

                });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);

                // Handle the selected radio button
                String selectedOption = radioButton.getText().toString();

                Map<String, Object> data = new HashMap<>();
                data.put("status", selectedOption);

                db.collection("employees").document(documentId)
                        .update(data).addOnSuccessListener(task ->{
                            Toast.makeText(UserDialogActivity.this.getContext(), "User permission changed: " + selectedOption,
                                    Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e ->{
                            Log.e(TAG, "Employee status update failure");
                        });

            }
        });

        Button closeButton = findViewById(R.id.btnClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}