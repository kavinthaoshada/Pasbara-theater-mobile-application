package lk.calm.pasbaradashboard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.calm.pasbaradashboard.entity.MovieEntity;
import lk.calm.pasbaradashboard.entity.ShowtimeEntity;

public class ManageMovieDetailActivity extends AppCompatActivity {

    public static final String TAG = ManageMovieDetailActivity.class.getName();
    private List<Object> selectedDates = new ArrayList<>();
    private List<String> selectedGenres = new ArrayList<>();
    TextView textview;
    ArrayList<String> arrayList;
    Dialog dialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Calendar selectedDateTime = Calendar.getInstance();
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialogShowtime;
    LinearLayout dateTimeList;

    private MaterialCardView selectPhoto;
    private ImageView carImageView;
    private Uri imageUri;
    private Bitmap bitmap;

    private String screen;

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_movie_detail);

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

        // FindViewByIds EditText
        EditText editTextTitle = findViewById(R.id.editTextTitle);
        EditText editTextCastAndCrew = findViewById(R.id.textCastAndCrew);
        EditText editTextDescription = findViewById(R.id.textDescription);
        EditText editTextTrailer = findViewById(R.id.textTrailer);
        EditText editTextPricing = findViewById(R.id.textPricing);

        Spinner spinner = findViewById(R.id.spinnerScreen);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.screen_dropdown_items,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedItem = parentView.getItemAtPosition(position).toString();
                setScreen(selectedItem);
                Toast.makeText(ManageMovieDetailActivity.this, "Selected: "+selectedItem,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do not anything
            }
        });


        // Searchable spinner
        textview=findViewById(R.id.testView);

        arrayList=new ArrayList<>();

        CollectionReference showtimeCollectionRef = db.collection("showtime");

        showtimeCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // Access data for each document
