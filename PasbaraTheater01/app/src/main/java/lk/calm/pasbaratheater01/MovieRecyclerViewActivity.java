package lk.calm.pasbaratheater01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lk.calm.pasbaratheater01.service.ConnectivityCheckerService;

public class MovieRecyclerViewActivity extends AppCompatActivity implements MovieListener {

    public static final String TAG = MovieRecyclerViewActivity.class.getName();
    private Button buttonGoToBooking;
    private FirebaseAuth mAuth;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int INTERVAL = 5000;
    private NoInternetActivity noInternetActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_recycler_view);

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("new_user_forums");

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        checkConnectivityPeriodically();

        //navigation menu
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        //navigation menu

        //navigation direction
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menuProfile) {
                    //
                    return true;
                } else if (id == R.id.menuLogin) {
                    Intent intent = new Intent(MovieRecyclerViewActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menuRegister) {
                    Intent intent = new Intent(MovieRecyclerViewActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    return true;
                }else if (id == R.id.menuHistory) {
                    Intent intent = new Intent(MovieRecyclerViewActivity.this, BookingHistoryActivity.class);
                    startActivity(intent);
                    return true;
                }else if (id == R.id.menuProfile) {
                    Log.i(TAG, "IN PROFIE..");
                    Toast.makeText(MovieRecyclerViewActivity.this, "In Profile..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MovieRecyclerViewActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    return true;
                }else if (id == R.id.menuSettings) {
                    Log.i(TAG, "IN PROFIE..");
                    Toast.makeText(MovieRecyclerViewActivity.this, "In Profile..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MovieRecyclerViewActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menuLogout) {
                    mAuth.signOut();
                    Toast.makeText(MovieRecyclerViewActivity.this, "Sign-out Successful..", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MovieRecyclerViewActivity.this, MainActivity.class);
//                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

        //navigation direction

        //search button
        findViewById(R.id.searchButtonRecyclerView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MovieRecyclerViewActivity.this, FilterActiviy.class));
            }
        });
        //search button



        RecyclerView tvShowsRecyclerView = findViewById(R.id.tvShowsRecyclerView);
        buttonGoToBooking = findViewById(R.id.buttonGoToBooking);

        List<Movie> tvShows = new ArrayList<>();

        final MoviesAdapter moviesAdapter = new MoviesAdapter(tvShows, this);
        tvShowsRecyclerView.setAdapter(moviesAdapter);

        //search from firestore
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
        //search from firestore

//        Movie the100 = new Movie();
//        the100.image = R.drawable.ambulance;
//        the100.name = "Ambulance";
//        the100.rating = 5f;
//        the100.createdBy = "Jason Stathem";
//        the100.story = "Needing money to cover his wife's medical bills, a decorated veteran teams up with his adoptive brother to steal $32 million from a Los Angeles bank. However, when their getaway goes spectacularly wrong, the desperate thieves hijack an ambulance that's carrying a severely wounded cop and an EMT worker. Caught in a high-speed chase, the two siblings must figure out a way to outrun the law while keeping their hostages alive.";
//        tvShows.add(the100);

//        final MoviesAdapter moviesAdapter = new MoviesAdapter(tvShows, this);
//        tvShowsRecyclerView.setAdapter(moviesAdapter);

        buttonGoToBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Movie> selectedTvShows = moviesAdapter.getSelectedTvShows();
                StringBuilder tvShowsName = new StringBuilder();
                for(int i=0;i<selectedTvShows.size();i++){
                    if(i==0){
                        tvShowsName.append(selectedTvShows.get(i).name);

                        Log.i(TAG, "Movie name : "+selectedTvShows.get(i).name);

                        Intent intent = new Intent(MovieRecyclerViewActivity.this, ProductViewActivity.class);
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

    private void checkConnectivityPeriodically() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ConnectivityCheckerService.isConnected(MovieRecyclerViewActivity.this)) {

//                    Toast.makeText(MovieRecyclerViewActivity.this, "Internet is available", Toast.LENGTH_SHORT).show();
                    if (noInternetActivity != null && !noInternetActivity.isFinishing()) {

                        noInternetActivity.finish();
                        noInternetActivity.onBackPressed();
                    }

                } else {
//                    Toast.makeText(MovieRecyclerViewActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    if (noInternetActivity == null || noInternetActivity.isFinishing()) {

                        noInternetActivity = new NoInternetActivity();
                        startActivity(new Intent(MovieRecyclerViewActivity.this, NoInternetActivity.class));
                    }
                }

                handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private boolean isNoInternetActivityRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String className = NoInternetActivity.class.getName();

        if (activityManager != null) {
            ActivityManager.RunningTaskInfo info = activityManager.getRunningTasks(1).get(0);

            if (info.topActivity != null && info.topActivity.getClassName().equals(className)) {
                return true;
            }
        }

        return false;
    }

}