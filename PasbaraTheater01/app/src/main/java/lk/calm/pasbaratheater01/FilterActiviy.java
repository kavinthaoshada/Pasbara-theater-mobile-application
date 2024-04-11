package lk.calm.pasbaratheater01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterActiviy extends AppCompatActivity implements MovieListener {

    public static final String TAG = FilterActiviy.class.getName();
    private List<String> selectedGenres = new ArrayList<>();
    private Button buttonGoToBooking;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    LinearLayout linearLayoutFilterContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_activiy);

        buttonGoToBooking = findViewById(R.id.buttonGoToBooking);

        // FindViewById CheckBoxes
        CheckBox checkBoxAction = findViewById(R.id.checkboxAction);
        CheckBox checkBoxAdventure = findViewById(R.id.checkboxAdventure);
        CheckBox checkBoxHorror = findViewById(R.id.checkboxHorror);
        CheckBox checkBoxThriller = findViewById(R.id.checkboxThriller);
        CheckBox checkBoxDrama = findViewById(R.id.checkboxDrama);
        CheckBox checkBoxFamily = findViewById(R.id.checkboxFamily);
        CheckBox checkBoxChildren = findViewById(R.id.checkboxChildren);
        CheckBox checkBoxAnimation = findViewById(R.id.checkboxAnimation);

        // Set up OnCheckedChangeListener for each CheckBox
        checkBoxAction.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxAdventure.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxHorror.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxThriller.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxDrama.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxFamily.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxChildren.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxAnimation.setOnCheckedChangeListener(onCheckedChangeListener);

        defaultDataLoading();


    }

    //checkbox listener
    private final CheckBox.OnCheckedChangeListener onCheckedChangeListener = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Get the text of the checked CheckBox
            String genre = buttonView.getText().toString();

            // Add or remove the genre from the list based on the checked state
            if (isChecked) {
                selectedGenres.add(genre);
                executeQuery();
            } else {
                selectedGenres.remove(genre);
                executeQuery();
            }
        }
    };
    //checkbox listener

    //default data
    private void defaultDataLoading(){

        RecyclerView tvShowsRecyclerView = findViewById(R.id.tvShowsRecyclerView);
        List<Movie> tvShows = new ArrayList<>();

        final MoviesAdapter moviesAdapter = new MoviesAdapter(tvShows, FilterActiviy.this);
        tvShowsRecyclerView.setAdapter(moviesAdapter);

        //search fom firestore
        CollectionReference showtimeCollectionRef = db.collection("showtime");

        showtimeCollectionRef
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {

                            Map<String, Object> movieMap = (Map<String, Object>) document.get("movie");
                            if (movieMap != null) {
                                String image = (String) movieMap.get("image");
                                String title = (String) movieMap.get("title");
                                String castAndCrew = (String) movieMap.get("cast_and_crew");
                                String description = (String) movieMap.get("description");

                                Log.i(TAG, title+", "+description);

                                Movie the100 = new Movie();
                                the100.setImageUrl(image);
                                the100.name = title;
                                the100.rating = 5f;
                                the100.createdBy = castAndCrew;
                                the100.story = description;
                                tvShows.add(the100);
                            }
                        }
                        if (moviesAdapter != null) {
                            moviesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Handle errors
                        Log.e("ShowtimeData", "Error getting documents.", task.getException());
                    }
                });
        //search fom firestore

        buttonGoToBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Movie> selectedTvShows = moviesAdapter.getSelectedTvShows();
                StringBuilder tvShowsName = new StringBuilder();
                for(int i=0;i<selectedTvShows.size();i++){
                    if(i==0){
                        tvShowsName.append(selectedTvShows.get(i).name);

                        Log.i(TAG, "Movie name : "+selectedTvShows.get(i).name);

                        Intent intent = new Intent(FilterActiviy.this, ProductViewActivity.class);
                        intent.putExtra("movieTitle", selectedTvShows.get(i).name);
                        startActivity(intent);
                        finish();
                    }else{
                        tvShowsName.append("\n").append(selectedTvShows.get(i).name);
                    }
                }
//                Toast.makeText(MovieRecyclerViewActivity.this, tvShowsName.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    //default data

    //search filter data

    private void executeQuery() {
        RecyclerView tvShowsRecyclerView = findViewById(R.id.tvShowsRecyclerView);

        List<Movie> tvShows = new ArrayList<>();
        final MoviesAdapter moviesAdapter = new MoviesAdapter(tvShows, FilterActiviy.this);

        tvShowsRecyclerView.setAdapter(moviesAdapter);

        CollectionReference showtimeRef = db.collection("showtime");

        Query query = showtimeRef;

        if (!selectedGenres.isEmpty()) {
            List<String> genres = new ArrayList<>(selectedGenres);

            query = query.whereArrayContainsAny("movie.genre", genres);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();

                for (QueryDocumentSnapshot document : querySnapshot) {
                    Map<String, Object> movieMap = (Map<String, Object>) document.get("movie");
//                    Log.d(TAG, "Movie Data: " + movieMap);

                    if (movieMap != null) {

                        String image = (String) movieMap.get("image");
                        String title = (String) movieMap.get("title");
                        String castAndCrew = (String) movieMap.get("cast_and_crew");
                        String description = (String) movieMap.get("description");

                        Log.d(TAG, "Movie Data: " + movieMap.get("title"));
                        Log.i(TAG, "execute query : "+title+", "+description);

                        tvShowsRecyclerView.removeAllViews();
                        Movie the100 = new Movie();
                        the100.setImageUrl(image);
                        the100.name = title;
                        the100.rating = 5f;
                        the100.createdBy = castAndCrew;
                        the100.story = description;
                        tvShows.add(the100);

                        //remove and adding dinamic layout
                    }
                }
                if (moviesAdapter != null) {
                    moviesAdapter.notifyDataSetChanged();
                }
            } else {
                Exception e = task.getException();
                Log.e(TAG, "Error getting documents: ", e);
            }
        });

        buttonGoToBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Movie> selectedTvShows = moviesAdapter.getSelectedTvShows();
                StringBuilder tvShowsName = new StringBuilder();
                for(int i=0;i<selectedTvShows.size();i++){
                    if(i==0){
                        tvShowsName.append(selectedTvShows.get(i).name);

                        Log.i(TAG, "Movie name : "+selectedTvShows.get(i).name);

                        Intent intent = new Intent(FilterActiviy.this, ProductViewActivity.class);
                        intent.putExtra("movieTitle", selectedTvShows.get(i).name);
                        startActivity(intent);
                        finish();
                    }else{
                        tvShowsName.append("\n").append(selectedTvShows.get(i).name);
                    }
                }
//                Toast.makeText(MovieRecyclerViewActivity.this, tvShowsName.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    //search filter data

    @Override
    public void onTvShowAction(Boolean isSelected, int selectedCount) {
        if(isSelected){
            if(selectedCount == 1){
                findViewById(R.id.buttonGoToBooking).setVisibility(View.VISIBLE);
            }else {
                findViewById(R.id.buttonGoToBooking).setVisibility(View.GONE);
            }
        }else{
            findViewById(R.id.buttonGoToBooking).setVisibility(View.GONE);
        }
    }

}