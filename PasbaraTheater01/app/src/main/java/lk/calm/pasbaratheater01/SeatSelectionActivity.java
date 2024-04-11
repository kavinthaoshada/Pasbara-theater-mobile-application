package lk.calm.pasbaratheater01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dev.jahidhasanco.seatbookview.SeatBookView;
import dev.jahidhasanco.seatbookview.SeatClickListener;
import dev.jahidhasanco.seatbookview.SeatLongClickListener;
import lk.calm.pasbaratheater01.entity.Booking;
import lk.calm.pasbaratheater01.model.SeatLayout;

public class SeatSelectionActivity extends AppCompatActivity {
    public static final String TAG = SeatSelectionActivity.class.getName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String layoutFloor;
    SeatBookView seatBookView;
    List<Integer> seatIdList;

    String documentId;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public SeatBookView getSeatBookView() {
        return seatBookView;
    }

    public void setSeatBookView(SeatBookView seatBookView) {
        this.seatBookView = seatBookView;
    }

    public String getLayoutFloor() {
        return layoutFloor;
    }

    public void setLayoutFloor(String layoutFloor) {
        this.layoutFloor = layoutFloor;
    }

    String layoutNumber;
    private FirebaseAuth mAuth;
    String user_email = "empty";
    int pricing_per_seat;

