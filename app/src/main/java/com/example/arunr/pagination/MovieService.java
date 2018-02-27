package com.example.arunr.pagination;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by arun.r on 22-02-2018.
 */

public interface MovieService {

    @GET("movie/top_rated")
    Call<TopRatedMovies> getTopRatedMovies(
            @Query("api_key") String apiKey
    );
}
