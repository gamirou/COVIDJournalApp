package com.gamiro.covidjournal.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

// Don't forget to add the request
public interface AppServerService {

    @FormUrlEncoded
    @POST("fcmsend/insert_token.php")
    Call<ResponseBody> sendTokenToDatabase(
            @Field("firebaseID") String firebaseID,
            @Field("deviceToken") String deviceToken,
            @Field("request") String request
    );

    @FormUrlEncoded
    @POST("fcmsend/delete_token.php")
    Call<ResponseBody> deleteTokenFromDatabase(
            @Field("firebaseID") String firebaseID,
            @Field("deviceToken") String deviceToken,
            @Field("request") String request
    );

//    @FormUrlEncoded
//    @POST("fcmsend/send_notification.php")
//    Call<ResponseBody> sendNotification(@Body RootModel root);
//
    @FormUrlEncoded
    @POST("fcmsend/send_notification.php")
    Call<ResponseBody> sendNotification(
            @Field("userID") String userID,
            @Field("title") String title,
            @Field("body") String body,
            @Field("notificationAction") String action,
            @Field("request") String request
    );
}
