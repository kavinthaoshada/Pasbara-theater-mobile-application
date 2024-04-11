package lk.calm.pasbaratheater01;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = UserProfileActivity.class.getName();
    private FirebaseAuth mAuth;
    private MaterialCardView selectPhoto;
    private RoundedImageView carImageView;
    private Uri imageUri;
    private Bitmap bitmap;
    private FirebaseFirestore db;
    String insImage;
    String insUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //load image
        selectPhoto = findViewById(R.id.selectCarPhoto);
        carImageView = findViewById(R.id.imageProfile);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "in image button");
                checkStoragePermission();
                System.out.println("in image button");
            }
        });
        //load image

        // Search User Data
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        String profileImage = document.getString("imgUrl");
                        insImage = profileImage;
                        if(profileImage!=null){
                            RoundedImageView roundedImageView = findViewById(R.id.imageProfile);
                            RequestOptions requestOptions = new RequestOptions()
                                    .placeholder(R.drawable.male_user)
                                    .error(R.drawable.male_user);

                            Glide.with(UserProfileActivity.this)
                                    .load(profileImage).apply(requestOptions)
                                    .into(roundedImageView);
                        }
                        String name = document.getString("name");
                        insUserName = name;
                        TextView user_name = findViewById(R.id.textUsername);
                        user_name.setText(name);
                    }
                }).addOnFailureListener(e -> {
                    Log.i(TAG, "users loading failure..");
                });
        // Search User Data

    }

    // Updating profile image
    private void updateProfileImage(){
        Log.i(TAG, "I'm in update..");
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + insUserName + ".jpg");

        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            if (imageUrl == null) {
                                imageUrl = insImage;
                            }

                    Map<String, Object> data = new HashMap<>();
                    data.put("imgUrl", imageUrl);

                    db.collection("users").document(mAuth.getCurrentUser().getUid())
                            .update(data).addOnSuccessListener(task ->{

                            }).addOnFailureListener(e ->{

                            });

                });
            }).addOnFailureListener(e -> {

            });
        }else{

        }
    }
    // Updating profile image

    // image loding
    private void checkStoragePermission() {
        Log.i(TAG, "in checkStoragePermission");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            Log.i(TAG, "in checkStoragePermission 0.1");
            if(ContextCompat.checkSelfPermission(UserProfileActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "in checkStoragePermission 0.1.1");
                ActivityCompat.requestPermissions(UserProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                Log.i(TAG, "in checkStoragePermission 0.2");
                pickImageFromGallery();
            }
        }else{
            Log.i(TAG, "in checkStoragePermission 0.3");
            pickImageFromGallery();
        }
    }
    private void pickImageFromGallery() {
        Log.i(TAG, "in pickImageFromGallery");
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
                                    UserProfileActivity.this.getContentResolver(),
                                    imageUri
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(imageUri!=null){
                        carImageView.setImageBitmap(bitmap);
                        updateProfileImage();
                    }
                }
            }
    );
    // image loding
}