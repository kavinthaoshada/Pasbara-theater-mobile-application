package lk.calm.pasbaratheater01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.calm.pasbaratheater01.entity.MovieFeedback;

public class ProductViewActivity extends AppCompatActivity implements SensorEventListener{

    public static final String TAG = ProductViewActivity.class.getName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private MyFragmentAdapter adapter;
    String movieTitle;
    private String globMovie;
    private FirebaseAuth mAuth;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        movieTitle = getIntent().getStringExtra("movieTitle");

        CollectionReference movieRef = db.collection("showtime");
        movieRef
                .whereEqualTo("movie.title", movieTitle)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult()){
                            String screen = document.getString("screen");
                            globMovie = document.getId();

                            Map<String, Object> movieMap = (Map<String, Object>) document.get("movie");
                            if (movieMap != null) {

                                RoundedImageView roundedImageView = findViewById(R.id.imageProfile);
                                String image = (String) movieMap.get("image");
                                RequestOptions requestOptions = new RequestOptions()
                                        .placeholder(R.drawable.ic_image_24)
                                        .error(R.drawable.ic_image_24);

                                Glide.with(ProductViewActivity.this)
                                        .load(image).apply(requestOptions)
                                        .into(roundedImageView);

                                String title = (String) movieMap.get("title");
                                TextView txtTitle = findViewById(R.id.textName);
                                txtTitle.setText(title);

                                List<String> genre = (List<String>) movieMap.get("genre");
                                String tags = "empty";
                                if (genre != null) {
                                    int i = 0;
                                    LinearLayout tagLinear = findViewById(R.id.layoutTags);
                                    tagLinear.removeAllViews();
                                    for (String g : genre) {

                                        TextView textView = createTagTextView(this, g);
                                        tagLinear.addView(textView);

                                       i++;
                                       if(i==1){
                                           tags=g;
                                       }else{
                                           tags+=" | "+g;
                                       }
                                    }
                                    TextView txtAbout = findViewById(R.id.textAbout);
                                    txtAbout.setText(tags);
                                }
                                String duration = (String) movieMap.get("duration");
                                TextView txtDuration = findViewById(R.id.textFollowers);
                                txtDuration.setText(duration);

                                String release_date = (String) movieMap.get("release_date");
                                TextView txt_release_date = findViewById(R.id.textFollowing);
                                txt_release_date.setText(release_date);

                                String trailer = (String) movieMap.get("trailer");

//                                https://www.youtube.com/embed/7NU-STboFeI?si=5JXgC28UlQMYeg8G
                                webView = findViewById(R.id.webView);
//                                String video = "<iframe width=\"100%\" height=\"100%\" src=\""+trailer+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
                                String video = "<html><head><style>body{margin:0;}iframe{width:100%;height:100%;}</style></head><body><div id=\"player\"></div><script src=\"https://www.youtube.com/iframe_api\"></script><script>var player; function onYouTubeIframeAPIReady() { player = new YT.Player('player', { height: '100%', width: '100%', videoId: '" + extractVideoId(trailer) + "', events: { 'onReady': onPlayerReady, 'onStateChange': onPlayerStateChange } }); } function onPlayerReady(event) { } function onPlayerStateChange(event) { if (event.data == YT.PlayerState.PLAYING && !event.target.isMuted()) { event.target.mute(); } } function playVideo() { player.playVideo(); } </script></body></html>";
                                webView.loadData(video, "text/html", "utf-8");
                                webView.getSettings().setJavaScriptEnabled(true);
                                webView.setWebChromeClient(new WebChromeClient());
                                webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                                webView.addJavascriptInterface(new JavaScriptInterface(), "Android");


                                if (accelerometer != null) {
                                    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                                    acceleration = 10f; // You can adjust this value based on your requirements
                                    currentAcceleration = SensorManager.GRAVITY_EARTH;
                                    lastAcceleration = SensorManager.GRAVITY_EARTH;
                                } else {
                                    // if device doesn't have an accelerometer
                                }

                            }

                            List<String> showtimes = (List<String>) document.get("showTimes");
                            List<String> times = new ArrayList<>();
                            if(showtimes!=null) {
                                for (String showtime : showtimes) {
                                    DateTimeFormatter inputFormatter = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                                        LocalDateTime dateTime = LocalDateTime.parse(showtime, inputFormatter);
                                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");
                                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                                        String date = dateTime.format(dateFormatter);
                                        String time = dateTime.format(timeFormatter);

                                        tabLayout = findViewById(R.id.tabLayout);
                                        viewPager2 = findViewById(R.id.viewPager2);

                                        tabLayout.addTab(tabLayout.newTab().setText(date));
//                                        tabLayout.addTab(tabLayout.newTab().setText("Nov 14"));
//                                        tabLayout.addTab(tabLayout.newTab().setText("Nov 15"));

                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        adapter = new MyFragmentAdapter(fragmentManager, getLifecycle());
                                        viewPager2.setAdapter(adapter);

                                        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                            @Override
                                            public void onTabSelected(TabLayout.Tab tab) {
                                                viewPager2.setCurrentItem(tab.getPosition());
                                            }

                                            @Override
                                            public void onTabUnselected(TabLayout.Tab tab) {

                                            }

                                            @Override
                                            public void onTabReselected(TabLayout.Tab tab) {

                                            }
                                        });

                                        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                            @Override
                                            public void onPageSelected(int position) {
                                                tabLayout.selectTab(tabLayout.getTabAt(position));
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error while getting data.");
                });

        EditText editTextComment = findViewById(R.id.editTextComment);

        findViewById(R.id.btnAddComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment(editTextComment.getText().toString(), mAuth.getCurrentUser().getUid(), globMovie);
            }
        });

        LinearLayout linearLayoutCommentShow = findViewById(R.id.linearLayoutCommentShow);
        CollectionReference commentRef = db.collection("movieFeedback");
        commentRef.get().addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                linearLayoutCommentShow.removeAllViews();
                for(DocumentSnapshot document : task.getResult()){

                    String comment = document.getString("textComment");
                    String uId = document.getString("userId");
                    db.collection("users").document(uId)
                            .get().addOnCompleteListener(t -> {
                                if(t.isSuccessful()){
                                    DocumentSnapshot doc = t.getResult();
                                    String uName = doc.getString("name");

                                    Log.i(TAG, "user name : "+uName);


                                    View dynamicLayout = LayoutInflater.from(this).inflate(R.layout.comment_show_container, null);
                                    linearLayoutCommentShow.addView(dynamicLayout);
                                    RoundedImageView imageProfile = dynamicLayout.findViewById(R.id.imageProfile);
                                    TextView textViewUserName = dynamicLayout.findViewById(R.id.textViewUserName);
                                    textViewUserName.setText(document.getString(uName));
                                    TextView textViewComment = dynamicLayout.findViewById(R.id.textViewComment);
                                    textViewComment.setText(comment);

                                }
                            });
                }
            }
        }).addOnFailureListener(e -> {

        });

    }


    private TextView createTagTextView(Context context, String tagText) {

        TextView textView = new TextView(context);

        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textView.setGravity(Gravity.CENTER);
        textView.setBackground(createTagBackgroundDrawable());
        textView.setIncludeFontPadding(false);
        textView.setPadding(
                (int) getResources().getDimension(R.dimen.tag_padding_start),
                (int) getResources().getDimension(R.dimen.tag_padding_top),
                (int) getResources().getDimension(R.dimen.tag_padding_end),
                0
        );
        textView.setTextColor(getResources().getColor(R.color.teal_200));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setText(tagText);

        return textView;
    }

    private Drawable createTagBackgroundDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke((int) getResources().getDimension(R.dimen.tag_stroke_width),
                getResources().getColor(R.color.teal_200));
        drawable.setCornerRadius(getResources().getDimension(R.dimen.tag_corner_radius));

        return drawable;
    }

    private  void addComment(String textComment, String userId, String showTimeId){
        MovieFeedback movieFeedback = new MovieFeedback(textComment, userId, showTimeId, getCurrentDateTime());

        db.collection("movieFeedback")
                .add(movieFeedback)
                .addOnSuccessListener(documentReference -> {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }).addOnFailureListener(e -> {

                });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date currentDate = new Date(System.currentTimeMillis());
        return sdf.format(currentDate);
    }

    //using accelorometer sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);

            float delta = currentAcceleration - lastAcceleration;
            acceleration = acceleration * 0.9f + delta;

            if (acceleration > 12) {
                webView.loadUrl("javascript:playVideo()");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do not anything
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private String extractVideoId(String youtubeUrl) {
        String videoId = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("https://www.youtube.com/")) {
            String expression = "(?:youtu\\.be\\/|watch\\?v=|\\/videos\\/|embed\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\/|youtu\\.be%2F|watch\\?v=|\\/e\\/|v\\/|watch\\?v%3D|watch?v=|\\/videos\\?action=embed\\/|\\/videos\\?v=|\\/watch\\?v=|https?:(?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:(?:[^\\s\\/]+\\/)+|(?:[^\\s]+\\/)?[^\\s]+)$))([a-zA-Z0-9_-]{11})";
            CharSequence input = youtubeUrl;
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(expression, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                videoId = matcher.group(1);
            }
        }
        return videoId;
    }

    private class JavaScriptInterface {
        @JavascriptInterface
        public void playVideo() {
            runOnUiThread(() -> {
                webView.loadUrl("javascript:playVideo()");
            });
        }
    }
    //using accelorometer sensor
}