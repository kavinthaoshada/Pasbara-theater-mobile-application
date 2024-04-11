package lk.calm.pasbaradashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BRUserDialogActivity extends Dialog {

    public static final String TAG = ManageBookingRecordsActivity.class.getName();
    private String uEmail;

    public BRUserDialogActivity(@NonNull Context context, String uEmail) {
        super(context);
        this.uEmail=uEmail;
        Log.i(TAG, "User Email : "+uEmail);
    }

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bruser_dialog);

        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", uEmail)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult()){
                            String user_name = document.getString("name");
                            String email = document.getString("email");

                            TextView userName = findViewById(R.id.textUserName);
                            userName.setText(user_name);
                            TextView userEmail = findViewById(R.id.textUserEmail);
                            userEmail.setText(email);
                        }
                    }
                }).addOnFailureListener(e ->{
                    Log.e(TAG, "Users loading failure");
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