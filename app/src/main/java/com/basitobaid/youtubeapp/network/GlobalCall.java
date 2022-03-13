package com.basitobaid.youtubeapp.network;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.basitobaid.youtubeapp.utils.AppPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class GlobalCall {

    RewardCallback rewardCallback;
    UpdateWalletCallback updateWalletCallback;

    public GlobalCall(RewardCallback rewardCallback) {
        this.rewardCallback = rewardCallback;
    }

    public GlobalCall(UpdateWalletCallback updateWalletCallback) {
        this.updateWalletCallback = updateWalletCallback;
    }

    public void getRewardList(String id) {
        Call<Response.RewardResponse> call = RestClient.getClient().getRewards(AppPreferences.getUserToken(),id);
        call.enqueue(new Callback<Response.RewardResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.RewardResponse> call,
                                   @NonNull retrofit2.Response<Response.RewardResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    rewardCallback.getRewardList(response.body().data);
                } else {
                    rewardCallback.onEmpty();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.RewardResponse> call,
                                  @NonNull Throwable t) {
                rewardCallback.onError();
            }
        });
    }

    public void updateWallet(Model.Reward reward) {
        Call<Response.CommonResponse> call = RestClient.getClient().updateWallet(AppPreferences.getUserToken(),
                reward.rId, null);
        call.enqueue(new Callback<Response.CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.CommonResponse> call,
                                   @NonNull retrofit2.Response<Response.CommonResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().status.equals("200")) {
                            updateWalletCallback.onSuccess(reward.rewardAmount);
                        } else {
                            updateWalletCallback.onAlreadyGot(response.body().message);
                        }
                    } else {
                        updateWalletCallback.onAlreadyGot("Empty response");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.CommonResponse> call,
                                  @NonNull Throwable t) {
                updateWalletCallback.onUpdateError();
            }
        });
    }

    public interface UpdateWalletCallback {
        void onSuccess(String amount);
        void onAlreadyGot(String message);
        void onUpdateError();
    }

    public interface RewardCallback {
        void getRewardList(List<Model.Reward> rewards);
        void onError();
        void onEmpty();
    }
}
