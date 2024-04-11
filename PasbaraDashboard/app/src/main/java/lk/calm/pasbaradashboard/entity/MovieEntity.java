package lk.calm.pasbaradashboard.entity;

import java.util.List;

public class MovieEntity {
    private String title;
    private String description;
    private List<String> genre;
    private String duration;
    private String release_date;
    private String image;
    private String cast_and_crew;
    private String trailer;

    public MovieEntity() {
    }

    public MovieEntity(String title, String description, List<String> genre, String duration, String release_date, String image, String cast_and_crew, String trailer) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.release_date = release_date;
        this.image = image;
        this.cast_and_crew = cast_and_crew;
        this.trailer = trailer;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCast_and_crew() {
        return cast_and_crew;
    }

    public void setCast_and_crew(String cast_and_crew) {
        this.cast_and_crew = cast_and_crew;
    }
}
