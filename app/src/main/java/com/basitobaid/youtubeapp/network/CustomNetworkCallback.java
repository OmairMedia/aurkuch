package com.basitobaid.youtubeapp.network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CustomNetworkCallback<T extends com.basitobaid.youtubeapp.network.Response.CommonResponse> implements Callback<T> {
    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.code() != 200) {
            onFailure(call, new Throwable(response.message()));
            return;
        }

        final T resp = response.body();

        if (resp == null) {
            onFailure(call, new Throwable("Unknown error, empty body"));
            return;
        }

        if (!resp.status.equals("200")) {
            onFailure(call, new Throwable(resp.message));
            return;
        }

        onSuccess(resp);
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onError(t);
    }

    public abstract void onSuccess(T response);
    public abstract void onError(Throwable throwable);
}
