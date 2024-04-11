package lk.calm.pasbaratheater01;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DateFirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    String movieTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_date_first, container, false);
        // Inflate the layout for this fragment

        if (getActivity() != null && getActivity() instanceof ProductViewActivity) {
            ProductViewActivity productView = (ProductViewActivity) getActivity();
            // Now you can access methods or variables in yourActivity
            movieTitle = productView.movieTitle;
        }

        rootView.findViewById(R.id.fragment1_TimeText1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DateFirstFragment.this.getContext(), SeatSelectionActivity.class);
                intent.putExtra("layoutNumber", "1.1");
                intent.putExtra("movieTitle", movieTitle);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.fragment1_TimeText2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DateFirstFragment.this.getContext(), SeatSelectionActivity.class);
                intent.putExtra("layoutNumber", "1.2");
                intent.putExtra("movieTitle", movieTitle);
                startActivity(intent);
            }
        });

        return rootView;
    }
}