package com.example.arunr.pagination;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    // Index for which pagination should start (0 is 1st page in our case)
    private static final int PAGE_START = 0;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load
    private int TOTAL_PAGES = 2;
    // indicates the current page which pagination is fetching
    private int currentPage = PAGE_START;

    private MovieService movieService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_recycler);
        progressBar = findViewById(R.id.main_progress);

        adapter = new PaginationAdapter(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;  // Increment page index to load the next one

                // mocking netwoek delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        // init service and load data
        // 1
        movieService = MovieApi.getClient(this).create(MovieService.class);
        loadFirstPage();
    }

    private void loadFirstPage(){
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                List<Result> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                // handle error
            }
        });
    }

    private void loadNextPage(){
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                List<Result> results = fetchResults(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                // handle failure
            }
        });
    }

    // Extracts List<Result> from response
    private List<Result> fetchResults(Response<TopRatedMovies> response){
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }

    // Performs a retrofit call to the top rated movies
    private retrofit2.Call<TopRatedMovies> callTopRatedMoviesApi(){ //
        return movieService.getTopRatedMovies(
                getString(R.string.my_api_key)
        );
    }
}
