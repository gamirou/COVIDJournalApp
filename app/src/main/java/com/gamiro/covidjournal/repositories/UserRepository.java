package com.gamiro.covidjournal.repositories;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.AppServerService;
import com.gamiro.covidjournal.models.notifications.NotificationModel;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.models.user.UserPost;
import com.gamiro.covidjournal.models.user.UserSymptoms;
import com.gamiro.covidjournal.models.user.UserTest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRepository {

    private static final int MAXIMUM_USERS = 50;
    private static final String TAG = "UserRepository";
    public static final String APP_SERVER_BASE_URL = "https://app.covidjournal.net/";

    private MutableLiveData<UserData> userLiveData;
    private MutableLiveData<HashMap<String, UserData>> allUsers;
    private MutableLiveData<HashMap<String, UserData>> allFriends;
    private MutableLiveData<Boolean> verified;

    // Posts - notifications (from system)
    private MutableLiveData<ArrayList<UserPost>> allPosts;
    private MutableLiveData<ArrayList<NotificationModel>> allNotificationsModels;
    private MutableLiveData<HashMap<String, ArrayList<String>>> allNotificationActions;

    // Tests
    private MutableLiveData<HashMap<String, UserTest>> allTests;

    // Symptoms
    private MutableLiveData<UserSymptoms> userSymptoms;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private FirebaseUser currentUser;

    // getting data is troublesome
    public UserRepository() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    // LOADING USER DATA
    public MutableLiveData<UserData> getUserData() {
        if (userLiveData == null) {
            userLiveData = new MutableLiveData<>();
        }
        loadUserData();
        return userLiveData;
    }
    private void loadUserData() {
        final DatabaseReference userRef = mDatabase.getReference("users/" + currentUser.getUid());
        final UserData[] fetchData = new UserData[1];

        Log.i(TAG, "Inside loadUser data");

        // Attach a listener to read the data at our user reference
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchData[0] = dataSnapshot.getValue(UserData.class);
                System.out.println("INSIDE USER REF LISTENER: We've got the data");
                userLiveData.postValue(fetchData[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    public void setUserData(@NonNull UserData userData, Uri croppedImageUri, String id) {
        if (croppedImageUri == null) {
            if (id == null) {
                final DatabaseReference userRef = mDatabase.getReference("users/" + currentUser.getUid());
                userRef.setValue(userData, ((databaseError, databaseReference) -> loadUserData()));
            } else {
                final DatabaseReference userRef = mDatabase.getReference("users/" + id);
                userRef.setValue(userData, (databaseError, databaseReference) -> {
                    loadAllFriends(userLiveData.getValue());
                });
            }
        } else {
            addImageToStorage(croppedImageUri, userData);
        }
    }
    public void addImageToStorage(Uri image, UserData data) {
        String userID = currentUser.getUid();
        StorageReference reference = mStorage.getReference().child("users_photos").child(userID);

        reference.putFile(image).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build();

            currentUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // user info updated successfully
                            Log.i(TAG, "updateProfile:success");

                            data.setImage(uri.toString());
                            setUserData(data, null, null);
                        } else {
                            Log.i(TAG, "updateProfile:failure");
                        }

                    }); // on complete
        }));
    }

    // LOAD CURRENT USER ID
    public String getCurrentUserID() {
        return currentUser.getUid();
    }
    public MutableLiveData<Boolean> isEmailVerified() {
        if (verified == null) {
            verified = new MutableLiveData<>();
        }

        boolean isVerified = currentUser.isEmailVerified();

        if (isVerified) {
            deleteNotificationFromDatabase(AppUtil.REMINDER_VERIFY_EMAIL);
        }

        verified.setValue(isVerified);
        return verified;
    }

    // LOADING USERS BY QUERY FOR ADDING FRIENDS
    public LiveData<HashMap<String, UserData>> getAllUsers(String query) {
        if (allUsers == null) {
            allUsers = new MutableLiveData<>();
        }

        loadAllUsers(query);
        return allUsers;
    }
    private void loadAllUsers(String query) {
        DatabaseReference ref = mDatabase.getReference("users");
        HashMap<String, UserData> users = new HashMap<>();

        Log.i(TAG, "Query is empty: " + query.isEmpty());
        Log.i(TAG, "Query is: " + query);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot d: dataSnapshot.getChildren()) {
                        UserData data = d.getValue(UserData.class);
                        if (data != null) {
                            if (data.getName().toLowerCase().contains(query.toLowerCase())) {
                                users.put(d.getKey(), data);
                            }
                        }
                        i++;

                        // Safety limit
                        if (i >= MAXIMUM_USERS) {
                            break;
                        }
                    }
                    allUsers.postValue(users);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // LOADING FRIENDS DATA
    public MutableLiveData<HashMap<String, UserData>> getAllFriends(UserData data) {
        if (allFriends == null) {
            allFriends = new MutableLiveData<>();
        }

        if (data != null) {
            loadAllFriends(data);
        }
        return allFriends;
    }
    public void loadAllFriends(UserData current) {
        HashMap<String, UserData> friendsMap = new HashMap<>();

        if (current != null) {
            if (current.getFriends() != null) {
                int index = 0;

                // NOT EFFICIENT, ALSO CAUSE OF FRIENDS NOT UPDATING
                // However, you cannot read everything at once and read all friends at once
                for (String id : current.getFriends().keySet()) {
                    DatabaseReference ref = mDatabase.getReference("users/" + id);

                    final UserData[] friendData = new UserData[1];
                    int finalIndex = index;
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            friendData[0] = dataSnapshot.getValue(UserData.class);
                            friendsMap.put(id, friendData[0]);

                            if (finalIndex >= current.getFriends().size() - 1) {
                                allFriends.postValue(friendsMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "loadAllFriends:failure + " + databaseError.toString());
                        }
                    });

                    index++;
                }
            }
        }
    }

    // LOADING CONTACTS BASED ON POSTS AND 14 DAYS
    public ArrayList<String> getContacts(ArrayList<UserPost> posts, String testResultDate) {
        ArrayList<String> ids = new ArrayList<>();

        for (UserPost post : posts) {
            if (post.getId() != null) {
                if (ids.contains(post.getId())) {
                    continue;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String postDate = post.getDate();

                    int dayDifference = Integer.parseInt(AppUtil.getDateDifferenceDays(testResultDate, postDate));

                    Log.i(TAG, "getContacts: day difference = " + dayDifference);
                    if (dayDifference <= 14) {
                        ids.add(post.getId());
                    }
                } else {
                    // Add id if older device as simple date format not available
                    ids.add((post.getId()));
                }
            }
        }

        return ids;
    }

    // LOADING POSTS AND ADDING POSTS
    public MutableLiveData<ArrayList<UserPost>> getAllPosts() {
        if (allPosts == null) {
            allPosts = new MutableLiveData<>();
        }

        loadAllPosts();
        return allPosts;
    }
    private void loadAllPosts() {
        DatabaseReference reference = mDatabase.getReference("posts/" + getCurrentUserID());
        ArrayList<UserPost> posts = new ArrayList<>();

        Log.i(TAG, "Current UID: @" + getCurrentUserID());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "Posts updated again");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i(TAG, "Data key: @" + ds.getKey());
                    Log.i(TAG, "Data title: @" + ds.getValue(UserPost.class).getPersonName());
                    posts.add(ds.getValue(UserPost.class));
                }

                int i = 0;
                for (UserPost p: posts) {
                    Log.i(TAG, i + " - " + p.getTitle() + " - " + p.getPersonName());
                    i++;
                }

                allPosts.setValue(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "LoadingPosts:failure + "  + databaseError.getMessage());
            }
        });
    }
    public void addPostToDatabase(@NonNull UserPost post, String name, String gender) {
        if (post.getPostType().equals("activity")) {
            DatabaseReference reference = mDatabase.getReference(
                    "posts/" + getCurrentUserID() + "/" + post.getTimeWhenPostAdded());
            reference.setValue(post);
        } else {
            DatabaseReference reference = mDatabase.getReference("posts");
            // Add post to the owner
            reference.child(post.getWhoMadePost() + "/" + post.getTimeWhenPostAdded()).setValue(post);

            if (!post.getId().isEmpty()) {
                // Add post to the current user
                UserPost other = UserPost.clone(post);

                String id = post.getWhoMadePost();

                other.setId(id);
                other.setPersonName(post.getWhoMadePostPersonName());

                reference.child(getCurrentUserID() + "/" + other.getTimeWhenPostAdded()).setValue(other);

                Log.i(TAG, "addPostToDatabase: POST ID @" + id);
                Log.i(TAG, "addPostToDatabase: CURRENT USER ID @" + getCurrentUserID());

                String title = name + " has accepted your post";
                String body = title + ". If you or " + AppUtil.getPronoun(gender) + " catch the virus, you will be notified!";
                sendNotificationToUser(id, title, body, "", post.getTimeWhenPostAdded());

                deleteNotificationFromDatabase(post.getTimeWhenPostAdded());
            }
        }
    }
    public void addPostToBeAccepted(UserPost post, String gender) {
        DatabaseReference reference = mDatabase.getReference(
                "notifications/" + post.getId() + "/" + post.getTimeWhenPostAdded());

        String pronoun = AppUtil.getPronoun(gender);
        String title = post.getWhoMadePostPersonName() + " has said you met " + pronoun;
        String body = "Please accept or reject this post";

        NotificationModel notificationModel = new NotificationModel(title, body, post.getTimeWhenPostAdded(), "");
        notificationModel.setPost(post);
        reference.setValue(notificationModel);

        sendNotificationToUser(post.getId(), title, body, AppUtil.ACCEPT_POST, "");
    }
    public void cancelPost(@NonNull UserPost post) {
        if (post.getId().isEmpty()) {
            DatabaseReference reference = mDatabase.getReference(
                    "posts/" + getCurrentUserID() + "/" + post.getTimeWhenPostAdded());
            reference.removeValue();
        } else {
            deleteNotificationFromDatabase(post.getTimeWhenPostAdded());
        }
    }

    // LOADING AND ADDING TESTS
    public MutableLiveData<HashMap<String, UserTest>> getAllTests() {
        if (allTests == null) {
            allTests = new MutableLiveData<>();
        }

        loadAllTests();
        return allTests;
    }
    private void loadAllTests() {
        DatabaseReference reference = mDatabase.getReference("tests/" + getCurrentUserID());

        HashMap<String, UserTest> result = new HashMap<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    result.put(ds.getKey(), ds.getValue(UserTest.class));
                }

                allTests.postValue(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }
    public void addTestToDatabase(UserTest test, String id) {
        DatabaseReference reference = mDatabase.getReference("tests/" + getCurrentUserID() + "/" + id);
        reference.setValue(test);

        //      When this function is performed, notification actions live data is not empty, because it is observed when user
        // test fragment is shown.
        if (test.getResult().equals("PENDING")) {
            addPendingTest(test, id);
        }
    }
    public void addPendingTest(UserTest test, String id) {
        HashMap<String, ArrayList<String>> actions = allNotificationActions.getValue();
        if (!actions.containsKey(AppUtil.REMINDER_TEST_PENDING)) {
            String title = "Don't forget to add the result of your test";
            String body = "The test added at " + id + " will need to be updated with the result";

            NotificationModel notificationModel = new NotificationModel(title, body, id, AppUtil.REMINDER_TEST_PENDING);
            notificationModel.setTest(test);

            DatabaseReference reference = mDatabase.getReference("notifications/" + getCurrentUserID() + "/" + id);
            reference.setValue(notificationModel);
        }
    }
    public void deleteTestFromDatabase(String id, String status) {
        DatabaseReference reference = mDatabase.getReference("tests/" + getCurrentUserID() + "/" + id);
        reference.removeValue();

        if (status.equals("PENDING")) {
            deleteNotificationFromDatabase(id);
        }
    }

    // SYMPTOMS
    public MutableLiveData<UserSymptoms> getUserSymptoms() {
        if (userSymptoms == null) {
            userSymptoms = new MutableLiveData<>();
        }

        loadUserSymptoms();
        return userSymptoms;
    }
    private void loadUserSymptoms() {
        DatabaseReference reference = mDatabase.getReference("symptoms/" + getCurrentUserID());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserSymptoms symptoms = dataSnapshot.getValue(UserSymptoms.class);
                userSymptoms.postValue(symptoms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: true", databaseError.toException());
            }
        });
    }
    public void editUserSymptoms(UserSymptoms data) {
        DatabaseReference reference = mDatabase.getReference("symptoms/" + getCurrentUserID());
        reference.setValue(data);
    }

    // NOTIFICATIONS IN DATABASE
    public MutableLiveData<ArrayList<NotificationModel>> getNotificationModels() {
        if (allNotificationsModels == null) {
            allNotificationsModels = new MutableLiveData<>();
        }

        loadAllNotificationModels();
        return allNotificationsModels;
    }
    private void loadAllNotificationModels() {
        DatabaseReference reference = mDatabase.getReference("notifications/" + getCurrentUserID());
        ArrayList<NotificationModel> resultList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    resultList.add(child.getValue(NotificationModel.class));
                }

                allNotificationsModels.postValue(resultList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: failure", databaseError.toException());
            }
        });
    }
    private void addNotificationToDatabase(String id, String title, String body, String action) {
        String dateCreated = AppUtil.getCurrentTimeDate();
        NotificationModel notification = new NotificationModel(title, body, dateCreated, action);

        // Store in realtime database
        DatabaseReference reference = mDatabase.getReference("notifications/" + id + "/" + dateCreated);
        reference.setValue(notification);
    }
    private void addNotificationToDatabase(ArrayList<String> ids, String title, String body, String action) {
        String dateCreated = AppUtil.getCurrentTimeDate();
        NotificationModel notification = new NotificationModel(title, body, dateCreated, action);

        // Store in realtime database
        DatabaseReference reference = mDatabase.getReference("notifications/");
        for (String id: ids) {
            reference.child(id + "/" + dateCreated).setValue(notification);
        }
    }
    // Will need timeCreated (its id) because UserPost is sent to the function and not NotificationModel. They both share same thing
    public void deleteNotificationFromDatabase(String timeCreated) {
        DatabaseReference reference = mDatabase.getReference("notifications/" + getCurrentUserID() + "/" + timeCreated);
        reference.removeValue((databaseError, databaseReference) -> loadAllNotificationModels());
    }
    public LiveData<HashMap<String, ArrayList<String>>> getNotificationActions(ArrayList<NotificationModel> notifications) {
        if (allNotificationActions == null) {
            allNotificationActions = new MutableLiveData<>();
        }

        HashMap<String, ArrayList<String>> result = new HashMap<>();
        for (NotificationModel notif : notifications) {
            ArrayList<String> ids = new ArrayList<>();
            if (result.containsKey(notif.getAction())) {
                ids = result.get(notif.getAction());
            }
            ids.add(notif.getDateCreated());
            result.put(notif.getAction(), ids);
        }

        allNotificationActions.setValue(result);
        return allNotificationActions;
    }

    // FCM
    public void addTokenToServerDatabase(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APP_SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AppServerService apiService = retrofit.create(AppServerService.class);
        Call<ResponseBody> call = apiService.sendTokenToDatabase(getCurrentUserID(), token, AppUtil.APP_SERVER_CODE);
        Log.d(TAG, "addTokenToServerDatabase: before api call");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Insert token: success" + " body: " + response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Insert token: failure", t);
            }
        });
    }
    public void deleteTokenFromServerDatabase(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APP_SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AppServerService apiService = retrofit.create(AppServerService.class);
        Call<ResponseBody> call = apiService.deleteTokenFromDatabase(getCurrentUserID(), token, AppUtil.APP_SERVER_CODE);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Delete token: success" + " body: " + response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Delete token: failure", t);
            }
        });
    }

    // Sending notifications
    // one user
    public void sendNotificationToUser(String userID, String title, String body, String action, String idToBeRemoved) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APP_SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AppServerService apiService = retrofit.create(AppServerService.class);
        Call<ResponseBody> call = apiService.sendNotification(userID, title, body, action, AppUtil.APP_SERVER_CODE);
        Log.d(TAG, "sendNotificationToUser: before on response");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "sendNotification: onResponse: success" + " body: " + response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: failure", t);
            }
        });

        // Send notification to database
        if (!action.isEmpty() && !action.equals(AppUtil.ACCEPT_POST)) {
            addNotificationToDatabase(userID, title, body, action);
        } else {
            // remove old one
            deleteNotificationFromDatabase(idToBeRemoved);
        }

    }
    // multiple users
    public void sendNotificationToUser(ArrayList<String> ids, String title, String body, String action, String idToBeRemoved) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APP_SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AppServerService apiService = retrofit.create(AppServerService.class);
        Call<ResponseBody> call = apiService.sendNotification(AppUtil.listToString(ids), title, body, action, AppUtil.APP_SERVER_CODE);
        Log.d(TAG, "sendNotificationToUser: before on response");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "sendNotification: onResponse: success" + " body: " + response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: failure", t);
            }
        });

        // Send notification to database
        if (!action.isEmpty() && !action.equals(AppUtil.ACCEPT_POST)) {
            addNotificationToDatabase(ids, title, body, action);
        } else {
            // remove old one
            deleteNotificationFromDatabase(idToBeRemoved);
        }

    }
}
