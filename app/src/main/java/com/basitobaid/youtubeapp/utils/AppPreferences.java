package com.basitobaid.youtubeapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class AppPreferences {

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_USER_TOKEN = "token";
    private static final String PREF_ACCOUNT_USER_NAME = "accountUserName";
    private static final String PREF_ACCOUNT_USER_PHONE_NO = "mobileNo";
    private static final String PREF_ACCOUNT_USER_PHONE_IMEI = "mobileIMEI";
    private static final String PREF_ACCOUNT_PHONE_VERIFIED = "phoneVerified";
    private static final String PREF_ACCOUNT_EMAIL_VERIFIED = "emailVerified";
    private static final String PREF_ACCOUNT_REFERAL_CODE = "referalCode";
    private static final String PREF_ACCOUNT_USER_ID = "userId";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private AppPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public static void getPrefernces(Context context) {
        if (sharedPreferences == null) {
            new AppPreferences(context);
        }
    }

    public static void setUserToken(String token) {
        editor.putString(PREF_USER_TOKEN, token);
        editor.apply();
    }

    public static String getUserToken() {
        return sharedPreferences.getString(PREF_USER_TOKEN, null);
    }

    public static void setUserAccountName(String accountName) {
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    public static String getUserAccountname() {
        return sharedPreferences.getString(PREF_ACCOUNT_NAME, null);
    }

    public static void setUserAccountUserName(String accountName) {
        editor.putString(PREF_ACCOUNT_USER_NAME, accountName);
        editor.apply();
    }

    public static String getUserAccountUserName() {
        return sharedPreferences.getString(PREF_ACCOUNT_USER_NAME, null);
    }

    public static void setUserPhoneNo(String value) {
        editor.putString(PREF_ACCOUNT_USER_PHONE_NO, value);
        editor.apply();
    }

    public static String getUserPhoneNo() {
        return sharedPreferences.getString(PREF_ACCOUNT_USER_PHONE_NO, null);
    }

    public static void setUserPhoneIMEI(String value) {
        editor.putString(PREF_ACCOUNT_USER_PHONE_IMEI, value);
        editor.apply();
    }

    public static String getUserPhoneIMEI() {
        return sharedPreferences.getString(PREF_ACCOUNT_USER_PHONE_IMEI, null);
    }

    public static void setUserStatus(String value) {
        editor.putString(PREF_ACCOUNT_USER_PHONE_IMEI, value);
        editor.apply();
    }

    public static String getUserStatus() {
        return sharedPreferences.getString(PREF_ACCOUNT_USER_PHONE_IMEI, null);
    }

    public static void setUserEmailVerified(boolean value) {
        editor.putBoolean(PREF_ACCOUNT_EMAIL_VERIFIED, value);
        editor.apply();
    }

    public static boolean getUserEmailVerified() {
        return sharedPreferences.getBoolean(PREF_ACCOUNT_EMAIL_VERIFIED, false);
    }

    public static void setUserPhoneVerified(boolean value) {
        editor.putBoolean(PREF_ACCOUNT_PHONE_VERIFIED, value);
        editor.apply();
    }

    public static boolean getUserPhoneVerified() {
        return sharedPreferences.getBoolean(PREF_ACCOUNT_PHONE_VERIFIED, false);
    }

    public static void setUserReferalCode(String value) {
        editor.putString(PREF_ACCOUNT_REFERAL_CODE, value);
        editor.apply();
    }

    public static String getUserReferalCode() {
        return sharedPreferences.getString(PREF_ACCOUNT_REFERAL_CODE, null);
    }

    public static void setUserId(String value) {
        editor.putString(PREF_ACCOUNT_USER_ID, value);
        editor.apply();
    }

    public static String getUserId() {
        return sharedPreferences.getString(PREF_ACCOUNT_USER_ID, null);
    }

    public static void setUserChannelId(String channelId) {
        editor.putString("channelId", channelId);
        editor.apply();
    }

    public static String getUserChannelId() {
        return sharedPreferences.getString("channelId", null);
    }


    public static String getPrivacyPolicy() {
        return sharedPreferences.getString("privacyPolicy", null);
    }

    public static void setPrivacyPolicy(String value) {
        editor.putString("privacyPolicy", value);
        editor.apply();
    }

    public static String getTermsAndConditions() {
        return sharedPreferences.getString("termsAndConditions", null);
    }

    public static void setTermsAndConditions(String value) {
        editor.putString("termsAndConditions", value);
        editor.apply();
    }

    public static String getFacebookLink() {
        return sharedPreferences.getString("fbLink", null);
    }

    public static void setFacebookLink(String value) {
        editor.putString("fbLink", value);
        editor.apply();
    }

    public static String getTwitterUserid() {
        return sharedPreferences.getString("twitterId", null);
    }

    public static void setTwitterUserid(String value) {
        editor.putString("twitterId", value);
        editor.apply();
    }

    public static String getTwitterUsername() {
        return sharedPreferences.getString("twitterName", null);
    }

    public static void setTwitterUsername(String value) {
        editor.putString("twitterName", value);
        editor.apply();
    }

    public static String getInstaLink() {
        return sharedPreferences.getString("instaLink", null);
    }

    public static void setInstaLink(String value) {
        editor.putString("instaLink", value);
        editor.apply();
    }

    public static String getYoutubeLink() {
        return sharedPreferences.getString("youtubeLink", null);
    }

    public static void setYoutubeLink(String value) {
        editor.putString("youtubeLink", value);
        editor.apply();
    }

    public static String getAboutUs() {
        return sharedPreferences.getString("aboutUs", null);
    }

    public static void setAboutUs(String value) {
        editor.putString("aboutUs", value);
        editor.apply();
    }

    public static boolean getFirstTime() {
        return sharedPreferences.getBoolean("firstTime", true);
    }

    public static void setFirstTime(boolean value) {
        editor.putBoolean("firstTime", value);
        editor.apply();
    }

    public static void setFcmToken(String fcmToken) {
        editor.putString("fcm", fcmToken);
        editor.apply();
    }

    public static String getFcmToken() {
        return sharedPreferences.getString("fcm", null);
    }

    public static void setDailyRewardsLeft(int rewardsLeft) {
        editor.putInt("left", rewardsLeft);
        editor.apply();
    }

    public static int getDailyRewardsLeft() {
        return sharedPreferences.getInt("left", -1);
    }


}
