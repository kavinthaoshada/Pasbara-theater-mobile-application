package lk.calm.pasbaradashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.calm.pasbaradashboard.entity.PromotionEntity;

public class ManagePromotionActivity extends AppCompatActivity {

    public static final String TAG = ManagePromotionActivity.class.getName();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_promotion);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView textViewName = findViewById(R.id.editTextPromotionName);
        TextView textViewDiscountAmount = findViewById(R.id.editTextDiscountAmount);
        TextView textViewCondition = findViewById(R.id.editTextCondition);
        TextView textViewValidPeriod = findViewById(R.id.editTextValidPeriod);

        findViewById(R.id.btnAddDiscount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textViewName.getText().toString() == null){
                    Toast.makeText(ManagePromotionActivity.this, "Promotion name cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }else if(textViewDiscountAmount.getText().toString() == null){
                    Toast.makeText(ManagePromotionActivity.this, "Discount cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }else if(textViewCondition.getText().toString() == null){
                    Toast.makeText(ManagePromotionActivity.this, "Condition cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }else if(textViewValidPeriod.getText().toString() == null){
                    Toast.makeText(ManagePromotionActivity.this, "Valid period cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }else{
                    PromotionEntity promotionEntity = new PromotionEntity(textViewName.getText().toString(),
                            Integer.parseInt(textViewDiscountAmount.getText().toString()),Integer.parseInt(textViewCondition.getText().toString()),
                            Integer.parseInt(textViewValidPeriod.getText().toString()), getCurrentDateTime());

                    db.collection("promotion")
                            .add(promotionEntity)
                            .addOnSuccessListener(task ->{
                                Toast.makeText(ManagePromotionActivity.this, "Discount added successful.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }).addOnFailureListener(e ->{
                                Log.e(TAG, "Promotion adding failure");
                            });
                }
            }
        });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date currentDate = new Date(System.currentTimeMillis());
        return sdf.format(currentDate);
    }
}