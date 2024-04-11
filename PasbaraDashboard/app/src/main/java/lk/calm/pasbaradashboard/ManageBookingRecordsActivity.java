package lk.calm.pasbaradashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
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

import java.util.List;
import java.util.Map;

public class ManageBookingRecordsActivity extends AppCompatActivity {

    public static final String TAG = ManageBookingRecordsActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_booking_records);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ScrollView scrollViewBR = findViewById(R.id.scrollViewBR);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        CollectionReference bookingHistoryRef = db.collection("booking");
        bookingHistoryRef
                .get().addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult()){

                            String uEmail = document.getString("userEmail");
                            String timeStamp = document.getString("timeStamp");
                            String showTimeDocId = document.getString("showTimeDocId");
                            long tot_cost = document.getLong("total_cost");

                                db.collection("showtime").document(showTimeDocId)
                                        .get().addOnCompleteListener(tsk ->{
                                            if(tsk.isSuccessful()){
                                                DocumentSnapshot docmnt = tsk.getResult();
                                                Map<String, Object> movieMap = (Map<String, Object>) docmnt.get("movie");
                                                if (movieMap != null) {
                                                    String movieTitle = (String) movieMap.get("title");
                                                    String image = (String) movieMap.get("image");

                                                    List<Integer> seats = (List<Integer>) document.get("seatIdList");
                                                    Log.i(TAG, seats.size()+" seats");
                                                    int s = seats.size();
                                                    String detail = s+" seats | total cost : "+tot_cost+".00 LKR";

                                                    View dynamicLayout = LayoutInflater.from(this).inflate(R.layout.booking_record_item, null);
                                                    linearLayout.addView(dynamicLayout);

                                                    TextView textTitle = dynamicLayout.findViewById(R.id.textTitle);
                                                    textTitle.setText(movieTitle);
                                                    TextView textDetail = dynamicLayout.findViewById(R.id.textDetail);
                                                    textDetail.setText(detail);
                                                    TextView textTimeStamp = dynamicLayout.findViewById(R.id.textTimeStamp);
                                                    textTimeStamp.setText(timeStamp);


                                                    //View Access User Dialog
                                                    View accessUserView = dynamicLayout.findViewById(R.id.textAccessUser);
                                                    if(accessUserView!=null) {
                                                        accessUserView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                BRUserDialogActivity userDialogActivity = new BRUserDialogActivity(
                                                                        ManageBookingRecordsActivity.this,
                                                                        uEmail);
                                                                userDialogActivity.show();
                                                            }
                                                        });
                                                    }
                                                    //View Access User Dialog

                                                }
                                            }
                                        }).addOnFailureListener(e ->{
                                            Log.e(TAG, "Failure");
                                        });

                        }
                        scrollViewBR.removeAllViews();
                        scrollViewBR.addView(linearLayout);

                    }
                }).addOnFailureListener(e ->{
                    Log.e(TAG, "Fail to load booking details.");
                });
    }
}