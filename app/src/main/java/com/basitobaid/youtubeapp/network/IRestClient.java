package com.basitobaid.youtubeapp.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRestClient {


    @FormUrlEncoded
    @POST(ApiPaths.REGISTER)
    Call<Response.LoginResponse> registerUser(@Field("email") String email,
                                              @Field("name") String name,
                                              @Field("mobile") String mobile,
                                              @Field("imei")String imei);

    @FormUrlEncoded
    @POST(ApiPaths.CHECK_DEVICE)
    Call<Response.LoginResponse> checkDevice(@Field("imei") String imei);

    @GET(ApiPaths.GET_CATEGORIES)
    Call<Response.CategoryResponse> getCategories(@Header("Authorization") String token);

    @GET(ApiPaths.GET_REWARDS)
    Call<Response.RewardResponse> getRewards(@Header("Authorization") String token,
                                             @Query("cat_id") String catId);

    @FormUrlEncoded
    @POST(ApiPaths.UPDATE_WALLET)
    Call<Response.CommonResponse> updateWallet(@Header("Authorization") String token,
                                               @Field("reward_id") String rewardId,
                                               @Field("referal") String refCode);

    @GET(ApiPaths.CHECK_USER_REWARDS)
    Call<Response.CommonResponse> getRemainingRewardCount(@Header("Authorization") String token);

    @GET(ApiPaths.GET_WALLET)
    Call<Response.WalletResponse> getWallet(@Header("Authorization") String token,
                                            @Query("filter") String filterId);

    @GET(ApiPaths.GET_PROFILE)
    Call<Response.ProfileResponse> getProfile(@Header("Authorization") String token);

    @GET(ApiPaths.REQUEST_TRANSACTION)
    Call<Response.RedeemResponse> requestRedeem(@Header("Authorization") String token);

    @GET(ApiPaths.GET_TRANSACTION)
    Call<Response.TransactionResponse> getTransactions(@Header("Authorization") String token);

    @GET(ApiPaths.GET_CHAT)
    Call<Response.ChatResponse> getChat(@Header("Authorization") String token);

    @GET(ApiPaths.GET_NOTIFICATION)
    Call<Response.NotificationResponse> getNotifications(@Header("Authorization") String token);

    @GET(ApiPaths.GET_NOTIFICATION)
    Call<Response.CommonResponse> getReferralReward(@Header("Authorization") String token,
                                                    @Field("referral_code") String refCode);

    @GET(ApiPaths.UPDATE_NOTIFICATION)
    Call<Response.CommonResponse> updateNotification(@Header("Authorization") String token,
                                                     @Query("notificaton_id") String noteId);

    @FormUrlEncoded
    @POST(ApiPaths.SEND_MESSAGE)
    Call<Response.CommonResponse> sendMessage(@Header("Authorization") String token,
                                              @Field("message") String message,
                                              @Field("chat_id") String chatId);

    @FormUrlEncoded
    @POST(ApiPaths.SEND_MESSAGE)
    Call<Response.CommonResponse> updateMessage(@Header("Authorization") String token,
                                                @Field("message_id") String message);

    @FormUrlEncoded
    @POST(ApiPaths.DEDUCT_WALLET)
    Call<Response.CommonResponse> deductWallet(@Header("Authorization") String token,
                                                @Field("reward_id") String rewardId);

    @FormUrlEncoded
    @POST(ApiPaths.UPDATE_FCM_TOKEN)
    Call<Response.CommonResponse> updateFcmToken(@Header("Authorization") String token,
                                                 @Field("fcm_token") String fcmToken);

    @POST(ApiPaths.VERIFY_PHONE)
    Call<Response.CommonResponse> verifyPhoneNo(@Header("Authorization") String token);

    @GET(ApiPaths.GET_BANNER_IMAGES)
    Call<Response.BannerImagesResponse> getBannerImages();

    @GET(ApiPaths.GET_SETTINGS)
    Call<Response.SettingResponse> getSettings();


}
