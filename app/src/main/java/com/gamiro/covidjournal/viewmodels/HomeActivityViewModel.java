package com.gamiro.covidjournal.viewmodels;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.countries.CountryStatistics;
import com.gamiro.covidjournal.models.notifications.NotificationModel;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.models.news.News;
import com.gamiro.covidjournal.models.user.UserPost;
import com.gamiro.covidjournal.models.user.UserTest;
import com.gamiro.covidjournal.repositories.CoronaRepository;
import com.gamiro.covidjournal.repositories.NewsRepository;
import com.gamiro.covidjournal.repositories.UserRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class HomeActivityViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";

    // Basic stuff needed by every fragment
    private MutableLiveData<UserData> userData;
    private MutableLiveData<HashMap<String, CountryStatistics>> coronaData;

    // Is email verified
    private MutableLiveData<Boolean> emailVerified;

    // For feed
    private LiveData<List<News>> coronaNews;

    // Profile image
    private MutableLiveData<Uri> profileImageUri = new MutableLiveData<>();

    // Profile country
    private MutableLiveData<String> userCountry = new MutableLiveData<>();

    // For posts
    private MutableLiveData<ArrayList<UserPost>> userPosts;

    // For notifications
    private MutableLiveData<ArrayList<NotificationModel>> userNotifications;
    private LiveData<HashMap<String, ArrayList<String>>> userNotificationActions;

    // For user tests
    private MutableLiveData<HashMap<String, UserTest>> userTests;

    // For friends
    private LiveData<HashMap<String, UserData>> allUsers;
    private LiveData<HashMap<String, UserData>> allFriends;

    // Search query for users
    private MutableLiveData<String> usersQuery = new MutableLiveData<>();

    // Current friend selected on add person
    private MutableLiveData<String> friendSelectedIdAddPerson = new MutableLiveData<>("");

    // Connectivity
    private MutableLiveData<Boolean> isNetworkAvailable = new MutableLiveData<>(true);

    // Repositories
    private CoronaRepository coronaRepository;
    private UserRepository userRepository;
    private NewsRepository newsRepository;

    // Initialize repositories
    public HomeActivityViewModel() {
        coronaRepository = new CoronaRepository();
        userRepository = new UserRepository();
        newsRepository = new NewsRepository();

        // User data needed almost everywhere
        userData = userRepository.getUserData();
    }

    /**
     * Network
     */
    public LiveData<Boolean> isNetworkAvailable() {
        return isNetworkAvailable;
    }

    public void setIsNetworkAvailable(boolean value) {
        isNetworkAvailable.postValue(value);
    }

    /**
     * Cities
     */
    public void setUserCountry(String countryName) {
        userCountry.setValue(countryName);
    }
    public LiveData<String> getUserCountry() {
        if (userCountry == null) {
            userCountry = new MutableLiveData<>();
        }
        return userCountry;
    }

    /**
     * NEWS FOR FEED
     */
    public LiveData<List<News>> getCoronaNews() {
        coronaNews = Transformations.switchMap(userCountry, country -> newsRepository.getNewsForCountry(country));
        return coronaNews;
    }

    // DEBUG
    public void setAdStatus(UserData data, boolean ad) {
        data.setAdEnabled(ad);
        userRepository.setUserData(data, null, null);
    }

    /**
     * CORONA DATA
     */
    public LiveData<HashMap<String, CountryStatistics>> getCoronaData() {
        if (coronaData == null) {
            coronaData = new MutableLiveData<>();
        }

        coronaData = coronaRepository.getCoronaData();
        return coronaData;
    }
    public LiveData<CountryStatistics> getCoronaCountry() {
        return Transformations.switchMap(userCountry, country -> coronaRepository.getCoronaDataOwnCountry(country));
    }

    /**
     * POSTS FUNCTIONS OR STUFF
     */
    public LiveData<ArrayList<UserPost>> getUserPosts() {
        userPosts = userRepository.getAllPosts();
        return userPosts;
    }
    public void addPostPerson(UserPost post) {
        post.setTimeWhenPostAdded(AppUtil.getCurrentTimeDate());
        post.setWhoMadePostPersonName(userData.getValue().getName());
        if (userData.getValue().getName() != null) {
            if (!post.isAccepted()) {
                String personId = friendSelectedIdAddPerson.getValue();
                post.setId(personId);
                userRepository.addPostToBeAccepted(post, userData.getValue().getGender());
            } else {
                userRepository.addPostToDatabase(post, userData.getValue().getName(), "them");
            }
        }
    }
    public void addPostOther(UserPost post) {
        post.setTimeWhenPostAdded(AppUtil.getCurrentTimeDate());
        userRepository.addPostToDatabase(post, "", "");
    }
    public void acceptPost(UserPost post) {
        userRepository.addPostToDatabase(post, userData.getValue().getName(), userData.getValue().getGender());
        String title = "";
        String body = "";
        if (userData.getValue().isHasCorona()) {
            title = post.getWhoMadePostPersonName() + " has been tested positive";
            body = "Order a test and self-isolate";
        }
        if (userData.getValue().isMightHaveCorona()) {
            title = "This person might have it";
            body = "Order a test and self-isolate";
        }

        if (!title.isEmpty()) {
            userRepository.sendNotificationToUser(post.getWhoMadePost(), title, body, AppUtil.OPEN_USER_TEST, "");
        }
    }
    public void cancelPost(UserPost post) {
        userRepository.cancelPost(post);
    }

    /**
     * TESTS FOR USERS
     */
    public LiveData<HashMap<String, UserTest>> getUserTests() {
        if (userTests == null) {
            userTests = new MutableLiveData<>();
        }

        userTests = userRepository.getAllTests();
        return userTests;
    }
    public void addUserTest(UserTest test, String id) {
        if (id.isEmpty()) {
            id = AppUtil.getCurrentTimeDate();
        } else {
            userRepository.deleteNotificationFromDatabase(id);
        }
        userRepository.addTestToDatabase(test, id);
    }
    public void deleteUserTest(String id, String status) {
        userRepository.deleteTestFromDatabase(id, status);
    }

    /**
     * ALL USERS & FRIENDS
     */
    public LiveData<HashMap<String, UserData>> getAllUsers() {
        allUsers = Transformations.switchMap(usersQuery, query -> userRepository.getAllUsers(query));
        return allUsers;
    }
    public LiveData<HashMap<String, UserData>> getAllFriends() {
        allFriends = Transformations.switchMap(userData, data -> userRepository.getAllFriends(data));
        return allFriends;
    }
    // TODO: Move some of this code inside user repository
    public void sendFriendRequestUserData(String id, UserData data) {
        boolean isFriend = false;

        UserData currentUserData = userData.getValue();
        String currentUserID = userRepository.getCurrentUserID();

        // Current user
        HashMap<String, String> friendsCurrent = new HashMap<>();
        if (currentUserData.getFriends() != null) {
            friendsCurrent = currentUserData.getFriends();
            for (String key: friendsCurrent.keySet()) {
                if (key.equals(id)) {
                    isFriend = true;
                    break;
                }
            }
        }

        // Other user
        HashMap<String, String> friendsOther = new HashMap<>();
        if (data.getFriends() != null) {
            friendsOther = data.getFriends();
            for (String key: friendsOther.keySet()) {
                if (key.equals(currentUserID)) {
                    isFriend = true;
                    break;
                }
            }
        }

        HashMap<String, String> currentRequests = currentUserData.getFriendsRequestSent();
        HashMap<String, String> otherRequests = data.getFriendsRequestSent();

        if (!isFriend) {
            friendsCurrent.put(id, AppUtil.FRIEND_SEND_REQUEST);
            currentUserData.setFriends(friendsCurrent);

            currentRequests.put(id, AppUtil.getCurrentTimeDate());
            currentUserData.setFriendsRequestSent(currentRequests);

            friendsOther.put(currentUserID, AppUtil.FRIEND_PENDING);
            data.setFriends(friendsOther);

            otherRequests.put(currentUserID, AppUtil.getCurrentTimeDate());
            data.setFriendsRequestSent(otherRequests);

            userRepository.setUserData(currentUserData, null, null);
            userRepository.setUserData(data, null, id);

            String title = AppUtil.getTitleFromNotificationType(AppUtil.OPEN_FRIENDS, currentUserData.getName());
            String body = AppUtil.getBodyFromNotificationType(AppUtil.OPEN_FRIENDS, currentUserData.getName());

            userRepository.sendNotificationToUser(id, title, body, AppUtil.OPEN_FRIENDS, "");
            userRepository.getAllUsers(usersQuery.getValue());
        } else {
            if (friendsCurrent.get(id).equals(AppUtil.FRIEND_PENDING)) {
                acceptFriendRequest(id, data);
                userRepository.getAllUsers(usersQuery.getValue());
            }
        }
    }
    public void acceptFriendRequest(String id, UserData data) {
        UserData currentUserData = getUserData().getValue();
        String currentUserID = getCurrentUserID();

        HashMap<String, String> friendsCurrent = currentUserData.getFriends();
        HashMap<String, String> friendsOther = data.getFriends();

        friendsCurrent.put(id, AppUtil.FRIEND_ACCEPTED);
        friendsOther.put(currentUserID, AppUtil.FRIEND_ACCEPTED);

        currentUserData.setFriends(friendsCurrent);
        data.setFriends(friendsOther);

        String title = AppUtil.getTitleFromNotificationType(AppUtil.FRIEND_ACCEPTED, currentUserData.getName());
        String body = AppUtil.getBodyFromNotificationType(AppUtil.FRIEND_ACCEPTED, currentUserData.getName());

        String notificationId = currentUserData.getFriendsRequestSent().get(id);
        userRepository.sendNotificationToUser(id, title, body, "", notificationId);

        userRepository.setUserData(currentUserData, null, null);
        userRepository.setUserData(data, null, id);
    }
    public void cancelFriendRequest(String id, UserData data) {
        UserData currentUserData = userData.getValue();
        String currentUserID = userRepository.getCurrentUserID();

        HashMap<String, String> friendsCurrent = currentUserData.getFriends();
        HashMap<String, String> friendsOther = data.getFriends();

        friendsCurrent.remove(id);
        friendsOther.remove(currentUserID);

        // Remove requests
        HashMap<String, String> currentRequests = currentUserData.getFriendsRequestSent();
        HashMap<String, String> otherRequests = data.getFriendsRequestSent();

        // Delete notification
        userRepository.deleteNotificationFromDatabase(currentRequests.get(id));

        currentRequests.remove(id);
        otherRequests.remove(currentUserID);

        userRepository.setUserData(currentUserData, null, null);
        userRepository.setUserData(data, null, id);
    }
    public void setUsersQuery(String query) {
        usersQuery.setValue(query);
        Log.i(TAG, "Query is: " + query);
    }

    /**
     * USER DATA
     * getCurrentUserId() does not return live data as the uid never changes unless the user logs out
     */
    public LiveData<UserData> getUserData() {
        return userData;
    }
    public String getCurrentUserID() {
        return userRepository.getCurrentUserID();
    }
    public LiveData<Boolean> isEmailVerified() {
        if (emailVerified == null) {
            emailVerified = new MutableLiveData<>();
        }

        emailVerified = userRepository.isEmailVerified();
        return emailVerified;
    }
    public void setUserData(String city) {
        Uri image = profileImageUri.getValue();
        UserData data = userData.getValue();

        if (data != null) {
            // Set the new city and country
            data.setCity(city);
            data.setCountry(userCountry.getValue());

            Log.i(TAG, "user country: " + userCountry.getValue());

            userRepository.setUserData(data, image, null);
        }
    }
    public void setUserCoronaState(boolean mightHaveCorona, Boolean hasCorona, UserTest test) {
        UserData data = userData.getValue();

        if (data != null) {
            data.setMightHaveCorona(mightHaveCorona);

            if (hasCorona != null) {
                data.setHasCorona(hasCorona);
            }
            userRepository.setUserData(data, null, null);


            String notificationType = "";
            if (mightHaveCorona) {
                notificationType = AppUtil.MIGHT_HAVE_CORONA;
            }

            if (hasCorona != null) {
                if (hasCorona) {
                    notificationType = AppUtil.HAS_CORONA;
                }
            }

            Log.d(TAG, "setUserCoronaState: notification type: @" + notificationType);
            if (!notificationType.isEmpty()) {
                String date = "";
                if (test != null) {
                    date = test.getDateResult();
                    if (date.isEmpty()) {
                        date = test.getDateMade();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        date = AppUtil.getToday();
                    }
                }

                Log.d(TAG, "setUserCoronaState: -> send Notification: success");
                sendNotificationToContacts(notificationType, date);
            }
        }
    }
    public void setMightHaveCorona(UserData data, String id, boolean mightHaveCorona) {
        data.setMightHaveCorona(mightHaveCorona);
        userRepository.setUserData(data, null, id);
    }

    /**
     * PROFILE IMAGE
     */
    public LiveData<Uri> getProfilePicture() {
        return profileImageUri;
    }
    public void setProfilePicture(Uri image) {
        profileImageUri.postValue(image);
    }

    /**
     * Add Person Contact code;
     */
    public LiveData<String> getFriendSelectedIdAddPerson() {
        if (friendSelectedIdAddPerson == null) {
            friendSelectedIdAddPerson = new MutableLiveData<>();
        }

        return friendSelectedIdAddPerson;
    }
    public void setFriendSelectedIdAddPerson(String id) {
        String current = friendSelectedIdAddPerson.getValue();
        if (!id.isEmpty() || (!current.isEmpty() && id.isEmpty())) {
            friendSelectedIdAddPerson.setValue(id);
        }
    }

    /**
     * Notifications
     */
    public LiveData<ArrayList<NotificationModel>> getNotificationModels() {
        userNotifications = userRepository.getNotificationModels();
        return userNotifications;
    }
    public void sendNotificationToContacts(String notificationType, String date) {
        if (notificationType.isEmpty()) {
            return;
        }

        String title = AppUtil.getTitleFromNotificationType(notificationType, userData.getValue().getName());
        String body = AppUtil.getBodyFromNotificationType(notificationType, userData.getValue().getName());
        HashMap<String, UserData> friends = allFriends.getValue();

        ArrayList<String> contactIds = userRepository.getContacts(Objects.requireNonNull(userPosts.getValue()), date);
        if (!contactIds.isEmpty()) {
            if (friends == null) {
                userRepository.sendNotificationToUser(contactIds, title, body, AppUtil.OPEN_USER_TEST, "");
            } else {
                ArrayList<String> positiveIds = new ArrayList<>();
                ArrayList<String> negativeIds = new ArrayList<>();

                for (String key : contactIds) {
                    if (friends.containsKey(key)) {
                        setMightHaveCorona(friends.get(key), key, true);

                        if (friends.get(key).isHasCorona()) {
                            positiveIds.add(key);
                        } else {
                            negativeIds.add(key);
                        }
                    }
                }

                Log.i(TAG, "sendNotificationToContacts: positive = " + positiveIds.size());
                Log.i(TAG, "sendNotificationToContacts: negative = " + negativeIds.size());

                userRepository.sendNotificationToUser(negativeIds, title, body, AppUtil.OPEN_USER_TEST, "");
                userRepository.sendNotificationToUser(positiveIds, title, body, "", "");
            }
        }


        /**
         * NOTIFICATION PLAND
         * The program should work this way:
         * 1. Get notification type from different parts of the app
         * - mightHaveCorona, hasCorona
         * 2. Create notification title & body inside viewmodel
         *  2.1 - mightHaveCorona + hasCorona
         *      whatever you have up there
         *      fetch user ids and send to Data Model
         *  2.2 - more reasons add here
         * 3. Send POST request to web server
         * * * * * * * * * * * * * *
         * Inside web server
         * 4. Check request:
         *  4.1 Cases
         *      4.1.1 Might || Has
         *          get all tokens from mysql database using PHP
         *          SELECT token FROM user_token_table WHERE userId == id (array)
         *     4.1.2 Other cases for single user
         *          SELECT token FROM user_token_table WHERE userId == inputId
         *  4.2 Server sends post request to fcm + server_key
         *  4.3 FCM sends notification
         * * * * * * * * * * * * * *
         * Inside message service
         * 5. Keep it as it is
         */
    }
    public void deleteNotification(String id) {
        userRepository.deleteNotificationFromDatabase(id);
    }
    public LiveData<HashMap<String, ArrayList<String>>> getUserNotificationActions() {
        userNotificationActions = Transformations.switchMap(userNotifications,
                notificationModels -> userRepository.getNotificationActions(notificationModels));
        return userNotificationActions;
    }

    public void addTokenToServerDatabase(String token) {
        userRepository.addTokenToServerDatabase(token);
    }
}
