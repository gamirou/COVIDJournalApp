package com.gamiro.covidjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.viewmodels.RegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.lang.ref.Reference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // Layout
    private ImageView imgUserPicture;
    private Button btnBack;
    private TextView textLoginPage;
    private ProgressBar progressRegister;

    private NavController navController;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgUserPicture = findViewById(R.id.img_user_picture);
        btnBack = findViewById(R.id.btn_reg_back);
        textLoginPage = findViewById(R.id.tv_register_page);
        progressRegister = findViewById(R.id.pb_register);
        setProgressState(false);

        navController = Navigation.findNavController(this, R.id.nav_host_register_fragment);

        // Pick profile picture
        imgUserPicture.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestPermission();
            } else {
                pickAnImage();
            }
        });

        btnBack.setOnClickListener(view -> RegisterActivity.super.onBackPressed());

        // View model
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Open Login
        textLoginPage.setOnClickListener(view -> openLoginPage());
    }

    private void openLoginPage() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // Images
    private void checkAndRequestPermission() {
        // Check if permission already granted
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Check if permission shown
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Please accept the following permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(
                        RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        AppUtil.REQUEST_PROFILE_IMAGE);
            }
        } else {
            pickAnImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppUtil.REQUEST_PROFILE_IMAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickAnImage();
            } else {
                //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                Toast.makeText(RegisterActivity.this, "Please accept the following permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Set progress visible / gone
    public void setProgressState(boolean state) {
        int progressState = state ? View.VISIBLE : View.GONE;
        int contentState = state ? View.INVISIBLE : View.VISIBLE;

        progressRegister.setVisibility(progressState);
        findViewById(R.id.nav_host_register_fragment).setVisibility(contentState);
    }


    // Pick Image
    private void pickAnImage() {
        CropImage.startPickImageActivity(this);
    }
    // Crop image
    private void cropRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from selected image
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            cropRequest(imageUri);
        }

        // result from cropping activity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Return cropped image
                // Image cropper
                Uri croppedImageUri = result.getUri();
                viewModel.setUserImage(croppedImageUri);
                imgUserPicture.setImageURI(croppedImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: " + result.getError().toString());
            }
        }
    }
}
