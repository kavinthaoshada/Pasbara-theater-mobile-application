package lk.calm.pasbaratheater01;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
//import com.infinityandroid.recyclerview.TvShow;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    public static final String TAG = BookingHistoryActivity.class.getName();
    private List<Movie> tvShows;
    private MovieListener tvShowListener;

    public MoviesAdapter(List<Movie> tvShows, MovieListener tvShowListener) {
        this.tvShows = tvShows;
        this.tvShowListener = tvShowListener;
        Log.i(TAG, "in adapter constructor :"+tvShows.size());
        Log.i(TAG, "in adapter constructor :"+tvShowListener);
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "in adapter onCreateViewHolder movie");
        return new MoviesViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_tv_shows,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.bindTvShow(tvShows.get(position));

        Movie movie = tvShows.get(position);

        Glide.with(holder.itemView.getContext())
                .load(movie.getImageUrl())
                .placeholder(R.drawable.ic_image_24)
                .error(R.drawable.ic_image_24)
                .into(holder.imageTvShow);
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    public List<Movie> getSelectedTvShows(){
        List<Movie> selectedTvShows = new ArrayList<>();
        for(Movie tvShow : tvShows){
            if(tvShow.isSelected){
                selectedTvShows.add(tvShow);
            }
        }
        return selectedTvShows;
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layoutTVShows;
        View viewBackground;
        RoundedImageView imageTvShow;
        TextView textName, textCreatedBy, textStory;
        RatingBar ratingBar;
        ImageView imageSelected;

        MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutTVShows = itemView.findViewById(R.id.layoutTVShows);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            imageTvShow = itemView.findViewById(R.id.imageTvShow);
            textName = itemView.findViewById(R.id.textName);
            textCreatedBy = itemView.findViewById(R.id.textCreateBy);
            textStory = itemView.findViewById(R.id.textStory);
            ratingBar = itemView.findViewById(R.id.ratinbar);
            imageSelected = itemView.findViewById(R.id.imageSelected);
        }

        void bindTvShow(final Movie tvShow){

            textName.setText(tvShow.name);
            textCreatedBy.setText(tvShow.createdBy);
            textStory.setText(tvShow.story);
            ratingBar.setRating(tvShow.rating);
            if(tvShow.isSelected){
                viewBackground.setBackgroundResource(R.drawable.tv_show_selected_backgound);
                imageSelected.setVisibility(View.VISIBLE);
            }else{
                viewBackground.setBackgroundResource(R.drawable.tv_show_background);
                imageSelected.setVisibility(View.GONE);
            }
            layoutTVShows.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tvShow.isSelected){
                        viewBackground.setBackgroundResource(R.drawable.tv_show_background);
                        imageSelected.setVisibility(View.GONE);
                        tvShow.isSelected = false;
                        if(getSelectedTvShows().size()==0){
                            tvShowListener.onTvShowAction(false, getSelectedTvShows().size());
                        }
                    }else{
                        viewBackground.setBackgroundResource(R.drawable.tv_show_selected_backgound);
                        imageSelected.setVisibility(View.VISIBLE);
                        tvShow.isSelected = true;
                        tvShowListener.onTvShowAction(true,getSelectedTvShows().size());
                    }
                }
            });
        }
    }
}
