package com.gamiro.covidjournal.repositories;

import android.net.Uri;
import android.util.Log;

import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.notifications.NotificationModel;
import com.gamiro.covidjournal.models.user.UserData;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AuthRepository {

    private static final String TAG = "AuthRepository";

    private MutableLiveData<Boolean> isSignInSuccessful = new MutableLiveData<>();
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    public AuthRepository() {
        //mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    public void createUserWithEmail(UserData data, FirebaseUser user, Uri image) {
        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "createUserWithEmail:success");

        if (image == null) {
            Log.i(TAG, "Image does not exist");
            updateUserProfile(data, user);
        } else {
            addImageToStorage(image, data, user);
        }
    }

    public void addUserToDatabase(UserData data, String userID) {
        DatabaseReference userRef = mDatabase.getReference("users/" + userID);
        if (data.getImage() == null) {
            data.setImage("");
        }
        userRef.setValue(data);
        sendNotificationToVerifyEmail(userID);
        isSignInSuccessful.setValue(true);
    }

    public void updateUserProfile(UserData data, FirebaseUser user) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(data.getName())
                .build();

        user.updateProfile(profileUpdate)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // user info updated successfully
                        Log.i(TAG,"updateUserWithoutImage: success");
                        user.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.i(TAG, "updateUserProfile: email sent");
                            } else {
                                Log.e(TAG, "updateUserProfile: ", task1.getException());
                            }
                        });
                        addUserToDatabase(data, user.getUid());
                    }
                });
    }

    public void addImageToStorage(Uri image, UserData data, FirebaseUser user) {
        String userID = user.getUid();
        StorageReference reference = mStorage.getReference().child("users_photos").child(userID);

        reference.putFile(image).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(data.getName())
                    .setPhotoUri(uri)
                    .build();

            user.updateProfile(profileUpdate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // user info updated successfully
                            Log.i(TAG, "updateProfile: success");

                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.i(TAG, "updateUserProfile: email sent");
                                } else {
                                    Log.e(TAG, "updateUserProfile: ", task1.getException());
                                }
                            });;
                            data.setImage(uri.toString());
                            addUserToDatabase(data, userID);
                        } else {
                            Log.i(TAG, "updateProfile: failure");
                        }

                    }); // on complete
        }));
    }

    private void sendNotificationToVerifyEmail(String uid) {
        // Easier to remove
        DatabaseReference reference = mDatabase.getReference("notifications/" + uid + "/" + AppUtil.REMINDER_VERIFY_EMAIL);
        NotificationModel notificationModel = new NotificationModel(
                "Don't forget to verify your email",
                "Some features are not available until you verify your email. Click the link to send an email if you hadn't received one.",
                AppUtil.getCurrentTimeDate(), AppUtil.REMINDER_VERIFY_EMAIL
        );
        reference.setValue(notificationModel);
    }

    //    private void updateUserInfo(final String name, final Uri pickedImgUri, FirebaseUser currentUser, String userID) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference photoRef = storage.getReference().child("users_photos");
//        System.out.println("Update user info start");
//
//        // Get path and user
//        final FirebaseUser user = currentUser;
//        final StorageReference imageFilePath = photoRef.child(userID);
//
//        if (pickedImgUri != null) {
//            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // image uploaded successfully
//                    // now we can get our image url
//                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            // uri contain user image url
//                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(name)
//                                    .setPhotoUri(uri)
//                                    .build();
//
//                            user.updateProfile(profileUpdate)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                            if (task.isSuccessful()) {
//                                                // user info updated successfully
//                                                Toast.makeText(RegisterActivity.this, "Registration complete.",
//                                                        Toast.LENGTH_SHORT).show();
//
//                                                database.getReference("users/" + userID).child("image").setValue(uri.toString());
//                                                updateUI();
//                                            }
//
//                                        }
//                                    }); // on complete
//                        } // on success
//                    }); // download url
//                } // on success
//            }); // image.putfile
//        } else {
//        }
//    }
//

    public LiveData<Boolean> getIsSignInSuccessful() {
        return isSignInSuccessful;
    }

    public void setIsSignInSuccessful(Boolean bool) {
        isSignInSuccessful.setValue(bool);
    }
}
