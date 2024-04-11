package lk.calm.pasbaratheater01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lk.calm.pasbaratheater01.model.BookHistory;
import lk.calm.pasbaratheater01.service.QRCodeGeneratorService;

public class BookingHistoryActivity extends AppCompatActivity {

    public static final String TAG = BookingHistoryActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String user_email = "empty";
    private ImageView qrCodeImageView;

//    LinearLayout linearLayoutBookingHistoryContainer;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

//        linearLayoutBookingHistoryContainer = findViewById(R.id.linearLayoutBookingHistoryContainer);

        ScrollView scrollViewBH = findViewById(R.id.scrollViewBH);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        CollectionReference bookingHistoryRef = db.collection("booking");


        if (mAuth.getCurrentUser() != null) {
            CollectionReference userColRef = FirebaseFirestore.getInstance().collection("users");
            DocumentReference userDocRef = userColRef.document(mAuth.getCurrentUser().getUid());
            userDocRef
                    .get().addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            DocumentSnapshot doc = t.getResult();
                            if (doc.exists()) {
                                Log.i(TAG, "user email prev : " + user_email);
                                String userEmail = doc.getString("email");
                                user_email = userEmail;
                                Log.i(TAG, "user email : " + user_email);

                                bookingHistoryRef
                                        .get().addOnCompleteListener(task ->{
                                            if(task.isSuccessful()){
                                                TextView bookingNum = findViewById(R.id.textTotBooking);
                                                bookingNum.setText(task.getResult().size()+"");
                                                Log.i(TAG, "Booking : "+task.getResult().size());
                                                for(DocumentSnapshot document : task.getResult()){

                                                    Log.i(TAG, "doc id : "+document.getId().toString());

                                                    String uEmail = document.getString("userEmail");
                                                    String timeStamp = document.getString("timeStamp");
                                                    String showTimeDocId = document.getString("showTimeDocId");
                                                    long tot_cost = document.getLong("total_cost");
                                                    if(uEmail.equals(user_email)){

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

                                                                            View dynamicLayout = LayoutInflater.from(this).inflate(R.layout.booking_history_item, null);
                                                                            linearLayout.addView(dynamicLayout);

//                                                                            linearLayoutBookingHistoryContainer.removeAllViews();
//                                                                            View dynamicLayout = LayoutInflater.from(this).inflate(R.layout.booking_history_item, null);
//                                                                            linearLayoutBookingHistoryContainer.addView(dynamicLayout);

                                                                            TextView textTitle = dynamicLayout.findViewById(R.id.textTitle);
                                                                            textTitle.setText(movieTitle);
                                                                            TextView textDetail = dynamicLayout.findViewById(R.id.textDetail);
                                                                            textDetail.setText(detail);
                                                                            TextView textTimeStamp = dynamicLayout.findViewById(R.id.textTimeStamp);
                                                                            textTimeStamp.setText(timeStamp);


                                                                            //View QR code
                                                                            View qrCodeView = dynamicLayout.findViewById(R.id.textQR);
                                                                            if(qrCodeView!=null) {
                                                                                qrCodeView.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View view) {

                                                                                        Log.i(TAG, "doc id in view btn : "+document.getId().toString());
                                                                                        getBookingDocumentId(document.getId().toString(), documentId -> {
                                                                                            if (documentId != null) {
                                                                                                Bitmap qrCodeBitmap = QRCodeGeneratorService.generateQRCode(documentId);

                                                                                                QRDialogActivity qrDialogActivity = new QRDialogActivity(BookingHistoryActivity.this, qrCodeBitmap);
                                                                                                qrDialogActivity.show();

                                                                                                //                          qrCodeImageView.setImageBitmap(qrCodeBitmap);
                                                                                            } else {
                                                                                                Log.i(TAG, "documentID is null in QR");
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                            //View QR code

                                                                        }
                                                                    }
                                                                }).addOnFailureListener(e ->{
                                                                    Log.e(TAG, "Failure");
                                                                });

                                                    }
                                                }
                                                scrollViewBH.removeAllViews();
                                                scrollViewBH.addView(linearLayout);

                                            }
                                        }).addOnFailureListener(e ->{
                                            Log.e(TAG, "Fail to load booking details.");
                                        });

                            }
                        }
                    });



        }

        findViewById(R.id.imgViewLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookingHistoryActivity.this, MapActivity.class));
            }
        });

    }

    public void getBookingDocumentId(String bookingId, final FirestoreCallback callback) {
        DocumentReference bookingRef = db.collection("booking").document(bookingId);
        bookingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    callback.onCallback(document.getId());
                } else {
                    callback.onCallback(null);
                }
            } else {
                callback.onCallback(null);
            }
        });
    }

    public interface FirestoreCallback {
        void onCallback(String documentId);
    }



}