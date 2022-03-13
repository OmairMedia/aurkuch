package com.basitobaid.youtubeapp.firebase;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.basitobaid.youtubeapp.activity.MainActivity;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.MyNotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;

public class FirebaseNotificationService extends FirebaseMessagingService {

    private String message, title, image, type;
    String CHANNEL_ID = "my_aur_kuch_channel";
    private Model.Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences.getPrefernces(getApplicationContext());
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG", "onMessageReceived: " + remoteMessage.getData());
            title = remoteMessage.getData().get("notification_title");
            message = remoteMessage.getData().get("notification_message");
            type = remoteMessage.getData().get("notification_type");
//                title = remoteMessage.getNotification().getTitle();
//                message = remoteMessage.getNotification().getBody();
//            notification = new Gson()
//                    .fromJson(remoteMessage.getData().get("news"), new TypeToken<Model.Notification>() {}.getType());

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("from", "notify");
            intent.putExtra("type", type);
            mNotificationManager.showSmallNotification(title, message, intent, CHANNEL_ID);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        AppPreferences.setFcmToken(s);
        Call<Response.CommonResponse> call = RestClient.getClient().updateFcmToken(AppPreferences.getUserToken(), s);
        call.enqueue(new CustomNetworkCallback<Response.CommonResponse>() {
            @Override
            public void onSuccess(Response.CommonResponse response) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
