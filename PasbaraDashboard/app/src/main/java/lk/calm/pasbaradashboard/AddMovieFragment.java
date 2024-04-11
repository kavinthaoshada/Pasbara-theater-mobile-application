package lk.calm.pasbaradashboard;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.calm.pasbaradashboard.entity.MovieEntity;
import lk.calm.pasbaradashboard.entity.ShowtimeEntity;

public class AddMovieFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = AddMovieFragment.class.getName();
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private MaterialCardView selectPhoto;
    private ImageView carImageView;
    private Uri imageUri;
    private Bitmap bitmap;
    private List<String> selectedGenres = new ArrayList<>();
    private List<Object> selectedDates = new ArrayList<>();
    Calendar selectedDateTime = Calendar.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    CollectionReference dateCollection = db.collection("your-collection");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initDatePicker();

    }

    String releaseDate;


    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialogShowtime;
    LinearLayout dateTimeList;

    private String screen;

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_movie, container, false);
        dateButton = rootView.findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

        selectPhoto = rootView.findViewById(R.id.selectCarPhoto);
        carImageView=rootView.findViewById(R.id.carImage);

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStoragePermission();
            }
        });

        // FindViewById CheckBoxes
        CheckBox checkBoxAction = rootView.findViewById(R.id.checkboxAction);
        CheckBox checkBoxAdventure = rootView.findViewById(R.id.checkboxAdventure);
        CheckBox checkBoxHorror = rootView.findViewById(R.id.checkboxHorror);
        CheckBox checkBoxThriller = rootView.findViewById(R.id.checkboxThriller);
        CheckBox checkBoxDrama = rootView.findViewById(R.id.checkboxDrama);
        CheckBox checkBoxOption1 = rootView.findViewById(R.id.checkboxOption1);
        CheckBox checkBoxChildren = rootView.findViewById(R.id.checkboxChildren);
        CheckBox checkBoxAnimation = rootView.findViewById(R.id.checkboxAnimation);

        // Set up OnCheckedChangeListener for each CheckBox
        checkBoxAction.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxAdventure.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxHorror.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxThriller.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxDrama.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxOption1.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxChildren.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxAnimation.setOnCheckedChangeListener(onCheckedChangeListener);

        // FindViewByIds EditText
        EditText editTextTitle = rootView.findViewById(R.id.textTitle);
        EditText editTextHours = rootView.findViewById(R.id.textHours);
        EditText editTextMinutes = rootView.findViewById(R.id.textMinutes);
        EditText editTextCastAndCrew = rootView.findViewById(R.id.textCastAndCrew);
        EditText editTextDescription = rootView.findViewById(R.id.textDescription);
        EditText editTextTrailer = rootView.findViewById(R.id.textTrailer);
        EditText editTextPricing = rootView.findViewById(R.id.textPricing);

        Spinner spinner = rootView.findViewById(R.id.spinnerScreen);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this.getContext(),
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
//                Toast.makeText(this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                Toast.makeText(AddMovieFragment.this.getContext(), "Selected: "+selectedItem,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do not anything
            }
        });

        rootView.findViewById(R.id.btnAddMovie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageRef.child("images/" + editTextTitle.getText().toString() + ".jpg");

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

                        if(editTextPricing.getText().toString()!=null&&editTextPricing.getText().toString()!=""
                                &&editTextDescription.getText().toString()!=null&&editTextDescription.getText().toString()!=""
                                &&selectedGenres!=null&&releaseDate!=null&&releaseDate!="" &&imageUrl!=null&&imageUrl!=""
                                &&editTextCastAndCrew.getText().toString()!=null&&editTextCastAndCrew.getText().toString()!=""
                                &&editTextTrailer.getText().toString()!=null&&editTextTrailer.getText().toString()!=""
                                &&selectedDates!=null&&getScreen()!=null &&getScreen()!=""
                                &&editTextPricing.getText().toString()!=null&&editTextPricing.getText().toString()!="") {
                            Log.i(TAG, "im in if");
                            int pricing = Integer.parseInt(editTextPricing.getText().toString());

                            Log.i(TAG, "title: " + editTextTitle.getText().toString() +
                                    " | hours: " + editTextHours.getText().toString() +
                                    " | minutes: " + editTextMinutes.getText().toString() +
                                    " | castAndCrew: " + editTextCastAndCrew.getText().toString() +
                                    " | description: " + editTextDescription.getText().toString() +
                                    " | trailer: " + editTextTrailer.getText().toString() +
                                    " | price: " + editTextPricing.getText().toString());

                            saveMovieDataToFirestore(editTextTitle.getText().toString(), editTextDescription.getText().toString(),
                                    selectedGenres, editTextHours.getText().toString()+"h, "+editTextMinutes.getText().toString()+" min",
                                    releaseDate,
                                    imageUrl, editTextCastAndCrew.getText().toString(), editTextTrailer.getText().toString(),
                                    selectedDates, getScreen(), pricing);

                            Log.i(TAG, "Glob Id : "+globDocId);

                              //Intent intent = new Intent(AddMovieFragment.this.getContext(), SetSeatsAvailabilityActivity.class);
                              //startActivity(intent);
                        }
                    });
                }).addOnFailureListener(e -> {

                });

            }
        });

        datePickerDialogShowtime = new DatePickerDialog(this.getContext(),
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
        timePickerDialog = new TimePickerDialog(this.getContext(),
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
        Button datePickerButton = rootView.findViewById(R.id.dateAndTimePickerButton);
        datePickerButton.setOnClickListener(v -> datePickerDialogShowtime.show());

        dateTimeList = rootView.findViewById(R.id.dateTimeList);

        return rootView;
    }

    private void showTimePicker() {
        timePickerDialog.show();
    }

    private void updateUI(Date selectedDateTime) {
        selectedDates.add(formatDate(selectedDateTime));

        TextView textView = new TextView(this.getContext());
        textView.setText(formatDate(selectedDateTime));
        textView.setTextSize(16);

        dateTimeList.addView(textView);
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }
    String globDocId;
    private void saveMovieDataToFirestore(String title, String description, List<String> genre,
                                          String duration, String releaseDate, String imageUrl,
                                          String castAndCrew, String trailer, List<Object> showTimes,
                                          String screen, int pricing) {
        MovieEntity movieEntity = new MovieEntity(title, description, genre, duration, releaseDate, imageUrl,
                castAndCrew, trailer);
        ShowtimeEntity showtimeEntity = new ShowtimeEntity(showTimes, screen, pricing, movieEntity, "empty");

        db.collection("showtime")
                .add(showtimeEntity)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();

                    globDocId = documentId;
                    System.out.println("Add movie : " + documentId);
                    Log.i(TAG, "Add movie : " + documentId);

                    if(documentId!=null) {
                        Intent intent = new Intent(AddMovieFragment.this.getContext(), SetSeatsAvailabilityActivity.class);
                        intent.putExtra("documentId", documentId);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "Add movie : fail");
                });
    }

    private final CheckBox.OnCheckedChangeListener onCheckedChangeListener = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            String genre = buttonView.getText().toString();

            // Add or remove the genre from the list based on the checked genre
            if (isChecked) {
                selectedGenres.add(genre);
            } else {
                selectedGenres.remove(genre);
            }
        }
    };

    private void checkStoragePermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),
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
                                    getActivity().getContentResolver(),
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


    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                releaseDate = makeDateString(day, month, year);
                dateButton.setText(releaseDate);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);

    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month)+" "+day+" "+year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        else if(month == 2)
            return "FEB";
        else if(month == 3)
            return "MAR";
        else if(month == 4)
            return "APR";
        else if(month == 5)
            return "MAY";
        else if(month == 6)
            return "JUN";
        else if(month == 7)
            return "JUL";
        else if(month == 8)
            return "AUG";
        else if(month == 9)
            return "SEP";
        else if(month == 10)
            return "OCT";
        else if(month == 11)
            return "NOV";
        else if(month == 12)
            return "DEC";

        return "JAN";
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month++;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}