package lk.calm.pasbaradashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.jahidhasanco.seatbookview.SeatBookView;
import dev.jahidhasanco.seatbookview.SeatClickListener;
import dev.jahidhasanco.seatbookview.SeatLongClickListener;
import lk.calm.pasbaradashboard.entity.SeatEntity;

public class SetSeatsAvailabilityActivity extends AppCompatActivity {

    public static final String TAG = SetSeatsAvailabilityActivity.class.getName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Integer> seatIdList;
    String documentId;

    String seats = "/_AAAAAAAAAAARRRR_/" +
            "_________________/" +
            "UU__AAAARRRRR__RR/" +
            "AU__UUUAAAAAA__AA/" +
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_seats_availability);

        documentId = getIntent().getStringExtra("documentId");
        Log.i(TAG, "Set seat availability : "+documentId);

        layOutUpdate();
//        Log.i(TAG, seatBookView);
//        System.out.println(seatBookView);
//        if (seatBookView == null) {
////            setSeatBookView(new SeatBookView(this));
//
//            setSeatBookView(findViewById(R.id.layoutSeat));
//        }

//        setSeatBookView(findViewById(R.id.layoutSeat));
        HorizontalScrollView hsv = findViewById(R.id.layoutSeatArchitecture);
        SeatBookView seatBookView = findViewById(R.id.layoutSeat);
        DocumentReference showtimeDocRef = db.collection("showtime").document(documentId);

        showtimeDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String layout = documentSnapshot.getString("layout");

                    hsv.removeView(seatBookView);
//                    seatBookView.removeView(getSeatBookView());
                    System.out.println("This is SeatBookView : "+seatBookView);
                    seatBookView.setSeatsLayoutString(layout)
                            .isCustomTitle(true)
                            .setCustomTitle(Arrays.asList(title))
                            .setSeatLayoutPadding(2)
                            .setSeatSizeBySeatsColumnAndLayoutWidth(8, -1);
                    seatBookView.show();

                    if(seatBookView!=null) {
                        hsv.addView(seatBookView);
                    }

                }
            }
        });

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

        findViewById(R.id.btnReserved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int seatId : seatIdList){
                    seatReplacer(seatId);
                }
                hsv.removeView(seatBookView);
                layOutUpdate();
                startActivity(new Intent(SetSeatsAvailabilityActivity.this, MainActivity.class));
            }
        });

    }
    public void layOutUpdate(){

        Map<String, Object> seatLayout = new HashMap<>();
        seatLayout.put("layout", seats);

        db.collection("showtime").document(documentId)
                .update(seatLayout)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

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
            seatsBuilder.setCharAt(indexToReplace, 'R');
            seats = seatsBuilder.toString();
        }

//        System.out.println(seats);
        Log.i(TAG, seats);
    }
}