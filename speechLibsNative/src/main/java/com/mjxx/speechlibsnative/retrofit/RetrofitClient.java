package com.mjxx.speechlibsnative.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private RetrofitService faceService;

    public RetrofitClient(String baseUrl) {

        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            faceService = retrofit.create(RetrofitService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public RetrofitService getRetrofitService() {
        return this.faceService;
    }

}