    String seats = "/_UUUUUUAAAAARRRR_/" +
            "_________________/" +
            "UU__AAAARRRRR__RR/" +
            "UU__UUUAAAAAA__AA/" +
            "AA__AAAAAAAAA__AA/" +
            "AA__AARUUUURR__AA/" +
            "UU__UUUA_RRRR__AA/" +
            "AA__AAAA_RRAA__UU/" +
            "AA__AARR_UUUU__RR/" +
            "AA__UUAA_UURR__RR/" +
            "_________________/" +
            "UU_AAAAAAAUUUU_RR/" +
            "RR_AAAAAAAAAAA_AA/" +
            "AA_UUAAAAAUUUU_AA/" +
            "AA_AAAAAAUUUUU_AA/" +
            "_________________/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        String[] title = new String[]{
                "/", "", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A13", "A14", "A15", "",
                "/", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "/", "B1", "B2", "", "", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10", "B11", "", "", "B12", "B13",
                "/", "C1", "C2", "", "", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10", "C11", "", "", "C12", "C13",
                "/", "D1", "D2", "", "", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10", "D11", "", "", "D12", "D13",
                "/", "E1", "E2", "", "", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "E10", "E11", "", "", "E12", "E13",
                "/", "F1", "F2", "", "", "F3", "F4", "F5", "F6", "", "F7", "F8", "F9", "F10", "", "", "F11", "F12",
                "/", "G1", "G2", "", "", "G3", "G4", "G5", "G6", "", "G7", "G8", "G9", "G10", "", "", "G11", "G12",
                "/", "H1", "H2", "", "", "H3", "H4", "H5", "H6", "", "H7", "H8", "H9", "H10", "", "", "H11", "H12",
                "/", "I1", "I2", "", "", "I3", "I4", "I5", "I6", "", "I7", "I8", "I9", "I10", "", "", "I11", "I12",
                "/", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "/", "J1", "J2", "", "J3", "J4", "J5", "J6", "J7", "J8", "J9", "J10", "J11", "J12", "J13", "", "J14", "J15",
                "/", "K1", "K2", "", "K3", "K4", "K5", "K6", "K7", "K8", "K9", "K10", "K11", "K12", "K13", "", "K14", "K15",
                "/", "L1", "L2", "", "L3", "L4", "L5", "L6", "L7", "L8", "L9", "L10", "L11", "L12", "L13", "", "L14", "L15",
                "/", "M1", "M2", "", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11", "M12", "M13", "", "M14", "M15",
                "/", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""

        };

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser() != null) {
            CollectionReference userColRef = FirebaseFirestore.getInstance().collection("users");
            DocumentReference userDocRef = userColRef.document(mAuth.getCurrentUser().getUid());
            userDocRef
                    .get().addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            DocumentSnapshot doc = t.getResult();
                            if (doc.exists()) {
                                String userEmail = doc.getString("email");
                                user_email =userEmail;
                                Log.i(TAG, "user emai : "+user_email);
                            }
                        }
            });
        }

        layoutNumber = getIntent().getStringExtra("layoutNumber");
        String movieTitle = getIntent().getStringExtra("movieTitle");


        CollectionReference showtimesRef2 = FirebaseFirestore.getInstance().collection("showtime");
        showtimesRef2
                .whereEqualTo("movie.title", movieTitle)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String document_Id = document.getId();
                            setDocumentId(document_Id);

                            Integer pricing = document.getLong("pricing").intValue();
                            pricing_per_seat = pricing;

                            String layout = document.getString("layout");
                            setLayoutFloor(layout);

                            setSeatBookView(findViewById(R.id.layoutSeat));
                            getSeatBookView().setSeatsLayoutString(getLayoutFloor())
                                    .isCustomTitle(true)
                                    .setCustomTitle(Arrays.asList(title))
                                    .setSeatLayoutPadding(2)
                                    .setSeatSizeBySeatsColumnAndLayoutWidth(8, -1);

                            getSeatBookView().show();

                            seatBookView.setSeatClickListener(new SeatClickListener() {
                                @Override
                                public void onAvailableSeatClick(@NonNull List<Integer> selectedIdList, @NonNull View view) {
                                    seatIdList = selectedIdList;
                                }

                                @Override
                                public void onBookedSeatClick(@NonNull View view) {

                                }

                                @Override
                                public void onReservedSeatClick(@NonNull View view) {

                                }
                            });

                            seatBookView.setSeatLongClickListener(new SeatLongClickListener() {
                                @Override
                                public void onAvailableSeatLongClick(@NonNull View view) {

                                }

                                @Override
                                public void onBookedSeatLongClick(@NonNull View view) {

                                }

                                @Override
                                public void onReservedSeatLongClick(@NonNull View view) {

                                }
                            });

                        }
                    }
                });



        //get seatLayout with point
        CollectionReference showtimesRef = FirebaseFirestore.getInstance().collection("showtime");
        String targetLayoutNumber = "1.1";
        Query query = showtimesRef.whereArrayContainsAny("LayoutFloors.SeatLayouts", Collections.singletonList(targetLayoutNumber));

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    List<Map<String, Object>> seatLayouts = (List<Map<String, Object>>) document.get("LayoutFloors.SeatLayouts");
                    if (seatLayouts != null) {
                        Log.i(TAG, "im in successful 2.1");
                        for (Map<String, Object> seatLayout : seatLayouts) {
                            String layout_number = (String) seatLayout.get("layoutNumber");
                            if (layout_number != null && layout_number.equals(layoutNumber)) {

                                String layout_floor = (String) seatLayout.get("layout_floor");
                                setLayoutFloor(layout_floor);

                                HorizontalScrollView scrollView = findViewById(R.id.layoutSeatArchitecture);
                                scrollView.removeAllViews();

                                setSeatBookView(findViewById(R.id.layoutSeat));
                                getSeatBookView().setSeatsLayoutString(getLayoutFloor())
                                        .isCustomTitle(true)
                                        .setCustomTitle(Arrays.asList(title))
                                        .setSeatLayoutPadding(2)
                                        .setSeatSizeBySeatsColumnAndLayoutWidth(8, -1);
                                getSeatBookView().show();

                                scrollView.addView(getSeatBookView());

                                break;
                            }
                        }
                    }
                }
            } else {
                // Handle errors
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });



        findViewById(R.id.btnPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int seatId : seatIdList){
                    seatReplacer(seatId);
                }
                layOutUpdate();
                startActivity(new Intent(SeatSelectionActivity.this, MainActivity.class));
            }
        });
    }


    public void layOutUpdate(){

        List<SeatLayout> layoutFloors = new ArrayList<>();
        SeatLayout seatLayout = new SeatLayout(layoutNumber, getLayoutFloor());
        layoutFloors.add(seatLayout);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("layoutFloors", layoutFloors);

        db.collection("showtime").document(getDocumentId())
                .update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        saveBooking(user_email, getDocumentId(), layoutNumber, seatIdList,
                                bookingTotal(seatIdList, pricing_per_seat), bookingTotal(seatIdList, pricing_per_seat));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void seatReplacer(int seatNum){

        int capitalCount = 0;
        int indexToReplace = -1;

        for (int i = 0; i < seats.length(); i++) {
            char currentChar = seats.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                capitalCount++;

                if (capitalCount == seatNum) {
                    indexToReplace = i;
                    break;
                }
            }
        }

        if (indexToReplace != -1) {
            StringBuilder seatsBuilder = new StringBuilder(seats);
            seatsBuilder.setCharAt(indexToReplace, 'U');
            seats = seatsBuilder.toString();
        }

//        System.out.println(seats);
        Log.i(TAG, seats);
    }
    String bookingGlobId;
    private void saveBooking(String userEmail, String showTimeDocId,
                                          String layoutNumber, List<Integer> seatIdList,
                                          double total_cost, double payment) {
         Booking booking = new Booking(userEmail, showTimeDocId, layoutNumber, seatIdList, total_cost,
                 payment, 1, getCurrentDateTime());

        db.collection("booking")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();

                    bookingGlobId = documentId;

                    if(documentId!=null) {
//                        Intent intent = new Intent(SeatSelectionActivity.this, MovieRecyclerViewActivity.class);
////                        intent.putExtra("documentId", documentId);
//                        startActivity(intent);
                        startActivity(new Intent(SeatSelectionActivity.this, CheckoutActivity.class));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "Booking : fail");
                });

//        startActivity(new Intent(SeatSelectionActivity.this, CheckoutActivity.class));

    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date currentDate = new Date(System.currentTimeMillis());
        return sdf.format(currentDate);
    }

    private double bookingTotal(List<Integer> seatIdList, int pricing_per_seat){
        double tot = seatIdList.size()*pricing_per_seat;
        return tot;
    }

}