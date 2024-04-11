package lk.calm.pasbaradashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import lk.calm.pasbaradashboard.entity.MovieEntity;

public class SelectMovieFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RadioGroup radioGroup;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_movie, container, false);
        // Inflate the layout for this fragment

        radioGroup = rootView.findViewById(R.id.radioGroup);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve data from Firestore
        firestore.collection("movies")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Clear existing radio buttons
                    radioGroup.removeAllViews();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MovieEntity movie = document.toObject(MovieEntity.class);
                        String dumyTitle = "fsfdg gsssaf hhsg";
//                        addRadioButton(movie.getTitle());
                        addRadioButton(dumyTitle);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });

        return rootView;
    }
    private void addRadioButton(String title) {
        RadioButton radioButton = new RadioButton(requireContext());
        radioButton.setText(title);
        radioButton.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        radioButton.setMarqueeRepeatLimit(-1);
        radioButton.setFocusable(true);
        radioButton.setFocusableInTouchMode(true);
        radioButton.setSingleLine(true);
        radioButton.setHorizontallyScrolling(true);
        radioButton.setPadding(0, 10, 0, 10);  // Adjust padding as needed

        // Add the radio button to the radio group
        radioGroup.addView(radioButton);
    }
}