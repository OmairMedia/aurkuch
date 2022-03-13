package com.basitobaid.youtubeapp.activity;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.Constant;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.app_text)
    TextView appText;
    private Context context;
    private Activity activity;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};
    TelephonyManager telephonyManager;
    String imei, token;
    Handler handler;
    Runnable runnable;
    Animation bounceAnim;
    RelativeLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        context = SplashActivity.this;
        activity = SplashActivity.this;
        parent = findViewById(R.id.parent);
        getSettings();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        new Handler().postDelayed(this::getDeviceIme, 200);
//        setBounceAnimation();
//        runnable = () -> appText.startAnimation(bounceAnim);
        handler = new Handler();
        appText.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slideUpTransition();
            }
        }, 500);

    }

    private void slideUpTransition() {
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(1000);
        transition.addTarget(appText);
        TransitionManager.beginDelayedTransition(parent, transition);
        appText.setVisibility(View.VISIBLE);
    }

    private void setBounceAnimation() {
        bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce_animation);
        appText.startAnimation(bounceAnim);
        bounceAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                handler.postDelayed(runnable,300);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void getSettings() {
        Call<Response.SettingResponse> call = RestClient.getClient().getSettings();
        call.enqueue(new CustomNetworkCallback<Response.SettingResponse>() {
            @Override
            public void onSuccess(Response.SettingResponse response) {
                setDataInPrefs(response.data);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void setDataInPrefs(Model.Setting setting) {
        AppPreferences.getPrefernces(SplashActivity.this);
        AppPreferences.setTermsAndConditions(setting.termAndConditions);
        AppPreferences.setPrivacyPolicy(setting.privacyPolicy);
        AppPreferences.setTwitterUserid(setting.twitterId);
        AppPreferences.setTwitterUsername(setting.twitterUsername);
        AppPreferences.setFacebookLink(setting.facebookLink);
        AppPreferences.setInstaLink(setting.instagramLink);
        AppPreferences.setAboutUs(setting.aboutUs);
        AppPreferences.setYoutubeLink(setting.youtube);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Log.d("TAG", "onActivityResult: return data is null");
            return;
        }
        switch (requestCode) {
            case Constant.REQUEST_AUTHORIZATION:
                Log.d("TAG", "onActivityResult: after auth return");
                Log.d("TAG", "onActivityResult: " + data);
                if (resultCode == RESULT_OK) {

                    Log.d("TAG", "onActivityResult: result code ok");
                    new GetUserChannel(mCredential).execute();
                }
                break;
        }
    }

    private void startMainActivity() {
//        appText.clearAnimation();
//        bounceAnim.cancel();
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private boolean hasPhoneStatePermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE);
    }

    @AfterPermissionGranted(Constant.READ_PHONE_STATE_REQUEST)
    private void getDeviceIme() {
        if (hasPhoneStatePermission()) {
            imei = getDeviceImei();
            if (ConnectionManager.isDeviceOnline(context)) {
                checkDeviceAndAccount();
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            EasyPermissions.requestPermissions(SplashActivity.this,
                    "Phone state request",
                    Constant.READ_PHONE_STATE_REQUEST,
                    Manifest.permission.READ_PHONE_STATE);
        }
    }

    @SuppressLint("MissingPermission")
    private String getDeviceImei() {
        String deviceId;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
        //return Build.VERSION_CODES.O <= Build.VERSION.SDK_INT ? telephonyManager.getImei() : telephonyManager.getDeviceId();
    }

    private void startRegistrationActivity() {
        startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> list) {
        // Do nothing.
    }


    @Override
    public void onPermissionsDenied(int requestCode,
                                    @NonNull List<String> list) {
        // Do nothing.
    }

    private Model.User user;

    private void checkDeviceAndAccount() {
        Call<Response.LoginResponse> call = RestClient
                .getClient()
                .checkDevice(imei);
        call.enqueue(new Callback<Response.LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.LoginResponse> call,
                                   @NonNull retrofit2.Response<Response.LoginResponse> response) {
                if (response.code() == 200) {

                    assert response.body() != null;
                    if (response.body().status.equals("100")) {
                        startRegistrationActivity();
                    } else if (response.body().status.equals("200")) {
                        user = response.body().getData();
                        token = response.body().token;

                        checkPermissionForAccount();
//                        saveDataInPrefs(response.body().getData());

                    } else {
                        Toast.makeText(SplashActivity.this,
                                "You can not login with another account or your account will be banned",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SplashActivity.this,
                            "error occurred",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Response.LoginResponse> call,
                                  @NonNull Throwable t) {
                if (context != null)
                    Toast.makeText(SplashActivity.this,
                            "error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDataInPrefs(Model.User user) {
        AppPreferences.setUserAccountName(user.uEmail);
        AppPreferences.setUserAccountUserName(user.uName);
        AppPreferences.setUserPhoneNo(user.uPhoneNo);
        AppPreferences.setUserStatus(user.uStatus);
        AppPreferences.setUserPhoneIMEI(user.uPhoneImei);
        AppPreferences.setUserPhoneVerified(!(user.uPhoneVerified.isEmpty() || user.uPhoneVerified.equals("0")));
        AppPreferences.setUserEmailVerified(!(user.uEmailVerified.equals("0") || user.uEmailVerified.isEmpty()));
        AppPreferences.setUserReferalCode(user.uReferalCode);
        AppPreferences.setUserId(user.uId);
        AppPreferences.setUserToken(token);
        mCredential.setSelectedAccountName(user.uEmail);
        if (mCredential.getSelectedAccountName() != null) {
            new GetUserChannel(mCredential).execute();
        } else {
            Log.d("TAG", "saveDataInPrefs: accountname is null");
            mCredential.setSelectedAccount(new Account(user.uEmail, getApplicationContext().getPackageName()));
            new GetUserChannel(mCredential).execute();
        }
//        new GetUserChannel(mCredential).execute();

    }

    @AfterPermissionGranted(Constant.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void checkPermissionForAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            saveDataInPrefs(user);
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Constant.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetUserChannel extends AsyncTask<Void, Void, ChannelListResponse> {

        private YouTube youTubeDataApi;
        private Exception mLastError = null;

        public GetUserChannel(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            youTubeDataApi = new YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(Constant.APP_NAME)
                    .build();
        }

        ChannelListResponse response;

        @Override
        protected ChannelListResponse doInBackground(Void... voids) {
            try {
                response = youTubeDataApi.channels()
                        .list("snippet")
                        .setMine(true)
                        .execute();

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                Log.d("TAG", "doInBackground: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ChannelListResponse channelListResponse) {
            super.onPostExecute(channelListResponse);
            if (channelListResponse.getItems().size() > 0) {
                String userChannel = channelListResponse.getItems().get(0).getId();
                AppPreferences.setUserChannelId(userChannel);
                Log.d("TAG", "onPostExecute: " + userChannel);
            }
            startMainActivity();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Constant.REQUEST_AUTHORIZATION);
                } else {

                }
            } else {
                Log.d("TAG", "onCancelled: Request cancelled");
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                Log.d("TAG", "doInBackground: " + mLastError);
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         *
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<>();
            ChannelListResponse result = null;
            result = mService.channels().list("snippet,contentDetails,statistics")
                    .setForUsername("GoogleDevelopers")
                    .execute();
            List<Channel> channels = result.getItems();
            if (channels != null) {
                Channel channel = channels.get(0);
                channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                        "Its title is '" + channel.getSnippet().getTitle() + ", " +
                        "and it has " + channel.getStatistics().getViewCount() + " views.");

                Log.d("TAG", "getDataFromApi: " + "This channel's ID is " + channel.getId() + ". " +
                        "Its title is '" + channel.getSnippet().getTitle() + ", " +
                        "and it has " + channel.getStatistics().getViewCount() + " views.");
            } else {
                Log.d("TAG", "getDataFromApi: " + "Channel is null");
            }
            return channelInfo;
        }

        @Override
        protected void onPreExecute() {
//            mOutputText.setText("");
//            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
//            mProgress.hide();
            if (output == null || output.size() == 0) {
                Log.d("TAG", "No results returned.");
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");

                Log.d("TAG", TextUtils.join("\n", output));
            }
            startMainActivity();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.d("TAG", "onCancelled: " + "The following error occurred:\n"
                            + mLastError.getMessage());
                    startMainActivity();

                }
            } else {
                Log.d("TAG", "onCancelled: Request cancelled");

            }
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                SplashActivity.this,
                connectionStatusCode,
                Constant.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
