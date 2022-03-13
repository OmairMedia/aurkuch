package com.basitobaid.youtubeapp.apiCalls;

import android.os.AsyncTask;

import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.Constant;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SubscriptionListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GetSubscriptions extends AsyncTask<String, Void, SubscriptionListResponse> {

    private YouTube youTubeDataApi;

    public GetSubscriptions(GoogleAccountCredential credential) throws GeneralSecurityException, IOException {
        HttpTransport transport =  AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        youTubeDataApi = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName(Constant.APP_NAME)
                .build();
    }

    private SubscriptionListResponse response = null;
    @Override
    protected SubscriptionListResponse doInBackground(String... params) {
        try {
            response = youTubeDataApi.subscriptions()
                    .list("snippet")
//                    .setChannelId(params[0])
//                    .setChannelId(AppPreferences.getUserChannelId())//user channel id
                    .setForChannelId(params[0])//channel id
                    .setMine(true)
                    .setFields("items(snippet(resourceId(channelId)))")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
