package lk.calm.pasbaratheater01;

public class Movie {
    String name, createdBy, story;
//    int image;
    String imageUrl;
    Boolean isSelected = false;
    float rating;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
