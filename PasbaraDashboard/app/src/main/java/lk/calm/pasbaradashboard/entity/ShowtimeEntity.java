package lk.calm.pasbaradashboard.entity;

import java.util.List;

public class ShowtimeEntity {
    private List<Object> showTimes;
    private String screen;
    private int pricing;
    private MovieEntity movie;
    private String layout;

    public ShowtimeEntity(List<Object> showTimes, String screen, int pricing, MovieEntity movie, String layout) {
        this.showTimes = showTimes;
        this.screen = screen;
        this.pricing = pricing;
        this.movie = movie;
        this.layout = layout;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public List<Object> getShowTimes() {
        return showTimes;
    }

    public void setShowTimes(List<Object> showTimes) {
        this.showTimes = showTimes;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public int getPricing() {
        return pricing;
    }

    public void setPricing(int pricing) {
        this.pricing = pricing;
    }

    public MovieEntity getMovie() {
        return movie;
    }

    public void setMovie(MovieEntity movie) {
        this.movie = movie;
    }
}
