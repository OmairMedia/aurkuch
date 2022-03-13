package com.basitobaid.youtubeapp.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;

import retrofit2.Call;
import retrofit2.Callback;

public class Global {


    public Global() {
    }

    public static void checkRemainingRewards(OnCountResult onCountResult) {
        Call<Response.CommonResponse> call = RestClient.getClient().getRemainingRewardCount(AppPreferences.getUserToken());
        call.enqueue(new Callback<Response.CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.CommonResponse> call,
                                   @NonNull retrofit2.Response<Response.CommonResponse> response) {
                if (response.body() != null) {
                    if (response.body().status.equals("200")) {
                        onCountResult.onSuccess();
                    } else {
                        onCountResult.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.CommonResponse> call,@NonNull Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                onCountResult.onFailure();
            }
        });
    }

    public interface OnCountResult {
        void onSuccess();
        void onFailure();
    }
}
