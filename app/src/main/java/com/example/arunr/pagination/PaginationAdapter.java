package com.example.arunr.pagination;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arun.r on 22-02-2018.
 */

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Result> movieResults;
    private Context context;

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";

    // flag for footer ProgressBar (i.e. last item of list)
    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context){
        this.context = context;
        movieResults = new ArrayList<>();
    }

    public List<Result> getMovies(){
        return movieResults;
    }

    public void setMovies(List<Result> movieResults){
        this.movieResults = movieResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater){
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){

        Result result = movieResults.get(position);     // Movie

        switch (getItemViewType(position)){
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                movieVH.title.setText(result.getTitle());
                movieVH.description.setText(result.getOverview());

                // Using Glide to handle image loading
                Glide.with(context)
                     .load(BASE_URL_IMG + result.getPosterPath())
                     .into(movieVH.poster);

                break;

            case LOADING:
                // Do nothing
                break;
        }
    }

    @Override
    public int getItemCount(){
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position){
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    // useful for added data fetched via Pagination
    public void add(Result result){
        movieResults.add(result);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<Result> moveResults){
        for (Result mc : moveResults){
            add(mc);
        }
    }

    public void remove(Result result){
        int positon = movieResults.indexOf(result);
        if (positon > -1){
            movieResults.remove(positon);
            notifyItemRemoved(positon);
        }
    }

    public void clear(){
        isLoadingAdded = false;
        while (getItemCount() > 0){
            remove(getItem(0));
        }
    }

    public boolean isEmpty(){
        return getItemCount() == 0;
    }

    public void addLoadingFooter(){
        isLoadingAdded = true;
        add(new Result());
    }

    public void removeLoadingFooter(){
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Result item = getItem(position);

        if (item != null){
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Result getItem(int position){
        return movieResults.get(position);
    }

    // View Holders
    protected class MovieVH extends RecyclerView.ViewHolder {
        private ImageView poster;
        private TextView title;
        private TextView description;

        public MovieVH(View itemView){
            super(itemView);

            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
