package com.example.arunr.pagination;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by arun.r on 22-02-2018.
 */

public class MovieApi {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.themoviedb.org/3/")
                    .build();
        }
        return retrofit;
    }
}