//                    String screen = document.getString("screen");
//                    List<String> showtimes = (List<String>) document.get("showtimes");

                    // Access data from the "movie" map
                    Map<String, Object> movieMap = (Map<String, Object>) document.get("movie");
                    if (movieMap != null) {
                        String title = (String) movieMap.get("title");
//                        List<String> genre = (List<String>) movieMap.get("genre");
                        arrayList.add(title);

//                        Log.d("ShowtimeData", "Screen: " + screen);
//                        Log.d("ShowtimeData", "Showtimes: " + showtimes.toString());
                        Log.d("ShowtimeData", "Movie Title: " + title);
//                        Log.d("ShowtimeData", "Movie Genre: " + genre.toString());
                    }
                }
            } else {
                Log.e("ShowtimeData", "Error getting documents.", task.getException());
            }
        });

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Searchable Spinner
                dialog=new Dialog(ManageMovieDetailActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(650,800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter=new ArrayAdapter<>(ManageMovieDetailActivity.this,
                        android.R.layout.simple_list_item_1,arrayList);

                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        textview.setText(adapter.getItem(position));
//                        textview.getText();
                        searchSelectedData(textview.getText().toString());

                        dialog.dismiss();
                    }
                });
            }
        });
        // Searchable spinner


        //load image
        selectPhoto = findViewById(R.id.selectCarPhoto);
        carImageView = findViewById(R.id.carImage);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStoragePermission();
                System.out.println("in image button");
            }
        });
        //load image

        //select date and time button
        datePickerDialogShowtime = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Show Time Picker Dialog after selecting the date
                    showTimePicker();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );

        // Set up the Time Picker Dialog
        timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    System.out.println("in time picker");
                    Log.i(TAG, "in Time picker");
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);

                    // storeDateTimeInFirestore(selectedDateTime.getTime());
                    updateUI(selectedDateTime.getTime());
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false
        );

        // Show Date Picker Dialog when the button is clicked
        Button datePickerButton = findViewById(R.id.dateAndTimePickerButton);
        datePickerButton.setOnClickListener(v -> datePickerDialogShowtime.show());

        dateTimeList = findViewById(R.id.dateTimeList);
        //select date and time button

        // update movie details
        findViewById(R.id.btnUpdateMovie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageRef.child("images/" + editTextTitle.getText().toString() + ".jpg");

                if(bitmap!=null) {
                    // Convert Bitmap to byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // Upload byte array to Firebase Storage
                    UploadTask uploadTask = imageRef.putBytes(imageData);

                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the download URL to Firestore
                            String imageUrl = uri.toString();
                            if (imageUrl == null) {
                                imageUrl = insImage;
                            }

                            if (editTextPricing.getText().toString() != null && editTextPricing.getText().toString() != ""
                                    && editTextDescription.getText().toString() != null && editTextDescription.getText().toString() != ""
                                    && selectedGenres != null && imageUrl != null && imageUrl != ""
                                    && editTextCastAndCrew.getText().toString() != null && editTextCastAndCrew.getText().toString() != ""
                                    && editTextTrailer.getText().toString() != null && editTextTrailer.getText().toString() != ""
                                    && selectedDates != null && getScreen() != null && getScreen() != ""
                                    && editTextPricing.getText().toString() != null && editTextPricing.getText().toString() != "") {
                                Log.i(TAG, "im in if");
                                int pricing = Integer.parseInt(editTextPricing.getText().toString());

                                Log.i(TAG, "title: " + editTextTitle.getText().toString() +
                                        " | castAndCrew: " + editTextCastAndCrew.getText().toString() +
                                        " | description: " + editTextDescription.getText().toString() +
                                        " | trailer: " + editTextTrailer.getText().toString() +
                                        " | price: " + editTextPricing.getText().toString());

                                updateMovieDataToFirestore(editTextTitle.getText().toString(), editTextDescription.getText().toString(),
                                        selectedGenres, imageUrl, editTextCastAndCrew.getText().toString(),
                                        editTextTrailer.getText().toString(), selectedDates, getScreen(), pricing);

                            }
                        });
                    }).addOnFailureListener(e -> {

                    });
                }else{
                    if (editTextPricing.getText().toString() != null && editTextPricing.getText().toString() != ""
                            && editTextDescription.getText().toString() != null && editTextDescription.getText().toString() != ""
                            && selectedGenres != null && insImage != null && insImage != ""
                            && editTextCastAndCrew.getText().toString() != null && editTextCastAndCrew.getText().toString() != ""
                            && editTextTrailer.getText().toString() != null && editTextTrailer.getText().toString() != ""
                            && selectedDates != null && getScreen() != null && getScreen() != ""
                            && editTextPricing.getText().toString() != null && editTextPricing.getText().toString() != "") {
                        Log.i(TAG, "im in if");
                        int pricing = Integer.parseInt(editTextPricing.getText().toString());

                        Log.i(TAG, "title: " + editTextTitle.getText().toString() +
                                " | castAndCrew: " + editTextCastAndCrew.getText().toString() +
                                " | description: " + editTextDescription.getText().toString() +
                                " | trailer: " + editTextTrailer.getText().toString() +
                                " | price: " + editTextPricing.getText().toString());

                        updateMovieDataToFirestore(editTextTitle.getText().toString(), editTextDescription.getText().toString(),
                                selectedGenres, insImage, editTextCastAndCrew.getText().toString(),
                                editTextTrailer.getText().toString(), selectedDates, getScreen(), pricing);

                    }
                }

                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });
        // update movie details

    }

    // Update movie detail
    private void updateMovieDataToFirestore(String title, String description, List<String> genre,
                                            String imageUrl, String castAndCrew, String trailer, List<Object> showTimes,
                                            String screen, int pricing) {

        CollectionReference showtimeCollectionRef = db.collection("showtime");

//        String documentId = insDocId;

        // Update data in Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("movie.title", title);
        data.put("movie.description", description);
        data.put("movie.genre", genre);
        data.put("movie.image", imageUrl);
        data.put("movie.cast_and_crew", castAndCrew);
        data.put("movie.trailer", trailer);
        data.put("showTimes", showTimes);
        data.put("screen", screen);
        data.put("pricing", pricing);

        showtimeCollectionRef.document(insDocId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating document", e);
                });

    }
    // Update movie detail

    //checkbox listener
    private final CheckBox.OnCheckedChangeListener onCheckedChangeListener = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Get the text of the checked CheckBox
            String genre = buttonView.getText().toString();

            // Add or remove the genre from the list based on the checked state
            if (isChecked) {
                selectedGenres.add(genre);
            } else {
                selectedGenres.remove(genre);
            }
        }
    };
    //checkbox listener

    //select date and time button
    private void showTimePicker() {
        timePickerDialog.show();
    }

    private void updateUI(Date selectedDateTime) {
        selectedDates.add(formatDate(selectedDateTime));
        // Create a new TextView to display the selected date and time
        TextView textView = new TextView(this);
        textView.setText(formatDate(selectedDateTime)); // You can format the date as needed
        textView.setTextSize(16); // Set the text size as needed

        // Add the TextView to the LinearLayout
        dateTimeList.addView(textView);
    }
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }
    //select date and time button

    String insDocId;
    String insImage;

    private void searchSelectedData(String movieTitle){
        CollectionReference showtimeCollectionRef = db.collection("showtime");

        showtimeCollectionRef
                .whereEqualTo("movie.title", movieTitle)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    insDocId = document.getId();
                    // Access data for each document
                    String screen = document.getString("screen");
                    List<String> showtimes = (List<String>) document.get("showTimes");
                    dateTimeList.removeAllViews();
                    if(showtimes!=null) {
                        for (String showtime : showtimes) {
                            selectedDates.add(showtime);
                            TextView textView = new TextView(this);
                            textView.setText(showtime);
                            textView.setTextSize(16);

                            dateTimeList.addView(textView);
                        }
                    }

                    Integer pricing = document.getLong("pricing").intValue();
                    EditText txtPricing = findViewById(R.id.textPricing);
                    txtPricing.setText(String.valueOf(pricing));

                    // Access data from the "movie" map
                    Map<String, Object> movieMap = (Map<String, Object>) document.get("movie");
                    if (movieMap != null) {
                        String title = (String) movieMap.get("title");
                        EditText txtTitle = findViewById(R.id.editTextTitle);
                        txtTitle.setText(title);

                        String castAndCrew = (String) movieMap.get("cast_and_crew");
                        EditText txtCastAndCrew = findViewById(R.id.textCastAndCrew);
                        txtCastAndCrew.setText(castAndCrew);

                        String description = (String) movieMap.get("description");
                        EditText txtDescription = findViewById(R.id.textDescription);
                        txtDescription.setText(description);

                        String image = (String) movieMap.get("image");
                        insImage = image;

                        String trailer = (String) movieMap.get("trailer");
                        EditText txtTrailer = findViewById(R.id.textTrailer);
                        txtTrailer.setText(trailer);

                        List<String> genre = (List<String>) movieMap.get("genre");
                        if (genre != null) {
                            for (String g : genre) {
                                switch (g) {
                                    case "Action":
                                        ((CheckBox) findViewById(R.id.checkboxAction)).setChecked(true);
                                        break;
                                    case "Adventure":
                                        ((CheckBox) findViewById(R.id.checkboxAdventure)).setChecked(true);
                                        break;
                                    case "Horror":
                                        ((CheckBox) findViewById(R.id.checkboxHorror)).setChecked(true);
                                        break;
                                    case "Thriller":
                                        ((CheckBox) findViewById(R.id.checkboxThriller)).setChecked(true);
                                        break;
                                    case "Drama":
                                        ((CheckBox) findViewById(R.id.checkboxDrama)).setChecked(true);
                                        break;
                                    case "Family":
                                        ((CheckBox) findViewById(R.id.checkboxFamily)).setChecked(true);
                                        break;
                                    case "Children":
                                        ((CheckBox) findViewById(R.id.checkboxChildren)).setChecked(true);
                                        break;
                                    case "Animation":
                                        ((CheckBox) findViewById(R.id.checkboxAnimation)).setChecked(true);
                                        break;
                                }
                            }
                        }

                        if (!arrayList.contains(title)) {
                            arrayList.add(title);
                        } else {
                            // value already exists
                            Log.d("ArrayListCheck", "Value already exists: " + title);
                        }

//                        Log.d("ShowtimeData", "Screen: " + screen);
//                        Log.d("ShowtimeData", "Showtimes: " + showtimes.toString());
                        Log.d("ShowtimeData", "Movie Title: " + title);
//                        Log.d("ShowtimeData", "Movie Genre: " + genre.toString());

                        if (image != null && !image.isEmpty()) {
                            // Assuming selectPhoto is your MaterialCardView
                            ImageView slectedImageView = findViewById(R.id.carImage);

                            RequestOptions requestOptions = new RequestOptions()
                                    .placeholder(R.drawable.ic_photo_camera_50)
                                    .error(R.drawable.ic_photo_camera_50);

                            Glide.with(ManageMovieDetailActivity.this)
                                    .load(image).apply(requestOptions)
                                    .into(slectedImageView);
                        }
                    }
                }
            } else {
                // Handle errors
                Log.e("ShowtimeData", "Error getting documents.", task.getException());
            }
        });
    }


    // image loding
    private void checkStoragePermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(ManageMovieDetailActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(ManageMovieDetailActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                pickImageFromGallery();
            }
        }else{
            pickImageFromGallery();
        }
    }
    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Log.i(TAG, "pickImageFromGallery");
        launcher.launch(intent);
    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if(data != null && data.getData() != null){
                        imageUri = data.getData();

                        try {
                            bitmap= MediaStore.Images.Media.getBitmap(
                                    ManageMovieDetailActivity.this.getContentResolver(),
                                    imageUri
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(imageUri!=null){
                        carImageView.setImageBitmap(bitmap);
                    }
                }
            }
    );
    // image loding
}