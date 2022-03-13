package com.basitobaid.youtubeapp.network;

import com.basitobaid.youtubeapp.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestClient {
    private static IRestClient iRestClient;

    public static IRestClient getClient() {
        if (iRestClient == null) {
            OkHttpClient okHttpClient;

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);

            okHttpClient =new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit.Builder builder = new Retrofit.Builder();

            Retrofit client = builder.baseUrl(BuildConfig.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            iRestClient = client.create(IRestClient.class);

        }
        return iRestClient;
    }
}
